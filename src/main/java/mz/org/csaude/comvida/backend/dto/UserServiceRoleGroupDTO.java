package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.entity.UserServiceRoleGroup;

@Introspected
@Serdeable
@Getter @Setter
@NoArgsConstructor
public class UserServiceRoleGroupDTO extends BaseEntityDTO {

    @NotBlank
    private String userServiceRoleUuid;

    @NotBlank
    private String groupUuid;

    public UserServiceRoleGroupDTO(UserServiceRoleGroup e) {
        super(e);
        this.userServiceRoleUuid = e.getUserServiceRole() != null ? e.getUserServiceRole().getUuid() : null;
        this.groupUuid = e.getGroup() != null ? e.getGroup().getUuid() : null;
    }

    public UserServiceRoleGroup toEntity() {
        UserServiceRoleGroup e = new UserServiceRoleGroup();
        if (getUuid() != null) e.setUuid(getUuid());
        if (getLifeCycleStatus() != null) e.setLifeCycleStatus(
                mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(getLifeCycleStatus())
        );

        UserServiceRole usr = new UserServiceRole(); usr.setUuid(this.userServiceRoleUuid);
        e.setUserServiceRole(usr);

        Group g = new Group(); g.setUuid(this.groupUuid);
        e.setGroup(g);

        return e;
    }
}
