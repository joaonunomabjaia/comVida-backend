package mz.org.csaude.comvida.backend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.Patient;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

@Getter
@Setter
@Serdeable
public class PatientDTO extends PersonDTO {

    private String patientIdentifier;
    private String status;

    private static final ObjectMapper mapper = new ObjectMapper();

    public PatientDTO() {}

    public PatientDTO(Patient patient) {
        super(patient); // Chama o construtor de PersonDTO
        this.patientIdentifier = patient.getPatientIdentifier();
        this.status = patient.getStatus();
    }

    @Override
    public Patient toEntity() {
        Patient patient = new Patient();
        patient.setUuid(this.getUuid());
        patient.setCreatedBy(this.getCreatedBy());
        patient.setCreatedAt(this.getCreatedAt());
        patient.setUpdatedBy(this.getUpdatedBy());
        patient.setUpdatedAt(this.getUpdatedAt());
        patient.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));

        // Conversão de campos herdados de Person para JSON string
        try {
            patient.setNames(mapper.writeValueAsString(this.getNames()));
            patient.setAddress(mapper.writeValueAsString(this.getAddress()));
            patient.setPersonAttributes(mapper.writeValueAsString(this.getPersonAttributes()));
        } catch (JsonProcessingException e) {
            patient.setNames("[]");
            patient.setAddress("[]");
            patient.setPersonAttributes("{}");
        }

        patient.setSex(this.getSex());
        patient.setBirthdate(this.getBirthdate() != null ? new java.sql.Date(this.getBirthdate().getTime()) : null);

        // Dados próprios
        patient.setPatientIdentifier(this.getPatientIdentifier());
        patient.setStatus(this.getStatus());

        return patient;
    }
}
