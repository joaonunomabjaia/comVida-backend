package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "allocations")
public class Allocation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cohort_member_id")
    private CohortMember cohortMember;

    @ManyToOne
    @JoinColumn(name = "user_service_role_id")
    private UserGroupRole userServiceRole;

    @ManyToOne
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date allocationDate;

    @Column(columnDefinition = "json")
    private String form;

    @Column(length = 50)
    private String status; // PENDING, IN_PROGRESS, DONE
}
