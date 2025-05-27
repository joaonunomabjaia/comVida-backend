package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.dto.CohortDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.CohortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.COHORT_CONTROLLER)
@Tag(name = "Cohort", description = "API for managing cohorts")
public class CohortController {

    @Inject
    private CohortService cohortService;

    public static final Logger LOG = LoggerFactory.getLogger(CohortController.class);

//    @Operation(summary = "Retrieve all cohorts with pagination")
//    @ApiResponse(responseCode = "200", description = "Cohorts retrieved successfully")
//    @Get
//    public HttpResponse<?> getAll(@Nullable Pageable pageable) {
//        try {
//            Page<CohortDTO> cohorts = cohortService.findAll(pageable);
//            return HttpResponse.ok(cohorts);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

    @Operation(summary = "Find cohort by ID")
    @ApiResponse(responseCode = "200", description = "Cohort found")
    @ApiResponse(responseCode = "404", description = "Cohort not found")
    @Get("/getById/{id}")
    public HttpResponse<?> findById(@PathVariable("id") Long id) {
        try {
            Optional<Cohort> cohort = cohortService.findById(id);
            return cohort.map(HttpResponse::ok)
                    .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
                    .status(HttpStatus.BAD_REQUEST.getCode())
                    .error(e.getLocalizedMessage())
                    .message(e.getMessage()).build());
        }
    }

    @Get(value = "/{id}/template", produces = MediaType.APPLICATION_OCTET_STREAM)
    public HttpResponse<byte[]> downloadTemplate(@PathVariable Long id) {
        Optional<Cohort> cohort = cohortService.findById(id);
        return cohort.map(c -> HttpResponse.ok(c.getTemplateFile()))
                .orElse(HttpResponse.notFound());
    }

//    @Operation(summary = "Search cohorts by name")
//    @ApiResponse(responseCode = "200", description = "Cohorts retrieved successfully")
//    @Get("/search")
//    public HttpResponse<?> search(@Nullable @QueryValue("name") String name,
//                                  @Nullable Pageable pageable) {
//        try {
//            Page<CohortDTO> cohorts = cohortService.search(name, pageable);
//            return HttpResponse.ok(cohorts);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Save or update a cohort")
//    @ApiResponse(responseCode = "201", description = "Cohort saved or updated successfully")
//    @Post("/saveOrUpdate")
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body CohortDTO cohortDTO, Authentication authentication) {
//        try {
//            CohortDTO saved = cohortService.saveOrUpdate((Long) authentication.getAttributes().get("userInfo"), cohortDTO);
//            LOG.info("Saved cohort {}", saved);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Delete a cohort by ID")
//    @ApiResponse(responseCode = "200", description = "Cohort deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Cohort not found")
//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable("id") Long id) {
//        try {
//            Optional<Cohort> cohort = cohortService.findById(id);
//            if (cohort.isPresent()) {
//                cohortService.destroy(cohort.get());
//                LOG.info("Deleted Cohort with ID {}", id);
//                return HttpResponse.ok();
//            } else {
//                return HttpResponse.notFound();
//            }
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(
//                    ComVidaAPIError.builder()
//                            .status(HttpStatus.BAD_REQUEST.getCode())
//                            .error(e.getLocalizedMessage())
//                            .message(e.getMessage())
//                            .build()
//            );
//        }
//    }
}
