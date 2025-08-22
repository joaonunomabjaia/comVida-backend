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
import mz.org.csaude.comvida.backend.dto.*;
import mz.org.csaude.comvida.backend.dto.request.AssignUserRolesRequest;
import mz.org.csaude.comvida.backend.dto.request.ReplaceUserRolesRequest;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.service.UserService;
import mz.org.csaude.comvida.backend.service.UserServiceRoleService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.USER_CONTROLLER)
@Tag(name = "User", description = "API para gestão de utilizadores")
public class UserController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UserService userService;

    @Inject
    private UserServiceRoleService userServiceRoleService;

    // ===========================
    // Utilizadores (existente)
    // ===========================

    @Get
    @Operation(summary = "Listar ou pesquisar utilizadores por nome (paginado)")
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<User> users = !Utilities.stringHasValue(name)
                ? userService.findAll(resolvePageable(pageable))
                : userService.searchByName(name, resolvePageable(pageable));

        List<UserDTO> dtos = users.getContent().stream().map(UserDTO::new).collect(Collectors.toList());

        return HttpResponse.ok(PaginatedResponse.of(
                dtos,
                users.getTotalSize(),
                users.getPageable(),
                users.isEmpty() ? "Sem dados para esta pesquisa" : "Dados encontrados"
        ));
    }

    @Get("/{id}")
    @Operation(summary = "Obter utilizador por ID")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<User> optional = userService.findById(id);
        return optional.map(user -> HttpResponse.ok(SuccessResponse.of("Utilizador encontrado com sucesso", new UserDTO(user))))
                .orElse(HttpResponse.notFound());
    }

    @Post
    @Operation(summary = "Criar novo utilizador")
    public HttpResponse<?> create(@Body UserDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        User user = dto.toEntity();
        user.setCreatedBy(userUuid);
        User created = userService.create(user);
        return HttpResponse.created(SuccessResponse.of("Utilizador criado com sucesso", new UserDTO(created)));
    }

    @Put
    @Operation(summary = "Atualizar utilizador existente")
    public HttpResponse<?> update(@Body UserDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        User user = dto.toEntity();
        user.setUpdatedBy(userUuid);
        User updated = userService.update(user);
        return HttpResponse.ok(SuccessResponse.of("Utilizador atualizado com sucesso", new UserDTO(updated)));
    }

    @Delete("/{uuid}")
    @Operation(summary = "Eliminar utilizador por UUID")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        userService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Utilizador eliminado com sucesso"));
    }

    @Put("/{uuid}/status")
    @Operation(summary = "Alterar estado do utilizador (ativo/inativo)")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        User updated = userService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado do utilizador atualizado com sucesso", new UserDTO(updated)));
    }

    // ==========================================
    // Gestão de ROLES do utilizador (NOVO)
    // via UserServiceRole
    // ==========================================

    @Get("/{userUuid}/service-roles")
    @Operation(summary = "Listar vínculos UserServiceRole do utilizador (paginado)")
    public HttpResponse<?> listUserServiceRoles(@PathVariable String userUuid,
                                                @Nullable Pageable pageable) {
        Page<UserServiceRole> page = userServiceRoleService.findByUser(userUuid, resolvePageable(pageable));
        List<UserServiceRoleDTO> dtos = page.getContent().stream()
                .map(UserServiceRoleDTO::new)
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0 ? "Sem Dados para esta pesquisa" : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, page.getTotalSize(), page.getPageable(), message)
        );
    }

    @Get("/{userUuid}/roles")
    @Operation(summary = "Listar apenas as roles do utilizador (derivado de UserServiceRole)")
    public HttpResponse<?> listUserRoles(@PathVariable String userUuid,
                                         @Nullable Pageable pageable) {
        Page<UserServiceRole> page = userServiceRoleService.findByUser(userUuid, resolvePageable(pageable));

        List<RoleDTO> roles = page.getContent().stream()
                .map(usr -> new RoleDTO(usr.getRole()))
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0 ? "Sem Dados para esta pesquisa" : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(roles, page.getTotalSize(), page.getPageable(), message)
        );
    }

    @Post("/{userUuid}/roles/assign")
    @Operation(summary = "Atribuir uma ou mais roles ao utilizador (escopo opcional por programActivityUuid)")
    public HttpResponse<?> assignRoles(@PathVariable String userUuid,
                                       @Body AssignUserRolesRequest request,
                                       Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        List<UserServiceRole> result = userServiceRoleService.assignRoles(
                userUuid,
                request.getProgramActivityUuid(),
                request.getRoleUuids(),
                actorUuid
        );

        List<UserServiceRoleDTO> dtos = result.stream().map(UserServiceRoleDTO::new).collect(Collectors.toList());
        return HttpResponse.ok(SuccessResponse.of("Roles atribuídos com sucesso", dtos));
    }

    @Put("/{userUuid}/roles/replace")
    @Operation(summary = "Substituir o conjunto de roles do utilizador num escopo (programActivity ou global)")
    public HttpResponse<?> replaceRoles(@PathVariable String userUuid,
                                        @Body ReplaceUserRolesRequest request,
                                        Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");
        List<UserServiceRole> result = userServiceRoleService.replaceRoles(
                userUuid,
                request.getProgramActivityUuid(),
                request.getRoleUuids(),
                actorUuid
        );

        List<UserServiceRoleDTO> dtos = result.stream().map(UserServiceRoleDTO::new).collect(Collectors.toList());
        return HttpResponse.ok(SuccessResponse.of("Roles substituídos com sucesso", dtos));
    }

    @Delete("/{userUuid}/roles/{roleUuid}")
    @Operation(summary = "Remover uma role do utilizador (escopo opcional por programActivityUuid)")
    public HttpResponse<?> removeRole(@PathVariable String userUuid,
                                      @PathVariable String roleUuid,
                                      @Nullable @QueryValue("programActivityUuid") String programActivityUuid) {
        userServiceRoleService.removeRole(userUuid, roleUuid, programActivityUuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Role removida com sucesso"));
    }

    @Operation(summary = "Update User password")
    @Put("/{uuid}/password")
    public HttpResponse<?> updatePassword(@PathVariable String uuid,
                                          @Body UserPasswordDTO dto,
                                          Authentication authentication) {
        String updatedByUuid = (String) authentication.getAttributes().get("useruuid");
        userService.updatePassword(uuid, dto.getNewPassword(), updatedByUuid);
        return HttpResponse.ok(
                SuccessResponse.messageOnly("Senha do utilizador atualizada com sucesso")
        );
    }
}
