package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.Person;

@Entity
@Getter
@Setter
@Table(name = "patients")
@PrimaryKeyJoinColumn(name = "id") // Garante que ID de Patient será o mesmo de Person
public class Patient extends Person {

    @Column(name = "patient_identifier", columnDefinition = "json")
    private String patientIdentifier;

    @Column(length = 50)
    private String status;
}