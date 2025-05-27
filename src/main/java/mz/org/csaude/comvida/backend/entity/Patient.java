package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "patients")
public class Patient extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "patient_identifier", columnDefinition = "json")
    private String patientIdentifier;

    @Column(length = 50)
    private String status;
}