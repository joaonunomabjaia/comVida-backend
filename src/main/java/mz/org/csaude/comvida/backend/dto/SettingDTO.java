package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Setting;

@Getter
@Setter
@NoArgsConstructor
@Serdeable
@Schema(name = "SettingDTO", description = "DTO representing a system setting")
public class SettingDTO extends BaseEntityDTO {

    @NotEmpty(message = "Designation is required")
    private String designation;

    @NotEmpty(message = "Value is required")
    private String value;

    @NotEmpty(message = "Type is required")
    private String type;

    @NotNull(message = "Enabled status must be provided")
    private Boolean enabled;

    @NotEmpty(message = "Description is required")
    private String description;

    public SettingDTO(Setting setting) {
        super(setting);
        this.designation = setting.getDesignation();
        this.value = setting.getValue();
        this.type = setting.getType();
        this.enabled = setting.getEnabled();
        this.description = setting.getDescription();
    }

    @Override
    public Setting toEntity() {
        return null;
    }
}
