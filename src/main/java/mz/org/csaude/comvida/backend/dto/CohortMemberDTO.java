package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.CohortMember;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class CohortMemberDTO extends BaseEntityDTO {

    private String uuid;
    private CohortDTO cohort;
    private PatientDTO patient;
    private SourceTypeDTO sourceType;

    private String originId;
    private Date inclusionDate;
    private Date exclusionDate;

    public CohortMemberDTO() {}

    public CohortMemberDTO(CohortMember member) {
        this.uuid = member.getUuid();
        this.cohort = member.getCohort() != null ? new CohortDTO(member.getCohort()) : null;
        this.patient = member.getPatient() != null ? new PatientDTO(member.getPatient()) : null;
        this.sourceType = member.getCohortMemberSource() != null ? new SourceTypeDTO(member.getCohortMemberSource()) : null;
        this.originId = member.getOriginId();
        this.inclusionDate = member.getInclusionDate();
        this.exclusionDate = member.getExclusionDate();
        // Herda de BaseEntityDTO: setCreatedAt(), setUpdatedAt(), etc., se houver
    }
}
