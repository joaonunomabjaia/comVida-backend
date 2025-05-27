package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Allocation;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class AllocationDTO extends BaseEntityDTO {

    private String uuid;
    private CohortMemberDTO cohortMember;
    private UserServiceRoleDTO userServiceRole;
    private UserDTO assignedBy;

    private Date allocationDate;
    private String form;
    private String status;

    public AllocationDTO() {}

    public AllocationDTO(Allocation allocation) {
        this.uuid = allocation.getUuid();
        this.cohortMember = allocation.getCohortMember() != null ? new CohortMemberDTO(allocation.getCohortMember()) : null;
        this.userServiceRole = allocation.getUserServiceRole() != null ? new UserServiceRoleDTO(allocation.getUserServiceRole()) : null;
        this.assignedBy = allocation.getAssignedBy() != null ? new UserDTO(allocation.getAssignedBy()) : null;
        this.allocationDate = allocation.getAllocationDate();
        this.form = allocation.getForm();
        this.status = allocation.getStatus();
    }
}
