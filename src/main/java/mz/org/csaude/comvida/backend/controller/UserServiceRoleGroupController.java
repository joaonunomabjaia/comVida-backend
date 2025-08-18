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
import mz.org.csaude.comvida.backend.dto.UserServiceRoleGroupDTO;
import mz.org.csaude.comvida.backend.entity.UserServiceRoleGroup;
import mz.org.csaude.comvida.backend.service.UserServiceRoleGroupService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.USER_SERVICE_ROLE_GROUP_CONTROLLER)
@Tag(name = "UserServiceRoleGroup", description = "API para gerir os grupos associados a UserServiceRole")
public class UserServiceRoleGroupController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceRoleGroupController.class);

    @Inject
    private UserServiceRoleGroupService service;

    @Operation(summary = "Lista (paginada) ou filtra por userServiceRoleUuid")
    @Get
    public HttpResponse<?> listOrFilter(@Nullable @QueryValue("userServiceRoleUuid") String userServiceRoleUuid,
                                        @Nullable Pageable pageable) {
        LOG.info("UserServiceRoleGroupController.listOrFilter");

        Page<UserServiceRoleGroup> page = Utilities.stringHasValue(userServiceRoleUuid)
                ? service.findByUserServiceRole(userServiceRoleUuid, resolvePageable(pageable))
                : service.findAll(resolvePageable(pageable));

        List<UserServiceRoleGroupDTO> dtos = page.getContent().stream()
                .map(UserServiceRoleGroupDTO::new)
                .collect(Collectors.toList());

        String message = page.getTotalSize() == 0 ? "Sem Dados para esta pesquisa" : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, page.getTotalSize(), page.getPageable(), message)
        );
    }

    @Operation(summary = "Obter por ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<UserServiceRoleGroup> optional = service.findById(id); // garante que teu service tenha este método
        return optional.map(entity ->
                HttpResponse.ok(SuccessResponse.of("Registo encontrado com sucesso", new UserServiceRoleGroupDTO(entity)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Criar vínculo UserServiceRole ↔ Group")
    @Post
    public HttpResponse<?> create(@Body UserServiceRoleGroupDTO dto, Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");

        UserServiceRoleGroup entity = dto.toEntity();
        entity.setCreatedBy(actorUuid);

        UserServiceRoleGroup created = service.create(entity);
        return HttpResponse.created(SuccessResponse.of("Registo criado com sucesso", new UserServiceRoleGroupDTO(created)));
    }

    @Operation(summary = "Atualizar vínculo UserServiceRole ↔ Group")
    @Put
    public HttpResponse<?> update(@Body UserServiceRoleGroupDTO dto, Authentication authentication) {
        String actorUuid = (String) authentication.getAttributes().get("userUuid");

        UserServiceRoleGroup entity = dto.toEntity();
        entity.setUpdatedBy(actorUuid);

        UserServiceRoleGroup updated = service.update(entity);
        return HttpResponse.ok(SuccessResponse.of("Registo atualizado com sucesso", new UserServiceRoleGroupDTO(updated)));
    }

    @Operation(summary = "Eliminar por UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        service.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Registo eliminado com sucesso"));
    }

    @Operation(summary = "Ativar/Desativar (LifeCycleStatus)")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        UserServiceRoleGroup updated = service.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado atualizado com sucesso", new UserServiceRoleGroupDTO(updated)));
    }
}
