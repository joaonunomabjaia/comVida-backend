package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.dto.PersonDTO;
import mz.org.csaude.comvida.backend.entity.Person;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PERSON_CONTROLLER)
@Tag(name = "Person", description = "API for managing persons")
public class PersonController {

    @Inject
    private PersonService personService;

    public static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

//    @Operation(summary = "Retrieve all persons with pagination")
//    @ApiResponse(responseCode = "200", description = "Persons retrieved successfully")
//    @Get
//    public HttpResponse<?> getAll(@Nullable Pageable pageable) {
//        try {
//            Page<PersonDTO> persons = personService.findAll(pageable);
//            return HttpResponse.ok(persons);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

    @Operation(summary = "Find person by ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @Get("/getById/{id}")
    public HttpResponse<?> findById(@PathVariable("id") Long id) {
        try {
            Optional<Person> person = personService.findById(id);
            return person.map(HttpResponse::ok)
                    .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
                    .status(HttpStatus.BAD_REQUEST.getCode())
                    .error(e.getLocalizedMessage())
                    .message(e.getMessage()).build());
        }
    }

//    @Operation(summary = "Search persons by sex")
//    @ApiResponse(responseCode = "200", description = "Persons retrieved successfully")
//    @Get("/search")
//    public HttpResponse<?> search(@Nullable @QueryValue("sex") String sex,
//                                  @Nullable Pageable pageable) {
//        try {
//            Page<PersonDTO> persons = personService.search(sex, pageable);
//            return HttpResponse.ok(persons);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Save or update a person")
//    @ApiResponse(responseCode = "201", description = "Person saved or updated successfully")
//    @Post("/saveOrUpdate")
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body PersonDTO personDTO, Authentication authentication) {
//        try {
//            PersonDTO saved = personService.saveOrUpdate((Long) authentication.getAttributes().get("userInfo"), personDTO);
//            LOG.info("Saved person {}", saved);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            return HttpResponse.badRequest().body(ComVidaAPIError.builder()
//                    .status(HttpStatus.BAD_REQUEST.getCode())
//                    .error(e.getLocalizedMessage())
//                    .message(e.getMessage()).build());
//        }
//    }

//    @Operation(summary = "Delete a person by ID")
//    @ApiResponse(responseCode = "200", description = "Person deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Person not found")
//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable("id") Long id) {
//        try {
//            Optional<Person> person = personService.findById(id);
//            if (person.isPresent()) {
//                personService.destroy(person.get());
//                LOG.info("Deleted Person with ID {}", id);
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
