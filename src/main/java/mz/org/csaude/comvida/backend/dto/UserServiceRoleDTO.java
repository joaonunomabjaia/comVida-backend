package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;

@Introspected
@Serdeable
@Getter @Setter
@NoArgsConstructor
public class UserServiceRoleDTO extends BaseEntityDTO {

    @NotBlank
    private String userUuid;

    // Optional if you still allow “no program activity” at this stage
    private String programActivityUuid;

    @NotBlank
    private String roleUuid;

    public UserServiceRoleDTO(UserServiceRole e) {
        super(e);
        this.userUuid = e.getUser() != null ? e.getUser().getUuid() : null;
        this.programActivityUuid = e.getProgramActivity() != null ? e.getProgramActivity().getUuid() : null;
        this.roleUuid = e.getRole() != null ? e.getRole().getUuid() : null;
    }

    /** Creates a minimal entity with only UUID links set (services should attach managed refs). */
    public UserServiceRole toEntity() {
        UserServiceRole e = new UserServiceRole();
        // base fields from BaseEntityDTO (uuid, lifecycleStatus) if you want:
        if (getUuid() != null) e.setUuid(getUuid());
        if (getLifeCycleStatus() != null) e.setLifeCycleStatus(
                mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(getLifeCycleStatus())
        );

        User u = new User(); u.setUuid(this.userUuid); e.setUser(u);

        if (this.programActivityUuid != null && !this.programActivityUuid.isBlank()) {
            ProgramActivity pa = new ProgramActivity(); pa.setUuid(this.programActivityUuid); e.setProgramActivity(pa);
        } else {
            e.setProgramActivity(null);
        }

        Role r = new Role(); r.setUuid(this.roleUuid); e.setRole(r);

        return e;
        // Tip: in your service, replace these placeholders with managed entities via repositories.
    }
}
