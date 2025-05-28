package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import java.util.Date;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Person extends BaseEntity {

    @Column(columnDefinition = "json")
    private String names;

    @Column(length = 10)
    private String sex;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Column(columnDefinition = "json")
    private String address;

    @Column(name = "person_attributes", columnDefinition = "json")
    private String personAttributes;
}