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
import mz.org.csaude.comvida.backend.dto.UserServiceRoleDTO;
import mz.org.csaude.comvida.backend.dto.request.AssignUserRolesRequest;
import mz.org.csaude.comvida.backend.dto.request.ReplaceUserRolesRequest;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.service.UserServiceRoleService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.USER_SERVICE_ROLE_CONTROLLER)
@Tag(name = "UserServiceRole", description = "API for managing user service roles")
public class UserServiceRoleController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceRoleController.class);

    @Inject
    private UserServiceRoleService service;

    // ====== LISTAGEM/FILTROS PADRÃO ======

    @Operation(summary = "List user service roles, optionally filtered by user/role/programActivity (paginated)")
    @Get
    public HttpResponse<?> list(@Nullable @QueryValue("userUuid") String userUuid,
                                @Nullable @QueryValue("roleUuid") String roleUuid,
                                @Nullable @QueryValue("programActivityUuid") String programActivityUuid,
                                @Nullable Pageable pageable) {
        Page<UserServiceRole> page;
        Pageable pg = resolvePageable(pageable);

        if (Utilities.stringHasValue(userUuid)) {
            page = service.findByUser(userUuid, pg);
        } else if (Utilities.stringHasValue(roleUuid)) {
            page = service.findByRole(roleUuid, pg);
        } else if (Utilities.stringHasValue(programActivityUuid)) {
            page = service.findByProgramActivity(programActivityUuid, pg);
        } else {
            page = service.findAll(pg);
        }

        List<UserServiceRoleDTO> dtos = page.getContent().stream()
                .map(UserServiceRoleDTO::new)
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0 ? "Sem Dados para esta pesquisa" : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, page.getTotalSize(), page.getPageable(), message)
        );
    }

    @Operation(summary = "Get user service role by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<UserServiceRole> optional = service.findById(id);
        return optional.map(e ->
                HttpResponse.ok(SuccessResponse.of("Registo encontrado com sucesso", new UserServiceRoleDTO(e)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new user service role")
    @Post
    public HttpResponse<?> create(@Body UserServiceRoleDTO dto, Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        UserServiceRole entity = dto.toEntity();
        entity.setCreatedBy(actorUuid);
        UserServiceRole created = service.create(entity);
        return HttpResponse.created(SuccessResponse.of("Registo criado com sucesso", new UserServiceRoleDTO(created)));
    }

    @Operation(summary = "Update an existing user service role")
    @Put
    public HttpResponse<?> update(@Body UserServiceRoleDTO dto, Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        UserServiceRole entity = dto.toEntity();
        entity.setUpdatedBy(actorUuid);
        UserServiceRole updated = service.update(entity);
        return HttpResponse.ok(SuccessResponse.of("Registo atualizado com sucesso", new UserServiceRoleDTO(updated)));
    }

    @Operation(summary = "Delete a user service role by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        service.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Registo eliminado com sucesso"));
    }

    @Operation(summary = "Activate or deactivate a user service role by changing its LifeCycleStatus")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        UserServiceRole updated = service.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado atualizado com sucesso", new UserServiceRoleDTO(updated)));
    }

    // ====== NOVOS ENDPOINTS: ASSIGN/REPLACE/REMOVE ======

    @Operation(summary = "Assign one or more roles to a user (optionally scoped by programActivity)")
    @Post("/{userUuid}/roles/assign")
    public HttpResponse<?> assignRoles(@PathVariable String userUuid,
                                       @Body AssignUserRolesRequest request,
                                       Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        List<UserServiceRole> result = service.assignRoles(userUuid, request.getProgramActivityUuid(), request.getRoleUuids(), actorUuid);

        List<UserServiceRoleDTO> dtos = result.stream().map(UserServiceRoleDTO::new).collect(Collectors.toList());
        return HttpResponse.ok(SuccessResponse.of("Roles atribuídos com sucesso", dtos));
    }

    @Operation(summary = "Replace user roles for a scope (programActivity or global) with the provided set")
    @Put("/{userUuid}/roles/replace")
    public HttpResponse<?> replaceRoles(@PathVariable String userUuid,
                                        @Body ReplaceUserRolesRequest request,
                                        Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        List<UserServiceRole> result = service.replaceRoles(userUuid, request.getProgramActivityUuid(), request.getRoleUuids(), actorUuid);

        List<UserServiceRoleDTO> dtos = result.stream().map(UserServiceRoleDTO::new).collect(Collectors.toList());
        return HttpResponse.ok(SuccessResponse.of("Roles substituídos com sucesso", dtos));
    }

    @Operation(summary = "Remove a single role from a user (optionally scoped by programActivity)")
    @Delete("/{userUuid}/roles/{roleUuid}")
    public HttpResponse<?> removeRole(@PathVariable String userUuid,
                                      @PathVariable String roleUuid,
                                      @Nullable @QueryValue("programActivityUuid") String programActivityUuid) {
        service.removeRole(userUuid, roleUuid, programActivityUuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Role removido com sucesso"));
    }
}
