package mz.org.csaude.comvida.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.PatientImportFileDTO;
import mz.org.csaude.comvida.backend.dto.SheetImportStatusDTO;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.service.PatientImportFileService;
import mz.org.csaude.comvida.backend.service.SheetImportStatusService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.PATIENT_IMPORT_CONTROLLER)
public class PatientImportFileController extends BaseController {

    @Inject
    private PatientImportFileService importService;

    @Inject
    private SheetImportStatusService sheetImportStatusService;

    @Operation(summary = "Upload cohort Excel", description = "Recebe ficheiro Excel e metadados para importar dados de cohort")
    @ApiResponse(responseCode = "200", description = "Upload e processamento concluídos com sucesso")
    @Post(uri = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> uploadExcel(
            @Part("file") CompletedFileUpload file,
            @Part("dto") String rawDtoJson, // o frontend envia o DTO como JSON string
            Authentication authentication
    ) {
        try {
            // Convertemos a string JSON recebida para o DTO
            ObjectMapper mapper = new ObjectMapper();
            PatientImportFileDTO dto = mapper.readValue(rawDtoJson, PatientImportFileDTO.class);
            String userUuid = (String) authentication.getAttributes().get("userUuid");
            importService.processExcelUpload(file, dto, userUuid, dto.getSourceSystem().getId());

            return HttpResponse.ok("Ficheiro enviado e processado com sucesso.");
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<PatientImportFile> getById(@PathVariable Long id) {
        Optional<PatientImportFile> result = importService.findById(id);
        return result.map(HttpResponse::ok).orElse(HttpResponse.notFound());
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


    @Operation(summary = "List or search")
    @Get
    public HttpResponse<Page<PatientImportFileDTO>> listPaginated(
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size,
            @QueryValue("status") List<String> statuses,
            @QueryValue(defaultValue = "") String name
    ) {
        Pageable pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc("createdAt")));
        Page<PatientImportFileDTO> pagedResult = importService.findAllPaginated(statuses, name, pageable);
        return HttpResponse.ok(pagedResult);
    }

    @Get("/{id}/sheets")
    public List<SheetImportStatusDTO> getSheetStatuses(Long id) {
        PatientImportFile file = importService.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado"));

        return sheetImportStatusService.findByPatientImportFile(file)
                .stream()
                .map(SheetImportStatusDTO::new)
                .collect(Collectors.toList());
    }

}
