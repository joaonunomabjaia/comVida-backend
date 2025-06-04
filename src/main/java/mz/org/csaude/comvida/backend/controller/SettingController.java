package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.SettingDTO;
import mz.org.csaude.comvida.backend.entity.Setting;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Tag(name = "Settings", description = "Admin API to manage system settings")
@Controller("/api/settings")
public class SettingController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(SettingController.class);

    @Inject
    private SettingService settingService;

    @Operation(summary = "List or search settings", description = "Returns a paginated list of settings. If a designation is provided, performs a case-insensitive search.")
    @ApiResponse(responseCode = "200", description = "Settings list retrieved successfully")
    @Get
    public HttpResponse<?> listOrSearch(
            @QueryValue("designation") String designation,
            @Parameter(hidden = true) @Nullable Pageable pageable) {
        try {
            Page<Setting> result = designation.isBlank()
                    ? settingService.findAll(resolvePageable(pageable))
                    : settingService.searchByDesignation(designation, resolvePageable(pageable));

            return HttpResponse.ok(result.map(SettingDTO::new));
        } catch (Exception e) {
            LOG.error("Error listing/searching settings: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Get a setting by ID", description = "Returns a single setting by its ID")
    @ApiResponse(responseCode = "200", description = "Setting found")
    @ApiResponse(responseCode = "404", description = "Setting not found")
    @Get("/{id}")
    public HttpResponse<?> findById(@PathVariable Long id) {
        try {
            Optional<Setting> setting = settingService.findById(id);
            return setting.map(s -> HttpResponse.ok(new SettingDTO(s)))
                    .orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error fetching setting by ID: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Create a new setting")
    @ApiResponse(responseCode = "201", description = "Setting created successfully")
    @Post
    public HttpResponse<?> create(@Body SettingDTO dto) {
        try {
            Setting created = settingService.create(new Setting(dto));
            return HttpResponse.created(new SettingDTO(created));
        } catch (Exception e) {
            LOG.error("Error creating setting: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Update an existing setting by designation")
    @ApiResponse(responseCode = "200", description = "Setting updated successfully")
    @ApiResponse(responseCode = "404", description = "Setting not found")
    @Put("/{designation}")
    public HttpResponse<?> update(@PathVariable String designation, @Body SettingDTO dto) {
        try {
            Optional<Setting> existing = settingService.findByDesignation(designation);
            return existing.map(setting -> {
                setting.setValue(dto.getValue());
                setting.setType(dto.getType());
                setting.setEnabled(dto.getEnabled());
                setting.setDescription(dto.getDescription());
                Setting updated = settingService.updateSetting(setting);
                return HttpResponse.ok(new SettingDTO(updated));
            }).orElse(HttpResponse.notFound());
        } catch (Exception e) {
            LOG.error("Error updating setting: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }

    @Operation(summary = "Delete a setting by UUID")
    @ApiResponse(responseCode = "204", description = "Setting deleted successfully")
    @ApiResponse(responseCode = "404", description = "Setting not found")
    @Delete("/{uuid}")
    public HttpResponse<?> delete(@PathVariable String uuid) {
        try {
            settingService.delete(uuid);
            return HttpResponse.noContent();
        } catch (Exception e) {
            LOG.error("Error deleting setting: {}", e.getMessage(), e);
            return buildErrorResponse(e);
        }
    }
}
