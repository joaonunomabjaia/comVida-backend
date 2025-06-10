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
@Table(name = "home_visits")
public class HomeVisit extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    @ManyToOne
    @JoinColumn(name = "cohort_member_id")
    private CohortMember cohortMember;

    private Integer visitNumber;

    @Temporal(TemporalType.DATE)
    private Date visitDate;

    private String result; // VISITADO, RECUSOU, etc.

    private String notes;

    @Column(columnDefinition = "json")
    private String form;
}
