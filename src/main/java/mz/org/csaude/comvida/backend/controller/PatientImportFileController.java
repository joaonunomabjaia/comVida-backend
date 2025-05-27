package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.service.PatientImportFileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller(RESTAPIMapping.PATIENT_IMPORT_CONTROLLER)
public class PatientImportFileController {

    @Inject
    private PatientImportFileService importService;

    @Post(consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<PatientImportFile> uploadFile(@Part String fileName, @Part CompletedFileUpload file) throws IOException {
        return HttpResponse.ok(importService.save(fileName, file.getBytes()));
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<PatientImportFile> getById(@PathVariable Long id) {
        Optional<PatientImportFile> result = importService.findById(id);
        return result.map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

    @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
    public List<PatientImportFile> listAll() {
        return (List<PatientImportFile>) importService.findAll();
    }

    @Get(uri = "/{id}/status", produces = MediaType.TEXT_PLAIN)
    public MutableHttpResponse<PatientImportFile.ImportStatus> getStatus(@PathVariable Long id) {
        Optional<PatientImportFile> file = importService.findById(id);
        return file.map(f -> HttpResponse.ok(f.getStatus()))
                .orElse(HttpResponse.notFound());
    }

    @Post(uri = "/{id}/process", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<String> processFile(@PathVariable Long id) {
        boolean started = importService.processFileAsync(id);
        return started ? HttpResponse.ok("Processing started") : HttpResponse.notFound();
    }
}
