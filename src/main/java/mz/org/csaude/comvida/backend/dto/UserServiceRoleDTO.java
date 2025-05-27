package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;

@Getter
@Setter
@Serdeable
public class UserServiceRoleDTO extends BaseEntityDTO {

    private String uuid;
    private UserDTO user;
    private ProgramServiceDTO service;
    private RoleDTO role;

    public UserServiceRoleDTO() {}

    public UserServiceRoleDTO(UserServiceRole userServiceRole) {
        this.uuid = userServiceRole.getUuid();
        this.user = userServiceRole.getUser() != null ? new UserDTO(userServiceRole.getUser()) : null;
        this.service = userServiceRole.getService() != null ? new ProgramServiceDTO(userServiceRole.getService()) : null;
        this.role = userServiceRole.getRole() != null ? new RoleDTO(userServiceRole.getRole()) : null;
    }
}
