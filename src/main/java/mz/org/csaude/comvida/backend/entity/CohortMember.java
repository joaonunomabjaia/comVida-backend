package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.util.SourceTypeEnum;

import java.util.Date;

@Entity
@Getter
@Setter
@Serdeable
@Table(name = "cohort_members")
public class CohortMember extends BaseEntity { // Membro que entrou na cohort

    // Relacionamento com a coorte
    @ManyToOne
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    // UUID do paciente no sistema de origem
    @Column(name = "origin_id", length = 100)
    private String originId;

    // Data de inclusão na coorte [Caso o paciente venha da INTEGRATTION, inclusionDate sera igual ao createdAt, se vier do FILE, sera agendado no frontend]
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "inclusion_date")
    private Date inclusionDate;

    // Data de exclusão da coorte
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exclusion_date")
    private Date exclusionDate;

    // Relacionamento com o paciente
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Sistema de origem (IDMED, OPENMRS, etc.)
    @ManyToOne
    @JoinColumn(name = "source_system_id")
    private SourceSystem sourceSystem;

    // Tipo de fonte: FILE ou INTEGRATION
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceTypeEnum sourceType;

    // Ficheiro de onde o paciente foi importado (se aplicável)
    @ManyToOne
    @JoinColumn(name = "import_file_id")
    private PatientImportFile patientImportFile;

}
