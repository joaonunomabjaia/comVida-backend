package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "groups")
public class Group extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_activity_id", nullable = false)
    private ProgramActivity programActivity;
}
