package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.SourceSystem;

@Getter
@Setter
@Serdeable
@NoArgsConstructor
@Schema(name = "SourceSystemDTO", description = "DTO representing a source system (e.g., OPENMRS, IDMED)")
public class SourceSystemDTO extends BaseEntityDTO {

    @NotEmpty(message = "The name of the source system is required.")
    private String code;

    @Creator
    public SourceSystemDTO(SourceSystem sourceSystem) {
        super(sourceSystem);
        this.code = sourceSystem.getCode();
    }

    public SourceSystem toEntity() {
        SourceSystem sourceSystem = new SourceSystem();
        sourceSystem.setId(this.getId());
        sourceSystem.setUuid(this.getUuid());
        sourceSystem.setCode(this.getCode());
        sourceSystem.setCreatedAt(this.getCreatedAt());
        sourceSystem.setCreatedBy(this.getCreatedBy());
        sourceSystem.setUpdatedAt(this.getUpdatedAt());
        sourceSystem.setUpdatedBy(this.getUpdatedBy());
        return sourceSystem;
    }
}
