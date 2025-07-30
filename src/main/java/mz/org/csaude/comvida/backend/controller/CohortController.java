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
import mz.org.csaude.comvida.backend.dto.CohortDTO;
import mz.org.csaude.comvida.backend.dto.LifeCycleStatusDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.service.CohortService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.COHORT_CONTROLLER)
@Tag(name = "Cohort", description = "API for managing cohorts")
public class CohortController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CohortController.class);

    @Inject
    private CohortService service;

    @Operation(summary = "List or search cohorts by name (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<Cohort> cohorts = Utilities.stringHasValue(name)
                ? service.searchByName(name, resolvePageable(pageable))
                : service.findAll(resolvePageable(pageable));

        List<CohortDTO> cohortDTOs = cohorts.getContent().stream()
                .map(CohortDTO::new)
                .collect(Collectors.toList());

        String message = cohorts.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(cohortDTOs, cohorts.getTotalSize(), cohorts.getPageable(), message)
        );
    }

    @Operation(summary = "Get cohort by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<Cohort> optional = service.findById(id);
        return optional.map(cohort ->
                HttpResponse.ok(SuccessResponse.of("Coorte encontrada com sucesso", new CohortDTO(cohort)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new cohort")
    @Post
    public HttpResponse<?> create(@Body CohortDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Cohort cohort = dto.toEntity();
        cohort.setCreatedBy(userUuid);
        Cohort created = service.create(cohort);
        return HttpResponse.created(SuccessResponse.of("Coorte criada com sucesso", new CohortDTO(created)));
    }

    @Operation(summary = "Update an existing cohort")
    @Put
    public HttpResponse<?> update(@Body CohortDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Cohort cohort = dto.toEntity();
        cohort.setUpdatedBy(userUuid);
        Cohort updated = service.update(cohort);
        return HttpResponse.ok(SuccessResponse.of("Coorte atualizada com sucesso", new CohortDTO(updated)));
    }

    @Operation(summary = "Delete a cohort by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        service.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Coorte eliminada com sucesso"));
    }

    @Operation(summary = "List cohorts by Program Activity ID (paginated)")
    @Get("/by-program-activity/{programActivityId}")
    public HttpResponse<?> listByProgramActivity(@PathVariable Long programActivityId,
                                                 @Nullable Pageable pageable) {
        Page<Cohort> cohorts = service.findByProgramActivityId(programActivityId, resolvePageable(pageable));

        List<CohortDTO> cohortDTOs = cohorts.getContent().stream()
                .map(CohortDTO::new)
                .collect(Collectors.toList());

        String message = cohorts.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(cohortDTOs, cohorts.getTotalSize(), cohorts.getPageable(), message)
        );
    }

    @Operation(summary = "Activate or deactivate a cohort by changing its LifeCycleStatus")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        Cohort updatedCohort = service.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado da coorte atualizado com sucesso", new CohortDTO(updatedCohort)));
    }

}
