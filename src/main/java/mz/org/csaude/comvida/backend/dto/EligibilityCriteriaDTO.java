package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;

@Getter
@Setter
@Serdeable
public class EligibilityCriteriaDTO extends BaseEntityDTO {

    private String uuid;
    private String criteria;

    public EligibilityCriteriaDTO() {}

    public EligibilityCriteriaDTO(EligibilityCriteria eligibilityCriteria) {
        this.uuid = eligibilityCriteria.getUuid();
        this.criteria = eligibilityCriteria.getCriteria();
    }
}
