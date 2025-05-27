package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String status;
}
