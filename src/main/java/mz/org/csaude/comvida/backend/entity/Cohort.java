package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "cohorts")
public class Cohort extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_activity_id", nullable = false)
    private ProgramActivity programActivity;
}
