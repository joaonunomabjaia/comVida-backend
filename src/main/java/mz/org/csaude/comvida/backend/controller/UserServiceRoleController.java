package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.UserGroupRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.USER_SERVICE_ROLE_CONTROLLER)
@Tag(name = "UserServiceRole", description = "API para gerenciar UserServiceRoles")
public class UserServiceRoleController {

    @Inject
    private UserGroupRoleService userGroupRoleService;

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceRoleController.class);

    @Operation(summary = "Listar todos os UserServiceRoles com paginação")
    @ApiResponse(responseCode = "200", description = "Lista obtida com sucesso")
    @Get("/")
    public HttpResponse<?> listAll(@Nullable Pageable pageable) {
        try {
            List<UserGroupRole> page = userGroupRoleService.findAll(pageable);
            return HttpResponse.ok(page);
        } catch (Exception e) {
            LOG.error("Erro ao listar UserServiceRoles", e);
            return HttpResponse.badRequest().body(new ComVidaAPIError(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        }
    }

    @Operation(summary = "Buscar UserServiceRole pelo ID")
    @ApiResponse(responseCode = "200", description = "UserServiceRole encontrado")
    @ApiResponse(responseCode = "404", description = "UserServiceRole não encontrado")
    @Get("/{id}")
    public HttpResponse<?> findById(@NonNull @PathVariable Long id) {
        try {
            Optional<UserGroupRole> opt = userGroupRoleService.findById(id);
            return opt.map(userServiceRole -> HttpResponse.ok(new UserGroupRole(userServiceRole)))
                    .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Erro ao buscar UserServiceRole por ID", e);
            return HttpResponse.badRequest().body(new ComVidaAPIError(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        }
    }

//    @Operation(summary = "Salvar ou atualizar UserServiceRole")
//    @ApiResponse(responseCode = "201", description = "UserServiceRole criado/atualizado com sucesso")
//    @ApiResponse(responseCode = "400", description = "Requisição inválida")
//    @Post("/saveOrUpdate")
//    public HttpResponse<?> saveOrUpdate(@NonNull @Body UserServiceRoleDTO dto, Authentication authentication) {
//        try {
//            UserServiceRoleDTO saved = userServiceRoleService.saveOrUpdate((Long) authentication.getAttributes().get("userInfo"), dto);
//            LOG.info("UserServiceRole salvo/atualizado: {}", saved);
//            return HttpResponse.created(saved);
//        } catch (Exception e) {
//            LOG.error("Erro ao salvar/atualizar UserServiceRole", e);
//            return HttpResponse.badRequest().body(new ComVidaAPIError(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
//        }
//    }

    @Operation(summary = "Deletar UserServiceRole pelo UUID")
    @ApiResponse(responseCode = "200", description = "UserServiceRole deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "UserServiceRole não encontrado")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@NonNull @PathVariable String uuid) {
        try {
            Optional<UserGroupRole> opt = userGroupRoleService.findByUuid(uuid);
            if (opt.isPresent()) {
                userGroupRoleService.delete(uuid);
                LOG.info("UserServiceRole deletado: {}", uuid);
                return HttpResponse.ok();
            } else {
                return HttpResponse.notFound();
            }
        } catch (Exception e) {
            LOG.error("Erro ao deletar UserServiceRole", e);
            return HttpResponse.badRequest().body(new ComVidaAPIError(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        }
    }
}
