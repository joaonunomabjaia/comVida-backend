package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;

@Getter
@Setter
@Serdeable
public class EligibilityCriteriaDTO extends BaseEntityDTO {
    private String criteria;
    private String description;
    private ProgramActivityDTO programActivity;

    public EligibilityCriteriaDTO() {}

    public EligibilityCriteriaDTO(EligibilityCriteria eligibilityCriteria) {
        super(eligibilityCriteria);
        this.criteria = eligibilityCriteria.getCriteria();
        this.description = eligibilityCriteria.getDescription();
        this.programActivity = new ProgramActivityDTO(eligibilityCriteria.getProgramActivity());
    }

    @Override
    public BaseEntity toEntity() {
        EligibilityCriteria entity = new EligibilityCriteria();
        entity.setId(this.getId());
        entity.setCriteria(this.criteria);
        entity.setDescription(this.description);

        if (this.programActivity != null) {
            entity.setProgramActivity((ProgramActivity) this.programActivity.toEntity());
        }

        return entity;
    }
}
