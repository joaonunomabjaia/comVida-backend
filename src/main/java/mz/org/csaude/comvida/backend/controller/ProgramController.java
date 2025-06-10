package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.ProgramDTO;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.ProgramService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PROGRAM_CONTROLLER)
@Tag(name = "Program", description = "API for managing programs")
public class ProgramController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramController.class);

    @Inject
    private ProgramService programService;

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Operation(summary = "List or search programs by name (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");
        try {
            Page<Program> programs = !Utilities.stringHasValue(name)
                    ? programService.findAll(resolvePageable(pageable))
                    : programService.searchByName(name, resolvePageable(pageable));

            return HttpResponse.ok(programs.map(ProgramDTO::new));
        } catch (Exception e) {
            LOG.error("Error listing/searching programs: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }


    @Operation(summary = "Get program by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        try {
            Optional<Program> optional = programService.findById(id);
            return optional.map(program -> HttpResponse.ok(new ProgramDTO(program)))
                           .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error fetching program by ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Create a new program")
    @Post
    public HttpResponse<?> create(@Body ProgramDTO dto) {
        try {
            Program program = dto.toEntity();
            Program created = programService.create(program);
            return HttpResponse.created(new ProgramDTO(created));
        } catch (Exception e) {
            LOG.error("Error creating program: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Update an existing program")
    @Put
    public HttpResponse<?> update(@Body ProgramDTO dto) {
        try {
            Program program = dto.toEntity();
            Program updated = programService.update(program);
            return HttpResponse.ok(new ProgramDTO(updated));
        } catch (Exception e) {
            LOG.error("Error updating program: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Delete a program by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        try {
            programService.delete(uuid);
            return HttpResponse.noContent();
        } catch (Exception e) {
            LOG.error("Error deleting program: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

}
