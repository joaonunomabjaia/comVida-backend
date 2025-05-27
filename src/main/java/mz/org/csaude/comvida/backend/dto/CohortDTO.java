package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;

@Getter
@Setter
@Serdeable
public class CohortDTO extends BaseEntityDTO {

    private String uuid;
    private String name;
    private String description;

    public CohortDTO() {}

    public CohortDTO(Cohort cohort) {
        this.uuid = cohort.getUuid();
        this.name = cohort.getName();
        this.description = cohort.getDescription();
        // Campos herdados de BaseEntityDTO, se aplicável (ex: createdAt, updatedAt)
    }
}
