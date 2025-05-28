package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
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
}
