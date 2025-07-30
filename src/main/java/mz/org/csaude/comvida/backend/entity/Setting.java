package mz.org.csaude.comvida.backend.entity;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.dto.SettingDTO;

@Schema(name = "settings", description = "Representa configurações do sistema")
@Entity(name = "settings")
@Table(name = "settings")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Serdeable.Deserializable
@ToString
public class Setting extends BaseEntity {

    public static final String MESSAGING_BROKER = "MESSAGING_BROKER";

    @NotNull
    @Column(name = "DESIGNATION", nullable = false)
    private String designation;

    @NotNull
    @Column(name = "SETTING_VALUE", nullable = false)
    private String value;

    @NotNull
    @Column(name = "SETTING_TYPE", nullable = false)
    private String type;

    @NotNull
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @NotNull
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Creator
    public Setting(String designation, String value, String type, Boolean enabled, String description) {
        this.designation = designation;
        this.value = value;
        this.type = type;
        this.enabled = enabled;
        this.description = description;
    }


    public Setting(SettingDTO settingDTO) {
        super(settingDTO);
        this.designation = settingDTO.getDesignation();
        this.value = settingDTO.getValue();
        this.type = settingDTO.getType();
        this.enabled = settingDTO.getEnabled();
        this.description = settingDTO.getDescription();
    }
}
