package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PATIENT_CONTROLLER)
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController extends BaseController {

    @Inject
    private PatientService patientService;

    public static final Logger LOG = LoggerFactory.getLogger(PatientController.class);

}
