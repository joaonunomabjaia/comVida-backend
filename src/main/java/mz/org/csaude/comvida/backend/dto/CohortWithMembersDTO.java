package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.entity.CohortMember;

import java.util.List;

@Getter
@Setter
@Serdeable
public class CohortWithMembersDTO {

    private Cohort cohort;
    private List<CohortMember> members;

    public CohortWithMembersDTO(Cohort cohort, List<CohortMember> members) {
        this.cohort = cohort;
        this.members = members;
    }

}
