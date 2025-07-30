package mz.org.csaude.comvida.backend.dto;

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
@NoArgsConstructor
@Serdeable
public class CohortDTO extends BaseEntityDTO {

    private String name;
    private String description;
    private ProgramActivityDTO programActivity;
    private LocalDate inclusionDate;
    private LocalDate exclusionDate;
    private LocalDate memberCreatedAt;


    public CohortDTO(Cohort cohort) {
        super(cohort);
        this.name = cohort.getName();
        this.description = cohort.getDescription();
        if (cohort.getProgramActivity() != null) {
            this.programActivity = new ProgramActivityDTO(cohort.getProgramActivity());
        }
    }

    public Cohort toEntity() {
        Cohort cohort = new Cohort();
        cohort.setId(this.getId());
        cohort.setUuid(this.getUuid());
        cohort.setName(this.getName());
        cohort.setDescription(this.getDescription());
        cohort.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        cohort.setCreatedAt(this.getCreatedAt());
        cohort.setCreatedBy(this.getCreatedBy());
        cohort.setUpdatedAt(this.getUpdatedAt());
        cohort.setUpdatedBy(this.getUpdatedBy());
        if (this.programActivity != null) {
            cohort.setProgramActivity(this.programActivity.toEntity());
        }
        return cohort;
    }
}
