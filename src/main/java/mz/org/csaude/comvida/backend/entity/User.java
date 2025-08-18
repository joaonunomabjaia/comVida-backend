package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.fgh.mentoring.util.LifeCycleStatus;
import org.hibernate.annotations.ColumnTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private String status;

    @Column(name = "should_reset_password")
    private Boolean shouldResetPassword = false;

    @Column
    private String salt;


    @Column(columnDefinition = "jsonb")              // <- use jsonb
    @ColumnTransformer(write = "?::jsonb")           // <- faz o cast na escrita
    private String attributes;


    public boolean isActive() {
        return this.getStatus().equals(String.valueOf(LifeCycleStatus.ACTIVE));
    }

    public List<UserServiceRole> getUserGroupRoles() {
        return null;
    }

    @Transient @JsonIgnore
    public List<Map<String, Object>> getAttributesAsMap() {
        if (this.attributes == null || this.attributes.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(this.attributes,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String,Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transient @JsonIgnore
    public void setAttributesAsMap(List<Map<String, Object>> map) {
        try {
            this.attributes = objectMapper.writeValueAsString(map != null ? map : new ArrayList<>());
        } catch (Exception e) {
            this.attributes = "[]";
        }
    }

}
