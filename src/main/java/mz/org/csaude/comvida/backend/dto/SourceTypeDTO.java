package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.CohortMemberSource;

@Getter
@Setter
@Serdeable
public class SourceTypeDTO extends BaseEntityDTO {

    private String uuid;
    private String name;

    public SourceTypeDTO() {}

    public SourceTypeDTO(CohortMemberSource cohortMemberSource) {
        this.uuid = cohortMemberSource.getUuid();
        this.name = cohortMemberSource.getName();
    }
}
