package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DTO for a user's role within a specific Program Activity.
 * Note: group memberships are materialized via UserServiceRoleGroup join entities.
 * Here we keep groupUuids as a convenience for create/update flows.
 */
@Getter
@Setter
@Serdeable
public class UserServiceRoleDTO extends BaseEntityDTO {

    /** Optional: useful for UI; entity uses ProgramActivity for FK. */
    private Long programId;

    private Long programActivityId;
    private String roleUuid;

    /** Convenience payload from UI; actual persistence uses UserServiceRoleGroup. */
    private List<String> groupUuids = new ArrayList<>();

    private String lifeCycleStatus = LifeCycleStatus.ACTIVE.name();

    @Creator
    public UserServiceRoleDTO() {
        super();
    }

    public UserServiceRoleDTO(UserServiceRole e) {
        if (e.getProgramActivity() != null) {
            this.programActivityId = e.getProgramActivity().getId();
            if (e.getProgramActivity().getProgram() != null) {
                this.programId = e.getProgramActivity().getProgram().getId();
            }
        }
        if (e.getRole() != null) {
            this.roleUuid = e.getRole().getUuid();
        }
        if (e.getLifeCycleStatus() != null) {
            this.lifeCycleStatus = e.getLifeCycleStatus().name();
        }
        // Pull group UUIDs via convenience accessor on the entity
        List<String> fromEntity = e.getGroupUuids();
        if (fromEntity != null && !fromEntity.isEmpty()) {
            // dedupe + drop blanks
            this.groupUuids = fromEntity.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public UserServiceRole toEntity() {
        UserServiceRole e = new UserServiceRole();

        // life cycle
        LifeCycleStatus lcs = LifeCycleStatus.ACTIVE;
        if (this.lifeCycleStatus != null && !this.lifeCycleStatus.isBlank()) {
            lcs = LifeCycleStatus.valueOf(this.lifeCycleStatus);
        }
        e.setLifeCycleStatus(lcs);

        // FK stubs
        if (this.programActivityId != null) {
            ProgramActivity pa = new ProgramActivity();
            pa.setId(this.programActivityId);
            e.setProgramActivity(pa);
        }
        if (this.roleUuid != null && !this.roleUuid.isBlank()) {
            Role r = new Role();
            r.setUuid(this.roleUuid);
            e.setRole(r);
        }

        // Push groups into the entity via helper (creates link rows later)
        if (this.groupUuids != null) {
            List<String> cleaned = this.groupUuids.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            e.setGroupUuids(cleaned);
        } else {
            e.setGroupUuids(new ArrayList<>());
        }

        return e;
    }
}
