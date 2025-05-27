package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "eligibility_criteria")
public class EligibilityCriteria extends BaseEntity {

    private String criteria;
}
