package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.Person;

@Entity
@Table(name = "patients")
@Getter
@Setter
@Serdeable
public class Patient extends Person {

    @Column(name = "patient_identifier", columnDefinition = "json")
    private String patientIdentifier;

    @Column(length = 50)
    private String status;
}