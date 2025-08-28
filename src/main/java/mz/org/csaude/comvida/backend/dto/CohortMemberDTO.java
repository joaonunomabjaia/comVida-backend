package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.util.SourceTypeEnum;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class CohortMemberDTO extends BaseEntityDTO {

    private String uuid;

    private CohortDTO cohort;
    private PatientDTO patient;
    private SourceSystemDTO sourceSystem;

    private String originId;
    private Date inclusionDate;
    private Date exclusionDate;

    private SourceTypeEnum sourceType;
    private PatientImportFileDTO patientImportFile;
    private GroupDTO group;
    private UserDTO assignedByUser;

    public CohortMemberDTO() {}

    public CohortMemberDTO(CohortMember member) {
        super(member);
        this.uuid = member.getUuid();

        this.cohort = member.getCohort() != null ? new CohortDTO(member.getCohort()) : null;
        this.patient = member.getPatient() != null ? new PatientDTO(member.getPatient()) : null;
        this.sourceSystem = member.getSourceSystem() != null ? new SourceSystemDTO(member.getSourceSystem()) : null;
        this.assignedByUser = member.getAssignedBy() != null ? new UserDTO(member.getAssignedBy()) : null;

        this.originId = member.getOriginId();
        this.inclusionDate = member.getInclusionDate();
        this.exclusionDate = member.getExclusionDate();

        this.sourceType = member.getSourceType();
        this.patientImportFile = member.getPatientImportFile() != null ? new PatientImportFileDTO(member.getPatientImportFile()) : null;
        this.group = member.getGroup() != null ? new GroupDTO(member.getGroup()) : null;

    }

    @Override
    public BaseEntity toEntity() {
        // Método de conversão opcional. Se for implementar, cuidado com recursividade e fetchs incompletos.
        return null;
    }
}
