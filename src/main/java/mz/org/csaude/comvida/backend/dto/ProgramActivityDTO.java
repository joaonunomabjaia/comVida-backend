package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.util.Utilities;

@Getter
@Setter
@Serdeable
public class ProgramActivityDTO extends BaseEntityDTO {

    private String name;
    private ProgramDTO program;

    @Creator
    public ProgramActivityDTO() {
    }

    public ProgramActivityDTO(ProgramActivity activity) {
        super(activity);
        this.name = activity.getName();
        if (activity.getProgram() != null) {
            this.program = new ProgramDTO(activity.getProgram());
        }
    }

    public ProgramActivity toEntity() {
        ProgramActivity activity = new ProgramActivity();
        activity.setId(this.getId());
        activity.setUuid(this.getUuid());
        activity.setName(this.getName());
        if (Utilities.stringHasValue(this.getLifeCycleStatus())) activity.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));

        if (this.program != null) {
            activity.setProgram(this.program.toEntity());
        }

        return activity;
    }
}
