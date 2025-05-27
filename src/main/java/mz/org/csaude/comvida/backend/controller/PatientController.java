package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PATIENT_CONTROLLER)
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController {

    @Inject
    private PatientService patientService;

    public static final Logger LOG = LoggerFactory.getLogger(PatientController.class);

//    @Operation(summary = "Retrieve all patients with pagination")
//    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
//    @Get
//    public HttpResponse<?> getAll(@Nullable Pageable pageable) {
//        try {
//            Page<PatientDTO> patients = patientService.findAll(pageable);
//            return HttpResponse.ok(patients);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Find patient by ID")
//    @ApiResponse(responseCode = "200", description = "Patient found")
//    @ApiResponse(responseCode = "404", description = "Patient not found")
//    @Get("/getById/{id}")
//    public HttpResponse<?> findById(@PathVariable("id") Long id) {
//        try {
//            Optional<Patient> patient = patientService.findById(id);
//            return patient.map(HttpResponse::ok)
//                    .orElse(HttpResponse.notFound());
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Search patients by status")
//    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
//    @Get("/search")
//    public HttpResponse<?> search(@Nullable @QueryValue("status") String status,
//                                  @Nullable Pageable pageable) {
//        try {
//            Page<PatientDTO> patients = patientService.search(status, pageable);
//            return HttpResponse.ok(patients);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Save or update a patient")
//    @ApiResponse(responseCode = "201", description = "Patient saved or updated successfully")
//    @Post("/saveOrUpdate")
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body PatientDTO patientDTO, Authentication authentication) {
//        try {
//            PatientDTO saved = patientService.saveOrUpdate((Long) authentication.getAttributes().get("userInfo"), patientDTO);
//            LOG.info("Saved patient {}", saved);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Delete a patient by ID")
//    @ApiResponse(responseCode = "200", description = "Patient deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Patient not found")
//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable("id") Long id) {
//        try {
//            Optional<Patient> patient = patientService.findById(id);
//            if (patient.isPresent()) {
//                patientService.destroy(patient.get());
//                LOG.info("Deleted Patient with ID {}", id);
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
