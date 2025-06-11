package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.CohortMemberCategory;

@Getter
@Setter
@Serdeable
public class CohortMemberCategoryDTO extends BaseEntityDTO {

    private String uuid;
    private CohortMemberDTO cohortMember;
    private EligibilityCriteriaDTO eligibilityCriteria;

    public CohortMemberCategoryDTO() {}

    public CohortMemberCategoryDTO(CohortMemberCategory cmc) {
        this.uuid = cmc.getUuid();
        this.cohortMember = cmc.getCohortMember() != null ? new CohortMemberDTO(cmc.getCohortMember()) : null;
        this.eligibilityCriteria = cmc.getEligibilityCriteria() != null ? new EligibilityCriteriaDTO(cmc.getEligibilityCriteria()) : null;
    }

    @Override
    public BaseEntity toEntity() {
        return null;
    }
}
