package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
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
import mz.org.fgh.mentoring.util.LifeCycleStatus;

/**
 * DTO for join table user_service_role_groups.
 * Accepts UUIDs (preferred) and also optional numeric IDs for internal use.
 */
@Introspected
@Serdeable
@Getter @Setter
public class UserServiceRoleGroupDTO extends BaseEntityDTO {

    /* Public API: UUIDs */
    @NotBlank
    private String userServiceRoleUuid;

    @NotBlank
    private String groupUuid;

    /* Optional internal use */
    private Long userServiceRoleId;
    private Long groupId;

    @Creator
    public UserServiceRoleGroupDTO() {
    }

    public UserServiceRoleGroupDTO(UserServiceRoleGroup e) {
        super(e);
        if (e.getUserServiceRole() != null) {
            this.userServiceRoleUuid = e.getUserServiceRole().getUuid();
            this.userServiceRoleId   = e.getUserServiceRole().getId();
        }
        if (e.getGroup() != null) {
            this.groupUuid = e.getGroup().getUuid();
            this.groupId   = e.getGroup().getId();
        }
        if (e.getLifeCycleStatus() != null) {
            this.setLifeCycleStatus(e.getLifeCycleStatus().name());
        }
    }

    public UserServiceRoleGroup toEntity() {
        UserServiceRoleGroup link = new UserServiceRoleGroup();

        // base
        if (getUuid() != null) link.setUuid(getUuid());
        LifeCycleStatus lcs = LifeCycleStatus.ACTIVE;
        if (getLifeCycleStatus() != null && !getLifeCycleStatus().isBlank()) {
            lcs = LifeCycleStatus.valueOf(getLifeCycleStatus());
        }
        link.setLifeCycleStatus(lcs);

        // USR stub
        UserServiceRole usr = new UserServiceRole();
        if (this.userServiceRoleId != null) usr.setId(this.userServiceRoleId);
        if (this.userServiceRoleUuid != null) usr.setUuid(this.userServiceRoleUuid);
        link.setUserServiceRole(usr);

        // Group stub
        Group g = new Group();
        if (this.groupId != null) g.setId(this.groupId);
        if (this.groupUuid != null) g.setUuid(this.groupUuid);
        link.setGroup(g);

        return link;
    }

    /** Helper for quick creation when you have UUIDs. */
    public static UserServiceRoleGroupDTO of(String usrRoleUuid, String grpUuid) {
        UserServiceRoleGroupDTO dto = new UserServiceRoleGroupDTO();
        dto.setUserServiceRoleUuid(usrRoleUuid);
        dto.setGroupUuid(grpUuid);
        dto.setLifeCycleStatus(LifeCycleStatus.ACTIVE.name());
        return dto;
    }
}
