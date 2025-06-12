package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Patient;

@Getter
@Setter
@Serdeable
public class PatientDTO extends BaseEntityDTO {

    private String uuid;

    // Dados herdados de Person
    private String names;
    private String sex;
    private String birthdate;
    private String address;
    private String personAttributes;

    // Dados próprios de Patient
    private String patientIdentifier;
    private String status;

    public PatientDTO() {}

    public PatientDTO(Patient patient) {
        this.uuid = patient.getUuid();
        this.names = patient.getNames();
        this.sex = patient.getSex();
        this.birthdate = patient.getBirthdate() != null ? patient.getBirthdate().toString() : null;
        this.address = patient.getAddress();
        this.personAttributes = patient.getPersonAttributes();
        this.patientIdentifier = patient.getPatientIdentifier();
        this.status = patient.getStatus();
    }

    @Override
    public BaseEntity toEntity() {
        return null;
    }
}
