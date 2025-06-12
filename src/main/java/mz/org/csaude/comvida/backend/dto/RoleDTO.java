package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Role;

@Getter
@Setter
@Serdeable
public class RoleDTO extends BaseEntityDTO {

    private String uuid;
    private String name;

    public RoleDTO() {}

    public RoleDTO(Role role) {
        this.uuid = role.getUuid();
        this.name = role.getName();
    }

    @Override
    public BaseEntity toEntity() {
        return null;
    }
}
