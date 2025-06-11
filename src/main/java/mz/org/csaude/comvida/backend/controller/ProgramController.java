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
import mz.org.csaude.comvida.backend.dto.ProgramDTO;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.service.ProgramService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PROGRAM_CONTROLLER)
@Tag(name = "Program", description = "API for managing programs")
public class ProgramController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramController.class);

    @Inject
    private ProgramService programService;

    @Operation(summary = "List or search programs by name (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<Program> programs = !Utilities.stringHasValue(name)
                ? programService.findAll(resolvePageable(pageable))
                : programService.searchByName(name, resolvePageable(pageable));

        List<ProgramDTO> programDTOs = programs.getContent().stream()
                .map(ProgramDTO::new)
                .collect(Collectors.toList());

        String message = programs.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(
                        programDTOs, // sempre passar lista (mesmo vazia)
                        programs.getTotalSize(),
                        programs.getPageable(),
                        message
                )
        );
    }



    @Operation(summary = "Get program by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<Program> optional = programService.findById(id);
        return optional.map(program ->
                HttpResponse.ok(SuccessResponse.of("Programa encontrado com sucesso", new ProgramDTO(program)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new program")
    @Post
    public HttpResponse<?> create(@Body ProgramDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Program program = dto.toEntity();
        program.setCreatedBy(userUuid);
        Program created = programService.create(program);
        return HttpResponse.created(SuccessResponse.of("Programa criado com sucesso", new ProgramDTO(created)));
    }

    @Operation(summary = "Update an existing program")
    @Put
    public HttpResponse<?> update(@Body ProgramDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Program program = dto.toEntity();
        program.setUpdatedBy(userUuid);
        Program updated = programService.update(program);
        return HttpResponse.ok(SuccessResponse.of("Programa atualizado com sucesso", new ProgramDTO(updated)));
    }

    @Operation(summary = "Delete a program by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        programService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Programa eliminado com sucesso"));
    }

    @Operation(summary = "Activate or deactivate a program by changing its LifeCycleStatus")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        Program updatedProgram = programService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado do programa atualizado com sucesso", new ProgramDTO(updatedProgram)));
    }

}
