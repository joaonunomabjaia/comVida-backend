package mz.org.csaude.comvida.backend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User DTO:
 * - attributes: List<Map<...>>  (matches JSONB array on the entity)
 * - userServiceRoles: top-level list (NOT inside attributes)
 */
@Getter
@Setter
@Serdeable
public class UserDTO extends PersonDTO {

    private String username;
    private String password;              // only set when provided by UI
    private String status;                // e.g. "ACTIVE" / "INACTIVE"
    private Boolean shouldResetPassword;
    private String salt;

    /** JSONB attributes (array of objects) */
    private List<Map<String, Object>> attributes = new ArrayList<>();

    /** Top-level roles from UI */
    private List<UserServiceRoleDTO> userServiceRoles = new ArrayList<>();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Creator
    public UserDTO() { super(); }

    public UserDTO(User user) {
        super(user);

        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.shouldResetPassword = user.getShouldResetPassword();
        this.salt = user.getSalt();

        // JSONB -> list of maps
        this.attributes = user.getAttributesAsMap();

        // lifecycle/status
        if (user.getLifeCycleStatus() != null) {
            this.setLifeCycleStatus(user.getLifeCycleStatus().name());
        }
        this.setCreatedAt(user.getCreatedAt());
        this.setCreatedBy(user.getCreatedBy());
        this.setUpdatedAt(user.getUpdatedAt());
        this.setUpdatedBy(user.getUpdatedBy());

        // entity roles -> DTO roles
        if (user.getUserServiceRoles() != null) {
            this.userServiceRoles = user.getUserServiceRoles().stream()
                    .map(UserServiceRoleDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public User toEntity() {
        User user = new User();

        // BaseEntity / audit
        user.setId(this.getId());
        user.setUuid(this.getUuid());
        user.setCreatedAt(this.getCreatedAt());
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedAt(this.getUpdatedAt());
        user.setUpdatedBy(this.getUpdatedBy());

        // Lifecycle + status
        LifeCycleStatus lcs = LifeCycleStatus.ACTIVE;
        if (this.getLifeCycleStatus() != null && !this.getLifeCycleStatus().isBlank()) {
            lcs = LifeCycleStatus.valueOf(this.getLifeCycleStatus());
        }
        user.setLifeCycleStatus(lcs);
        user.setStatus(this.status != null ? this.status : lcs.name());

        // Person fields
        user.setSex(this.getSex());
        user.setBirthdate(this.getBirthdate() != null ? new Date(this.getBirthdate().getTime()) : null);

        // Person JSON lists -> JSON strings for entity
        try {
            user.setNames(MAPPER.writeValueAsString(this.getNames() != null ? this.getNames() : List.of()));
            user.setAddress(MAPPER.writeValueAsString(this.getAddress() != null ? this.getAddress() : List.of()));
            user.setPersonAttributes(MAPPER.writeValueAsString(this.getPersonAttributes() != null ? this.getPersonAttributes() : List.of()));
        } catch (JsonProcessingException e) {
            user.setNames("[]");
            user.setAddress("[]");
            user.setPersonAttributes("[]");
        }

        // User fields
        user.setUsername(this.username);
        user.setPassword(this.password); // service/controller may handle blank on update
        user.setShouldResetPassword(this.shouldResetPassword);
        user.setSalt(this.salt);

        // JSONB attributes (array)
        user.setAttributesAsMap(this.attributes != null ? this.attributes : new ArrayList<>());

        // ✅ DTO roles -> entity roles as a Set (entity expects Set<UserServiceRole>)
        if (this.userServiceRoles != null && !this.userServiceRoles.isEmpty()) {
            Set<UserServiceRole> roles =
                    this.userServiceRoles.stream()
                            .map(UserServiceRoleDTO::toEntity)
                            .peek(r -> r.setUser(user)) // backref
                            .collect(Collectors.toCollection(LinkedHashSet::new));
            user.setUserServiceRoles(roles);
        } else {
            user.setUserServiceRoles(new LinkedHashSet<>());
        }

        return user;
    }
}
