package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.api.response.PaginatedResponse;
import mz.org.csaude.comvida.backend.api.response.SuccessResponse;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.LifeCycleStatusDTO;
import mz.org.csaude.comvida.backend.dto.ProgramActivityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.service.ProgramActivityService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PROGRAM_ACTIVITY_CONTROLLER)
@Tag(name = "ProgramActivity", description = "API for managing program activities")
public class ProgramActivityController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramActivityController.class);

    @Inject
    private ProgramActivityService programActivityService;

    @Operation(summary = "List or search program activities by name (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<ProgramActivity> programActivities = !Utilities.stringHasValue(name)
                ? programActivityService.findAll(resolvePageable(pageable))
                : programActivityService.searchByName(name, resolvePageable(pageable));

        List<ProgramActivityDTO> activityDTOs = programActivities.getContent().stream()
                .map(ProgramActivityDTO::new)
                .collect(Collectors.toList());

        String message = programActivities.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(
                        activityDTOs,
                        programActivities.getTotalSize(),
                        programActivities.getPageable(),
                        message
                )
        );
    }

    @Operation(summary = "Get program activity by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<ProgramActivity> optional = programActivityService.findById(id);
        return optional.map(activity ->
                HttpResponse.ok(SuccessResponse.of("Atividade encontrada com sucesso", new ProgramActivityDTO(activity)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new program activity")
    @Post
    public HttpResponse<?> create(@Body ProgramActivityDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        ProgramActivity activity = dto.toEntity();
        activity.setCreatedBy(userUuid);
        ProgramActivity created = programActivityService.create(activity);
        return HttpResponse.created(SuccessResponse.of("Atividade criada com sucesso", new ProgramActivityDTO(created)));
    }

    @Operation(summary = "Update an existing program activity")
    @Put
    public HttpResponse<?> update(@Body ProgramActivityDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        ProgramActivity activity = dto.toEntity();
        activity.setUpdatedBy(userUuid);
        ProgramActivity updated = programActivityService.update(activity);
        return HttpResponse.ok(SuccessResponse.of("Atividade atualizada com sucesso", new ProgramActivityDTO(updated)));
    }

    @Operation(summary = "Delete a program activity by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        programActivityService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Atividade eliminada com sucesso"));
    }

    @Operation(summary = "Activate or deactivate a program activity by changing its LifeCycleStatus")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        ProgramActivity updatedActivity = programActivityService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado da atividade atualizado com sucesso", new ProgramActivityDTO(updatedActivity)));
    }
}
