package mz.org.csaude.comvida.backend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Serdeable
public class UserDTO extends PersonDTO {

    private String username;
    private String password;
    private String status;
    private Boolean shouldResetPassword;
    private String salt;

    // Agora é uma string JSON
    private String attributes;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Creator
    public UserDTO() {
        super();
    }

    public UserDTO(User user) {
        super(user);

        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.shouldResetPassword = user.getShouldResetPassword();
        this.salt = user.getSalt();
        this.attributes = user.getAttributes(); // agora é String

        this.setCreatedAt(user.getCreatedAt());
        this.setCreatedBy(user.getCreatedBy());
        this.setUpdatedAt(user.getUpdatedAt());
        this.setUpdatedBy(user.getUpdatedBy());
        if (user.getLifeCycleStatus() != null) {
            this.setLifeCycleStatus(user.getLifeCycleStatus().name());
        }
    }

    @Override
    public User toEntity() {
        User user = new User();

        user.setId(this.getId());
        user.setUuid(this.getUuid());
        user.setCreatedAt(this.getCreatedAt());
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedAt(this.getUpdatedAt());
        user.setUpdatedBy(this.getUpdatedBy());
        user.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));

        user.setSex(this.getSex());
        user.setBirthdate(this.getBirthdate() != null ? new Date(this.getBirthdate().getTime()) : null);

        // Conversões de JSON
        try {
            user.setNames(mapper.writeValueAsString(this.getNames()));
            user.setAddress(mapper.writeValueAsString(this.getAddress()));
            user.setPersonAttributes(mapper.writeValueAsString(this.getPersonAttributes()));
        } catch (JsonProcessingException e) {
            user.setNames("[]");
            user.setAddress("[]");
            user.setPersonAttributes("{}");
        }

        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setStatus(this.status);
        user.setShouldResetPassword(this.shouldResetPassword);
        user.setSalt(this.salt);
        user.setAttributes(this.attributes); // já é String

        return user;
    }

    // Métodos auxiliares opcionais
    public Map<String, Object> getAttributesAsMap() {
        if (this.attributes == null) return new HashMap<>();
        try {
            return mapper.readValue(this.attributes, Map.class);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    public void setAttributesAsMap(Map<String, Object> map) {
        try {
            this.attributes = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            this.attributes = "{}";
        }
    }
}
