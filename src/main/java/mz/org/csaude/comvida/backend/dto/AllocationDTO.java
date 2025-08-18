package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Allocation;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class AllocationDTO extends BaseEntityDTO {

    private String uuid;
    private CohortMemberDTO cohortMember;
    private UserServiceRole userServiceRole;
    private UserDTO assignedBy;

    private Date allocationDate;
    private String form;
    private String status;

    public AllocationDTO() {}

    public AllocationDTO(Allocation allocation) {
        this.uuid = allocation.getUuid();
        this.cohortMember = allocation.getCohortMember() != null ? new CohortMemberDTO(allocation.getCohortMember()) : null;
        this.userServiceRole = allocation.getUserServiceRole() != null ? new UserServiceRole() : null;
        this.assignedBy = allocation.getAssignedBy() != null ? new UserDTO(allocation.getAssignedBy()) : null;
        this.allocationDate = allocation.getAllocationDate();
        this.form = allocation.getForm();
        this.status = allocation.getStatus();
    }

    @Override
    public BaseEntity toEntity() {
        return null;
    }
}
