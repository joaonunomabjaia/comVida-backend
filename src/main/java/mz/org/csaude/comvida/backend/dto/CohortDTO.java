package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Serdeable
public class CohortDTO extends BaseEntityDTO {

    private String name;
    private String description;
    private ProgramActivityDTO programActivity;


    public CohortDTO(Cohort cohort) {
        super(cohort);
        this.name = cohort.getName();
        this.description = cohort.getDescription();
        if (cohort.getProgramActivity() != null) {
            this.programActivity = new ProgramActivityDTO(cohort.getProgramActivity());
        }
    }

    @Creator
    public CohortDTO() {
    }

    public Cohort toEntity() {
        Cohort cohort = new Cohort();
        cohort.setId(this.getId());
        cohort.setUuid(this.getUuid());
        cohort.setName(this.getName());
        cohort.setDescription(this.getDescription());
        cohort.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        if (this.programActivity != null) {
            cohort.setProgramActivity(this.programActivity.toEntity());
        }
        return cohort;
    }
}
