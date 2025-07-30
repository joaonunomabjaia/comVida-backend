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
import mz.org.csaude.comvida.backend.dto.LifeCycleStatusDTO;
import mz.org.csaude.comvida.backend.dto.SourceSystemDTO;
import mz.org.csaude.comvida.backend.entity.SourceSystem;
import mz.org.csaude.comvida.backend.service.SourceSystemService;
import mz.org.csaude.comvida.backend.util.Utilities;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.SOURCE_SYSTEM_CONTROLLER)
@Tag(name = "SourceSystem", description = "API for managing source systems")
public class SourceSystemController extends BaseController {

    @Inject
    private SourceSystemService sourceSystemService;

    @Operation(summary = "List or search source systems by code (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("code") String code,
                                        @Nullable Pageable pageable) {
        Page<SourceSystem> systems = !Utilities.stringHasValue(code)
                ? sourceSystemService.findAll(resolvePageable(pageable))
                : sourceSystemService.findByCodeIlike(code, resolvePageable(pageable));

        List<SourceSystemDTO> dtos = systems.getContent().stream()
                .map(SourceSystemDTO::new)
                .collect(Collectors.toList());

        String message = systems.getTotalSize() == 0 ? "Sem dados" : "Dados encontrados";
        return HttpResponse.ok(PaginatedResponse.of(dtos, systems.getTotalSize(), systems.getPageable(), message));
    }

    @Operation(summary = "Get a source system by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<SourceSystem> optional = sourceSystemService.findById(id);
        return optional.map(entity -> HttpResponse.ok(SuccessResponse.of("Fonte encontrada", new SourceSystemDTO(entity))))
                .orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Get a source system by Code")
    @Get("/code/{code}")
    public HttpResponse<?> findByCode(@PathVariable String code) {
        Optional<SourceSystem> optional = sourceSystemService.findByCode(code);
        return optional.map(entity -> HttpResponse.ok(SuccessResponse.of("Fonte encontrada", new SourceSystemDTO(entity))))
                .orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new source system")
    @Post
    public HttpResponse<?> create(@Body SourceSystemDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        SourceSystem entity = dto.toEntity();
        entity.setCreatedBy(userUuid);
        SourceSystem created = sourceSystemService.create(entity);
        return HttpResponse.created(SuccessResponse.of("Fonte criada com sucesso", new SourceSystemDTO(created)));
    }

    @Operation(summary = "Update an existing source system")
    @Put
    public HttpResponse<?> update(@Body SourceSystemDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        SourceSystem entity = dto.toEntity();
        entity.setUpdatedBy(userUuid);
        SourceSystem updated = sourceSystemService.update(entity);
        return HttpResponse.ok(SuccessResponse.of("Fonte atualizada com sucesso", new SourceSystemDTO(updated)));
    }

    @Operation(summary = "Delete a source system by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        sourceSystemService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Fonte eliminada com sucesso"));
    }

    @Operation(summary = "Update lifecycle status of a source system")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        SourceSystem updated = sourceSystemService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado atualizado com sucesso", new SourceSystemDTO(updated)));
    }
}
