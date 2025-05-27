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
import mz.org.csaude.comvida.backend.dto.ProgramDTO;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PROGRAM_CONTROLLER)
@Tag(name = "Program", description = "API for managing programs")
public class ProgramController {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramController.class);

    @Inject
    private ProgramService programService;

//    @Operation(summary = "Get all programs", description = "Retrieves all programs with pagination")
//    @ApiResponse(responseCode = "200", description = "Programs retrieved successfully")
//    @Get
//    public HttpResponse<?> getAll(@Nullable Pageable pageable) {
//        try {
//            Page<ProgramDTO> result = programService.findAll(pageable);
//            return HttpResponse.ok(result);
//        } catch (Exception e) {
//            LOG.error("Error fetching programs: {}", e.getMessage(), e);
//            return buildErrorResponse(e);
//        }
//    }

    @Operation(summary = "Get program by ID", description = "Retrieves a program by its ID")
    @ApiResponse(responseCode = "200", description = "Program found")
    @ApiResponse(responseCode = "404", description = "Program not found")
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

//    @Operation(summary = "Create or update a program")
//    @ApiResponse(responseCode = "201", description = "Program created or updated successfully")
//    @Post
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body ProgramDTO dto, Authentication authentication) {
//        try {
//            Long userId = (Long) authentication.getAttributes().get("userInfo");
//            ProgramDTO saved = programService.saveOrUpdate(userId, dto);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error("Error saving/updating program: {}", e.getMessage(), e);
//            return buildErrorResponse(e);
//        }
//    }
//
//    @Operation(summary = "Delete a program by ID")
//    @ApiResponse(responseCode = "200", description = "Program deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Program not found")
//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable Long id) {
//        try {
//            Optional<Program> optional = programService.findById(id);
//            if (optional.isPresent()) {
//                programService.delete(optional.get());
//                return HttpResponse.ok();
//            } else {
//                return HttpResponse.notFound();
//            }
//        } catch (Exception e) {
//            LOG.error("Error deleting program: {}", e.getMessage(), e);
//            return buildErrorResponse(e);
//        }
//    }

    private HttpResponse<ComVidaAPIError> buildErrorResponse(Exception e) {
        return HttpResponse.badRequest(ComVidaAPIError.builder()
                .status(HttpStatus.BAD_REQUEST.getCode())
                .message(e.getMessage())
                .error(e.getLocalizedMessage())
                .build());
    }
}
