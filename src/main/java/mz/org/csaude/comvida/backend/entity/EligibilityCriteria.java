package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Serdeable
@Table(name = "eligibility_criteria")
public class EligibilityCriteria extends BaseEntity {

    @Column(name = "criteria")
    private String criteria;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_activity_id", nullable = false)
    private ProgramActivity programActivity;
}
