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
import mz.org.csaude.comvida.backend.dto.EligibilityCriteriaDTO;
import mz.org.csaude.comvida.backend.dto.LifeCycleStatusDTO;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import mz.org.csaude.comvida.backend.service.EligibilityCriteriaService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.ELIGIBILITY_CRITERIA_CONTROLLER)
@Tag(name = "EligibilityCriteria", description = "API para gerir critérios de elegibilidade")
public class EligibilityCriteriaController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(EligibilityCriteriaController.class);

    @Inject
    private EligibilityCriteriaService service;

    @Operation(summary = "Listar ou pesquisar critérios de elegibilidade por nome (paginado)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("criteria") String criteria,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<EligibilityCriteria> page = Utilities.stringHasValue(criteria)
                ? service.searchByCriteria(criteria, resolvePageable(pageable))
                : service.findAll(resolvePageable(pageable));

        List<EligibilityCriteriaDTO> dtos = page.getContent().stream()
                .map(EligibilityCriteriaDTO::new)
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0
                ? "Sem dados para esta pesquisa"
                : "Dados encontrados com sucesso";

        return HttpResponse.ok(PaginatedResponse.of(dtos, page.getTotalSize(), page.getPageable(), message));
    }

    @Operation(summary = "Buscar critério de elegibilidade por ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<EligibilityCriteria> result = service.findById(id);
        return result.map(eligibilityCriteria ->
                HttpResponse.ok(SuccessResponse.of("Critério encontrado com sucesso", new EligibilityCriteriaDTO(eligibilityCriteria)))
        ).orElse(HttpResponse.notFound());
    }

//    @Operation(summary = "Criar novo critério de elegibilidade")
//    @Post
//    public HttpResponse<?> create(@Body EligibilityCriteriaDTO dto, Authentication authentication) {
//        String userUuid = (String) authentication.getAttributes().get("userUuid");
//        EligibilityCriteria entity = dto.toEntity();
//        entity.setCreatedBy(userUuid);
//        EligibilityCriteria saved = service.create(entity);
//        return HttpResponse.created(SuccessResponse.of("Critério criado com sucesso", new EligibilityCriteriaDTO(saved)));
//    }

//    @Operation(summary = "Actualizar critério de elegibilidade existente")
//    @Put
//    public HttpResponse<?> update(@Body EligibilityCriteriaDTO dto, Authentication authentication) {
//        String userUuid = (String) authentication.getAttributes().get("userUuid");
//        EligibilityCriteria entity = dto.toEntity();
//        entity.setUpdatedBy(userUuid);
//        EligibilityCriteria updated = service.update(entity);
//        return HttpResponse.ok(SuccessResponse.of("Critério actualizado com sucesso", new EligibilityCriteriaDTO(updated)));
//    }
//
//    @Operation(summary = "Eliminar critério de elegibilidade por UUID")
//    @Delete("/{uuid}")
//    public HttpResponse<?> delete(@PathVariable String uuid) {
//        service.delete(uuid);
//        return HttpResponse.ok(SuccessResponse.messageOnly("Critério eliminado com sucesso"));
//    }

//    @Operation(summary = "Actualizar o estado (ativo/inativo) de um critério de elegibilidade")
//    @Put("/{uuid}/status")
//    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
//        EligibilityCriteria updated = service.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
//        return HttpResponse.ok(SuccessResponse.of("Estado do critério atualizado com sucesso", new EligibilityCriteriaDTO(updated)));
//    }
}
