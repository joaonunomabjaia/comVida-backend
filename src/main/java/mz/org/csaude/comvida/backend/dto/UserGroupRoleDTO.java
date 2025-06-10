package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;

@Introspected
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Serdeable
public class UserGroupRoleDTO extends BaseEntityDTO {

    private UserDTO user;
    private GroupDTO group;
    private RoleDTO role;

    public UserGroupRoleDTO(UserGroupRole entity) {
        super(entity);
        if (entity.getUser() != null) {
            this.user = new UserDTO(entity.getUser());
        }
        if (entity.getGroup() != null) {
            this.group = new GroupDTO(entity.getGroup());
        }
        if (entity.getRole() != null) {
            this.role = new RoleDTO(entity.getRole());
        }
    }
}
