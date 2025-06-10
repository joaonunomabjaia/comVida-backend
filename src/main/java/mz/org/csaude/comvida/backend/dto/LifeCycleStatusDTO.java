package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

@Getter
@Setter
@Serdeable
public class LifeCycleStatusDTO {

    @NotNull
    private LifeCycleStatus lifeCycleStatus;
}
