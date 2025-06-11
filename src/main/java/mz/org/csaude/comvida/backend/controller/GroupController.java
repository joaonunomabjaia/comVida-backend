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
import mz.org.csaude.comvida.backend.dto.GroupDTO;
import mz.org.csaude.comvida.backend.dto.LifeCycleStatusDTO;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.service.GroupService;
import mz.org.csaude.comvida.backend.util.Utilities;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.GROUP_CONTROLLER)
@Tag(name = "Group", description = "API for managing user groups")
public class GroupController extends BaseController {

    @Inject
    private GroupService groupService;

    @Operation(summary = "List or search groups (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("name") String name,
                                        @Nullable Pageable pageable) {

        Page<Group> groups = !Utilities.stringHasValue(name)
                ? groupService.findAll(resolvePageable(pageable))
                : groupService.searchByName(name, resolvePageable(pageable));

        List<GroupDTO> groupDTOs = groups.getContent().stream()
                .map(GroupDTO::new)
                .collect(Collectors.toList());

        String message = groups.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(
                        groupDTOs,
                        groups.getTotalSize(),
                        groups.getPageable(),
                        message
                )
        );
    }

    @Operation(summary = "Get group by ID")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        Optional<Group> optional = groupService.findById(id);
        return optional.map(group ->
                HttpResponse.ok(SuccessResponse.of("Grupo encontrado com sucesso", new GroupDTO(group)))
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Create a new group")
    @Post
    public HttpResponse<?> create(@Body GroupDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Group group = dto.toEntity();
        group.setCreatedBy(userUuid);
        Group created = groupService.create(group);
        return HttpResponse.created(SuccessResponse.of("Grupo criado com sucesso", new GroupDTO(created)));
    }

    @Operation(summary = "Update an existing group")
    @Put
    public HttpResponse<?> update(@Body GroupDTO dto, Authentication authentication) {
        String userUuid = (String) authentication.getAttributes().get("userUuid");
        Group group = dto.toEntity();
        group.setUpdatedBy(userUuid);
        Group updated = groupService.update(group);
        return HttpResponse.ok(SuccessResponse.of("Grupo atualizado com sucesso", new GroupDTO(updated)));
    }

    @Operation(summary = "Delete a group by UUID")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        groupService.delete(uuid);
        return HttpResponse.ok(SuccessResponse.messageOnly("Grupo eliminado com sucesso"));
    }

    @Operation(summary = "Activate or deactivate a group by changing its LifeCycleStatus")
    @Put("/{uuid}/status")
    public HttpResponse<?> updateLifeCycleStatus(@PathVariable String uuid, @Body LifeCycleStatusDTO dto) {
        Group updatedGroup = groupService.updateLifeCycleStatus(uuid, dto.getLifeCycleStatus());
        return HttpResponse.ok(SuccessResponse.of("Estado do grupo atualizado com sucesso", new GroupDTO(updatedGroup)));
    }
}
