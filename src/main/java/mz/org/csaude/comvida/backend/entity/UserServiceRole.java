package mz.org.csaude.comvida.backend.entity;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Serdeable.Deserializable
@Table(name = "user_service_roles")
public class UserServiceRole extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "program_activity_id")
    private ProgramActivity programActivity;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    @Creator
    public UserServiceRole() {

    }
}
