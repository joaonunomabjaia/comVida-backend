package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "cohort_members")
public class CohortMember extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cohort_id")
    private Cohort cohort;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "source_type_id")
    private SourceType sourceType;

    private String originId;

    @Temporal(TemporalType.DATE)
    private Date inclusionDate;

    @Temporal(TemporalType.DATE)
    private Date exclusionDate;
}
