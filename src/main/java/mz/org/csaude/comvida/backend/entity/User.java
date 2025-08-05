package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.io.IOException;
import java.util.HashMap;
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


    @Column(columnDefinition = "json")
    private String attributes;


    public boolean isActive() {
        return this.getLifeCycleStatus().equals(mz.org.fgh.mentoring.util.LifeCycleStatus.ACTIVE);
    }

    public List<UserGroupRole> getUserGroupRoles() {
        return null;
    }

    @Transient
    public Map<String, Object> getAttributesAsMap() {
        if (this.attributes == null) return new HashMap<>();
        try {
            return new ObjectMapper().readValue(this.attributes, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler JSON de atributos", e);
        }
    }

    @Transient
    public void setAttributesAsMap(Map<String, Object> map) {
        try {
            this.attributes = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao salvar JSON de atributos", e);
        }
    }

}
