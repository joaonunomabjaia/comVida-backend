package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Patient;

@Getter
@Setter
@Serdeable
public class PatientDTO extends BaseEntityDTO {

    private String uuid;
    private PersonDTO person;
    private String patientIdentifier;
    private String status;

    public PatientDTO() {}

    public PatientDTO(Patient patient) {
        this.uuid = patient.getUuid();
        this.person = patient.getPerson() != null ? new PersonDTO(patient.getPerson()) : null;
        this.patientIdentifier = patient.getPatientIdentifier();
        this.status = patient.getStatus();
    }
}
