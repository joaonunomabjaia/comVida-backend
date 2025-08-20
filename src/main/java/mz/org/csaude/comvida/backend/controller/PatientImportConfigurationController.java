package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.api.response.PaginatedResponse;
import mz.org.csaude.comvida.backend.api.response.SuccessResponse;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.PatientImportConfigurationDTO;
import mz.org.csaude.comvida.backend.entity.PatientImportConfiguration;
import mz.org.csaude.comvida.backend.service.PatientImportConfigurationService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.IMPORT_CONFIGURATION_CONTROLLER)
@Tag(name = "PatientImportConfiguration", description = "API para gerenciar configurações de importação de pacientes")
public class PatientImportConfigurationController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(PatientImportConfigurationController.class);

    @Inject
    private PatientImportConfigurationService service;

    @Operation(summary = "Listar ou pesquisar configurações (paginado)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable Pageable pageable,
            @QueryValue("ProgramActivityId") @Nullable Long ProgramActivityId) {

        Page<PatientImportConfiguration> configs = service.findAll(resolvePageable(pageable));

        List<PatientImportConfigurationDTO> dtos = configs.getContent().stream()
                .map(PatientImportConfigurationDTO::new)
                .collect(Collectors.toList());

        String message = configs.getTotalSize() == 0
                ? "Sem dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(PaginatedResponse.of(dtos, configs.getTotalSize(), configs.getPageable(), message));
    }

    @Operation(summary = "Buscar configuração por ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<PatientImportConfiguration> optional = service.findById(id);
        return optional.map(config ->
                HttpResponse.ok(SuccessResponse.of("Configuração encontrada", new PatientImportConfigurationDTO(config)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Criar nova configuração")
    @Post
    public HttpResponse<?> create(@Body PatientImportConfigurationDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        PatientImportConfiguration entity = dto.toEntity();
        entity.setCreatedBy(userUuid);
        PatientImportConfiguration created = service.save(entity);
        return HttpResponse.created(SuccessResponse.of("Configuração criada com sucesso", new PatientImportConfigurationDTO(created)));
    }

//    @Operation(summary = "Atualizar configuração existente")
//    @Put
//    public HttpResponse<?> update(@Body PatientImportConfigurationDTO dto, Authentication authentication) {
//        String userUuid = (String) authentication.getAttributes().get("userUuid");
//        PatientImportConfiguration entity = dto.toEntity();
//        entity.setUpdatedBy(userUuid);
//        PatientImportConfiguration updated = service.save(entity);
//        return HttpResponse.ok(SuccessResponse.of("Configuração atualizada com sucesso", new PatientImportConfigurationDTO(updated)));
//    }

    @Operation(summary = "Eliminar configuração por ID")
    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return HttpResponse.ok(SuccessResponse.messageOnly("Configuração eliminada com sucesso"));
    }

    @Operation(summary = "Atualizar configuração existente")
    @Put
    public HttpResponse<?> update(@Body PatientImportConfigurationDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        PatientImportConfiguration entity = dto.toEntity();
        entity.setUpdatedBy(userUuid);
        PatientImportConfiguration updated = service.save(entity);
        return HttpResponse.ok(SuccessResponse.of("Configuração atualizada com sucesso", new PatientImportConfigurationDTO(updated)));
    }
}
