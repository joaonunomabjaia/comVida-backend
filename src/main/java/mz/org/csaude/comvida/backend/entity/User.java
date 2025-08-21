package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.fgh.mentoring.util.LifeCycleStatus;
import org.hibernate.annotations.ColumnTransformer;

import java.util.*;

/**
 * User entity extending Person.
 * <p>
 * Notes:
 * - {@code attributes} is stored as JSONB and represents an ARRAY of objects.
 * - Use {@link #getAttributesAsMap()} and {@link #setAttributesAsMap(List)} to
 *   work with attributes as a typed structure.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Serdeable.Deserializable
public class User extends Person {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String status;                   // e.g. "ACTIVE" / "INACTIVE"

    @Column(name = "should_reset_password")
    private Boolean shouldResetPassword = false;

    @Column
    private String salt;

    /**
     * JSONB column that stores an ARRAY of objects. Example:
     * [
     *   { "integratedSystem": 2, "idOnIntegratedSystem": "123456" },
     *   { "userServiceRoles": [ { ... }, { ... } ] }
     * ]
     */
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String attributes;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserServiceRole> userServiceRoles = new LinkedHashSet<>();

    // Local object mapper for helpers below
    @Transient
    @JsonIgnore
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /* ---------------------------- Convenience ---------------------------- */

    /**
     * True when either the entity's lifecycle or the status string is ACTIVE.
     */
    @JsonIgnore
    public boolean isActive() {
        final LifeCycleStatus lcs = getLifeCycleStatus();
        if (lcs == LifeCycleStatus.ACTIVE) return true;
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    /**
     * Read the JSONB {@code attributes} as a list of maps.
     * Never returns {@code null}.
     */
    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getAttributesAsMap() {
        if (this.attributes == null || this.attributes.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(
                    this.attributes,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
        } catch (Exception e) {
            // Fallback to empty list on parse error
            return new ArrayList<>();
        }
    }

    /**
     * Write the given list to the JSONB {@code attributes} field.
     * If {@code map} is null, stores an empty array ("[]").
     */
    @Transient
    @JsonIgnore
    public void setAttributesAsMap(List<Map<String, Object>> map) {
        try {
            this.attributes = MAPPER.writeValueAsString(
                    map != null ? map : new ArrayList<>()
            );
        } catch (Exception e) {
            this.attributes = "[]";
        }
    }
}
