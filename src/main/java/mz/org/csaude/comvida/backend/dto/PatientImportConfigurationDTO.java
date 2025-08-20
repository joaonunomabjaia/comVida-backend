package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.PatientImportConfiguration;
import mz.org.csaude.comvida.backend.util.Utilities;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Date;

@Getter
@Setter
@Serdeable
@NoArgsConstructor
@Schema(name = "PatientImportConfigurationDTO", description = "DTO representing a patient import configuration")
public class PatientImportConfigurationDTO extends BaseEntityDTO {

    private CohortDTO cohort;

    private ProgramActivityDTO programActivity;

    private PatientImportFileDTO patientImportFile;

    @NotNull(message = "Data de entrada é obrigatória.")
    private Date entryDate;

    private Date exitDate;

    private String notes;

    @Creator
    public PatientImportConfigurationDTO(PatientImportConfiguration entity) {
        super(entity);
        if (entity.getCohort() != null) {
            this.cohort = new CohortDTO(entity.getCohort());
        }
        if (entity.getProgramActivity() != null) {
            this.programActivity = new ProgramActivityDTO(entity.getProgramActivity());
        }
        if (entity.getImportFile() != null) {
            this.patientImportFile = new PatientImportFileDTO(entity.getImportFile());
        }

        this.entryDate = entity.getEntryDate();
        this.exitDate = entity.getExitDate();

        this.notes = entity.getNotes();
    }

    public PatientImportConfiguration toEntity() {
        PatientImportConfiguration entity = new PatientImportConfiguration();
        entity.setId(this.getId());
        entity.setUuid(this.getUuid());
        entity.setEntryDate(this.entryDate);
        entity.setExitDate(this.exitDate);
        entity.setNotes(this.notes);

        // Atenção: os setters abaixo devem ser preenchidos pelo service (buscando entidades reais do banco)
        // Aqui apenas preenchemos os IDs. O service deve resolver as entidades:
        //   entity.setCohort(cohortService.findById(this.cohortId).orElseThrow(...));
        //   entity.setImportFile(importFileService.findById(this.importFileId).orElseThrow(...));

        if (Utilities.stringHasValue(this.getLifeCycleStatus()))
            entity.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));

        return entity;
    }
}
