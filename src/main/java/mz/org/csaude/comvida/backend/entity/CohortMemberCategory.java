package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Serdeable
@Table(name = "cohort_member_categories")
public class CohortMemberCategory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cohort_member_id")
    private CohortMember cohortMember;

    @ManyToOne
    @JoinColumn(name = "eligibility_criteria_id")
    private EligibilityCriteria eligibilityCriteria;
}
