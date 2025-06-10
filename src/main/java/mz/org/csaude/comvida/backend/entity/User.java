package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @Column(length = 255)
    private String salt;

    public boolean isActive() {
        return this.getLifeCycleStatus().equals(mz.org.fgh.mentoring.util.LifeCycleStatus.ACTIVE);
    }

    public List<UserGroupRole> getUserGroupRoles() {
        return null;
    }
}
