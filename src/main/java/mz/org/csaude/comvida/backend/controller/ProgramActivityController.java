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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.ProgramActivityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.ProgramActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/program-activities")
@Tag(name = "ProgramActivity", description = "API for managing program activities")
public class ProgramActivityController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramActivityController.class);

    @Inject
    private ProgramActivityService service;

    @Operation(summary = "List or search program activities by name (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        try {
            Page<ProgramActivity> result = name.isBlank()
                    ? service.findAll(resolvePageable(pageable))
                    : service.searchByName(name, resolvePageable(pageable));

            return HttpResponse.ok(result.map(ProgramActivityDTO::new));
        } catch (Exception e) {
            LOG.error("Error listing/searching activities: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Get program activity by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        try {
            Optional<ProgramActivity> activity = service.findById(id);
            return activity.map(value -> HttpResponse.ok(new ProgramActivityDTO(value)))
                           .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error fetching activity by ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Create a new program activity")
    @Post
    public HttpResponse<?> create(@Body ProgramActivityDTO dto) {
        try {
            ProgramActivity created = service.create(dto.toEntity());
            return HttpResponse.created(new ProgramActivityDTO(created));
        } catch (Exception e) {
            LOG.error("Error creating activity: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Update an existing program activity")
    @Put
    public HttpResponse<?> update(@Body ProgramActivityDTO dto) {
        try {
            ProgramActivity updated = service.update(dto.toEntity());
            return HttpResponse.ok(new ProgramActivityDTO(updated));
        } catch (Exception e) {
            LOG.error("Error updating activity: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Delete a program activity by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        try {
            service.delete(uuid);
            return HttpResponse.noContent();
        } catch (Exception e) {
            LOG.error("Error deleting activity: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

}
