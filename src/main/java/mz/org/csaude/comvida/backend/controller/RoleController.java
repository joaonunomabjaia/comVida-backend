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
import mz.org.csaude.comvida.backend.dto.RoleDTO;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.service.RoleService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.ROLE_CONTROLLER) // ex.: "/roles"
@Tag(name = "Role", description = "API para gestão de funções/perfis")
public class RoleController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    @Inject
    private RoleService roleService;

    @Operation(summary = "Listar ou pesquisar roles por nome (paginado)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("RoleController.listOrSearch");

        final Pageable pg = resolvePageable(pageable);
        Page<Role> page = !Utilities.stringHasValue(name)
                ? roleService.findAll(pg)
                : roleService.searchByName(name, pg);

        List<RoleDTO> dtos = page.getContent().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0 ? "Sem Dados para esta pesquisa" : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, page.getTotalSize(), page.getPageable(), message)
        );
    }

    @Operation(summary = "Obter role por ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<Role> optional = roleService.findById(id);
        return optional.map(role ->
                HttpResponse.ok(SuccessResponse.of("Role encontrada com sucesso", new RoleDTO(role)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Criar nova role")
    @Post
    public HttpResponse<?> create(@Body RoleDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");

        Role role = dto.toEntity();
        role.setCreatedBy(userUuid);

        Role created = roleService.create(role);
        return HttpResponse.created(SuccessResponse.of("Role criada com sucesso", new RoleDTO(created)));
    }

    @Operation(summary = "Atualizar role existente")
    @Put
    public HttpResponse<?> update(@Body RoleDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");

        Role role = dto.toEntity();
        role.setUpdatedBy(userUuid);

        Role updated = roleService.update(role);
        return HttpResponse.ok(SuccessResponse.of("Role atualizada com sucesso", new RoleDTO(updated)));
    }

    @Operation(summary = "Eliminar role por UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        roleService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Role eliminada com sucesso"));
    }

    @Operation(summary = "Alterar estado (LifeCycleStatus) da role")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid,
                                                 @Body LifeCycleStatusDTO dto) {
        Role updated = roleService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado da role atualizado com sucesso", new RoleDTO(updated)));
    }
}
