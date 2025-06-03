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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.CohortDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.service.CohortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/cohorts")
@Tag(name = "Cohort", description = "API for managing cohorts")
public class CohortController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CohortController.class);

    @Inject
    private CohortService service;

    @Operation(summary = "List or search cohorts", description = "Returns a paginated list of cohorts. If a name is provided, performs a search.")
    @ApiResponse(responseCode = "200", description = "List of cohorts returned successfully")
    @Get
    public HttpResponse<?> listOrSearch(
            @Parameter(description = "Name to search for", example = "Gravidez")
            @QueryValue("name") String name,
            @Parameter(hidden = true) @Nullable Pageable pageable) {
        try {
            Page<Cohort> result = name.isBlank()
                    ? service.findAll(resolvePageable(pageable))
                    : service.searchByName(name, resolvePageable(pageable));

            return HttpResponse.ok(result.map(CohortDTO::new));
        } catch (Exception e) {
            LOG.error("Error listing/searching cohorts: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Get cohort by ID", description = "Fetch a cohort by its numeric ID")
    @ApiResponse(responseCode = "200", description = "Cohort found")
    @ApiResponse(responseCode = "404", description = "Cohort not found")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        try {
            Optional<Cohort> cohort = service.findById(id);
            return cohort.map(value -> HttpResponse.ok(new CohortDTO(value)))
                         .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error fetching cohort by ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Create a new cohort", description = "Creates a new cohort with name, description and associated program activity")
    @ApiResponse(responseCode = "201", description = "Cohort created successfully")
    @Post
    public HttpResponse<?> create(@Body CohortDTO dto) {
        try {
            Cohort created = service.create(dto.toEntity());
            return HttpResponse.created(new CohortDTO(created));
        } catch (Exception e) {
            LOG.error("Error creating cohort: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Update an existing cohort", description = "Updates a cohort's name, description and associated program activity")
    @ApiResponse(responseCode = "200", description = "Cohort updated successfully")
    @Put
    public HttpResponse<?> update(@Body CohortDTO dto) {
        try {
            Cohort updated = service.update(dto.toEntity());
            return HttpResponse.ok(new CohortDTO(updated));
        } catch (Exception e) {
            LOG.error("Error updating cohort: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Delete a cohort by UUID", description = "Deletes the cohort with the specified UUID")
    @ApiResponse(responseCode = "204", description = "Cohort deleted successfully")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        try {
            service.delete(uuid);
            return HttpResponse.noContent();
        } catch (Exception e) {
            LOG.error("Error deleting cohort: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "List cohorts by Program Activity ID", description = "Returns a paginated list of cohorts for the specified program activity.")
    @ApiResponse(responseCode = "200", description = "Cohorts retrieved successfully")
    @Get("/by-program-activity/{programActivityId}")
    public HttpResponse<?> listByProgramActivity(
            @Parameter(description = "ID of the ProgramActivity", required = true)
            @PathVariable Long programActivityId,
            @Parameter(hidden = true) @Nullable Pageable pageable) {
        try {
            Page<Cohort> result = service.findByProgramActivityId(programActivityId, resolvePageable(pageable));
            return HttpResponse.ok(result.map(CohortDTO::new));
        } catch (Exception e) {
            LOG.error("Error fetching cohorts by program activity ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

}
