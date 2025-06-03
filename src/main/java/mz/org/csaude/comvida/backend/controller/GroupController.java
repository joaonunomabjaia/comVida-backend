package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.GroupDTO;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/groups")
@Tag(name = "Group", description = "API for managing user groups")
public class GroupController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(GroupController.class);

    @Inject
    private GroupService service;

    @Operation(summary = "List or search groups", description = "Returns a paginated list of groups. If a name is provided, performs a search.")
    @ApiResponse(responseCode = "200", description = "List of groups returned successfully")
    @Get
    public HttpResponse<?> listOrSearch(
            @Parameter(description = "Name to search for", example = "Admin")
            @QueryValue("name") String name,
            @Parameter(hidden = true) @Nullable Pageable pageable) {
        try {
            Page<Group> result = name.isBlank()
                    ? service.findAll(resolvePageable(pageable))
                    : service.searchByName(name, resolvePageable(pageable));

            return HttpResponse.ok(result.map(GroupDTO::new));
        } catch (Exception e) {
            LOG.error("Error listing/searching groups: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Get group by ID", description = "Fetch a group by its numeric ID")
    @ApiResponse(responseCode = "200", description = "Group found")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @Get("/{id}")
    public HttpResponse<?> findById(
            @Parameter(description = "Numeric ID of the group", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<Group> group = service.findById(id);
            return group.map(value -> HttpResponse.ok(new GroupDTO(value)))
                        .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error fetching group by ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Create a new group", description = "Creates a new group with name and description")
    @ApiResponse(responseCode = "201", description = "Group created successfully")
    @Post
    public HttpResponse<?> create(
            @Parameter(description = "Group data to create", required = true)
            @Body GroupDTO dto) {
        try {
            Group created = service.create(dto.toEntity());
            return HttpResponse.created(new GroupDTO(created));
        } catch (Exception e) {
            LOG.error("Error creating group: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Update an existing group", description = "Updates group name and description")
    @ApiResponse(responseCode = "200", description = "Group updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or group not found")
    @Put
    public HttpResponse<?> update(
            @Parameter(description = "Group data to update", required = true)
            @Body GroupDTO dto) {
        try {
            Group updated = service.update(dto.toEntity());
            return HttpResponse.ok(new GroupDTO(updated));
        } catch (Exception e) {
            LOG.error("Error updating group: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Delete a group by UUID", description = "Deletes the group with the specified UUID")
    @ApiResponse(responseCode = "204", description = "Group deleted successfully")
    @ApiResponse(responseCode = "400", description = "Group not found or error deleting")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(
            @Parameter(description = "UUID of the group", required = true)
            @PathVariable String uuid) {
        try {
            service.delete(uuid);
            return HttpResponse.noContent();
        } catch (Exception e) {
            LOG.error("Error deleting group: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

}
