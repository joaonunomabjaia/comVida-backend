package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "program_activities")
public class ProgramActivity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
}
