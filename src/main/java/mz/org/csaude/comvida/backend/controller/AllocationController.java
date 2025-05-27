package mz.org.csaude.comvida.backend.controller;

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
import mz.org.csaude.comvida.backend.entity.Allocation;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.AllocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.ALLOCATION_CONTROLLER)
@Tag(name = "Allocation", description = "API for managing allocations")
public class AllocationController {

    @Inject
    private AllocationService allocationService;

    public static final Logger LOG = LoggerFactory.getLogger(AllocationController.class);

//    @Operation(summary = "Retrieve all allocations with pagination")
//    @ApiResponse(responseCode = "200", description = "Allocations retrieved successfully")
//    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ComVidaAPIError.class)))
//    @Get
//    public HttpResponse<?> getAll(@Nullable Pageable pageable) {
//        try {
//            Page<AllocationDTO> allocations = allocationService.findAll(pageable);
//            return HttpResponse.ok(allocations);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

    @Operation(summary = "Find allocation by ID")
    @ApiResponse(responseCode = "200", description = "Allocation found")
    @ApiResponse(responseCode = "404", description = "Allocation not found")
    @Get("/getById/{id}")
    public HttpResponse<?> findById(@PathVariable("id") Long id) {
        try {
            Optional<Allocation> allocation = allocationService.findById(id);
            return allocation.map(HttpResponse::ok)
                    .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
                    .status(HttpStatus.BAD_REQUEST.getCode())
                    .error(e.getLocalizedMessage())
                    .message(e.getMessage()).build());
        }
    }

//    @Operation(summary = "Search allocations by status")
//    @ApiResponse(responseCode = "200", description = "Allocations retrieved successfully")
//    @ApiResponse(responseCode = "400", description = "Bad request")
//    @Get("/search")
//    public HttpResponse<?> searchByStatus(@Nullable @QueryValue("status") String status,
//                                          Pageable pageable) {
//        try {
//            Page<AllocationDTO> allocations = allocationService.search(status, pageable);
//            return HttpResponse.ok(allocations);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(APIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Save or update allocation")
//    @ApiResponse(responseCode = "201", description = "Allocation saved or updated successfully")
//    @ApiResponse(responseCode = "400", description = "Bad request")
//    @Post("/saveOrUpdate")
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body AllocationDTO allocationDTO, Authentication authentication) {
//        try {
//            AllocationDTO saved = allocationService.saveOrUpdate((Long) authentication.getAttributes().get("userInfo"), allocationDTO);
//            LOG.info("Saved allocation {}", saved);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(APIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Delete allocation by ID")
//    @ApiResponse(responseCode = "200", description = "Allocation deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Allocation not found")
//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable("id") Long id) {
//        try {
//            Optional<Allocation> allocation = allocationService.findById(id);
//            if (allocation.isPresent()) {
//                allocationService.destroy(allocation.get());
//                LOG.info("Deleted Allocation with ID {}", id);
//                return HttpResponse.ok();
//            } else {
//                return HttpResponse.notFound();
//            }
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(
//                    APIError.builder()
//                            .status(HttpStatus.BAD_REQUEST.getCode())
//                            .error(e.getLocalizedMessage())
//                            .message(e.getMessage())
//                            .build()
//            );
//        }
//    }
}
