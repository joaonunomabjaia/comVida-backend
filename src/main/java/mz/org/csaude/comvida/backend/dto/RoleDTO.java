package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

@Getter
@Setter
@Serdeable
public class RoleDTO extends BaseEntityDTO {

    private String uuid;
    private String name;

    @Creator
    public RoleDTO() {}

    public RoleDTO(Role role) {
        super(role); // copies id, createdBy, createdAt, etc. from BaseEntity
        this.uuid = role.getUuid();
        this.name = role.getName();
    }

    @Override
    public Role toEntity() {
        Role role = new Role();
        role.setUuid(this.uuid);
        role.setName(this.name);
        // If BaseEntityDTO has these fields, populate them
        role.setId(this.getId());
        role.setCreatedBy(this.getCreatedBy());
        role.setCreatedAt(this.getCreatedAt());
        role.setUpdatedBy(this.getUpdatedBy());
        role.setUpdatedAt(this.getUpdatedAt());
        role.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        return role;
    }
}
