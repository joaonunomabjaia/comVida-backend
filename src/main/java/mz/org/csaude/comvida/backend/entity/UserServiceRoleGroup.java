package mz.org.csaude.comvida.backend.entity;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Table(
        name = "user_service_role_groups",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usr_role_groups_pair",
                        columnNames = {"user_service_role_id", "group_id"}
                )
        }
)
@Getter
@Setter
@Serdeable.Deserializable
public class UserServiceRoleGroup extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_service_role_id", nullable = false)
    private UserServiceRole userServiceRole;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Creator
    public UserServiceRoleGroup() {
    }

    public UserServiceRoleGroup(UserServiceRole userServiceRole, Group group) {
        this.userServiceRole = userServiceRole;
        this.group = group;
    }
}
