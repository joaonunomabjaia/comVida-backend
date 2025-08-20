package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

import java.util.Date;

@Entity
@Getter
@Setter
@Serdeable.Deserializable
@Table(name = "patient_import_configurations")
public class PatientImportConfiguration extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @ManyToOne
    @JoinColumn(name = "import_file_id", nullable = false)
    private PatientImportFile importFile;

    @ManyToOne
    @JoinColumn(name = "program_activity_id", nullable = false)
    private ProgramActivity programActivity;

    @Temporal(TemporalType.DATE)
    @Column(name = "entry_date")
    private Date entryDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "exit_date")
    private Date exitDate;

    @Column(length = 255)
    private String notes; // observações adicionais sobre a configuração/importação
}
