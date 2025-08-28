package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Introspected
@Serdeable
@Data
@Getter
@Setter
public class PatientImportScheduleRequest {

    @NotNull
    private Long cohortId;

    @NotNull
    private Long patientImportFileId;

    private String entryDate; // "DD-MM-YYYY"

    private String exitDate; // "DD-MM-YYYY"
}
