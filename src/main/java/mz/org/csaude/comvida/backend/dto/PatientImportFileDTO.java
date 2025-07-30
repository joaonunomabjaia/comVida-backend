package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.entity.PatientImportFile.ImportStatus;

@Getter
@Setter
@Serdeable
@NoArgsConstructor
@Schema(name = "PatientImportFileDTO", description = "DTO representing a patient import file")
public class PatientImportFileDTO extends BaseEntityDTO {

    @NotEmpty(message = "File name is required.")
    private String name;

    @NotNull(message = "Status is required.")
    private ImportStatus status;

    @NotNull(message = "Progress is required.")
    private Integer progress;

    private String message;

    @NotNull(message = "Program activity is required.")
    private ProgramActivityDTO programActivity;

    @NotNull(message = "SurceSystem is required.")
    private SourceSystemDTO sourceSystem;

    @Creator
    public PatientImportFileDTO(PatientImportFile entity) {
        super(entity);
        this.name = entity.getName();
        this.status = entity.getStatus();
        this.progress = entity.getProgress();
        this.message = entity.getMessage();
        this.programActivity = new ProgramActivityDTO(entity.getProgramActivity());
        this.sourceSystem = new SourceSystemDTO(entity.getSourceSystem());
    }

    public PatientImportFile toEntity() {
        PatientImportFile entity = new PatientImportFile();
        entity.setId(this.getId());
        entity.setUuid(this.getUuid());
        entity.setCreatedAt(this.getCreatedAt());
        entity.setCreatedBy(this.getCreatedBy());
        entity.setUpdatedAt(this.getUpdatedAt());
        entity.setUpdatedBy(this.getUpdatedBy());

        entity.setName(this.name);
        entity.setStatus(this.status);
        entity.setProgress(this.progress);
        entity.setMessage(this.message);
        entity.setProgramActivity(this.programActivity.toEntity());
        entity.setSourceSystem(this.sourceSystem.toEntity());

        return entity;
    }
}

