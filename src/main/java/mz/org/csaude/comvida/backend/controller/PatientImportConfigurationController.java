package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.PatientImportConfiguration;
import mz.org.csaude.comvida.backend.service.PatientImportConfigurationService;

import java.util.List;
import java.util.Optional;


@Controller(RESTAPIMapping.IMPORT_CONFIGURATION_CONTROLLER)
public class PatientImportConfigurationController {

    private final PatientImportConfigurationService service;

    public PatientImportConfigurationController(PatientImportConfigurationService service) {
        this.service = service;
    }

    @Get
    public List<PatientImportConfiguration> listAll() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<PatientImportConfiguration> getById(@PathVariable Long id) {
        Optional<PatientImportConfiguration> config = service.findById(id);
        return config.map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

    @Post
    public HttpResponse<PatientImportConfiguration> create(@Body @Valid PatientImportConfiguration config) {
        return HttpResponse.created(service.save(config));
    }

    @Put("/{id}")
    public HttpResponse<PatientImportConfiguration> update(@PathVariable Long id, @Body @Valid PatientImportConfiguration config) {
        return service.findById(id)
                .map(existing -> {
                    config.setId(id);
                    return HttpResponse.ok(service.save(config));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return HttpResponse.noContent();
    }
}
