package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.HomeVisit;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class HomeVisitDTO extends BaseEntityDTO {

    private String uuid;
    private AllocationDTO allocation;
    private CohortMemberDTO cohortMember;
    private Integer visitNumber;
    private Date visitDate;
    private String result;
    private String notes;
    private String form;

    public HomeVisitDTO() {}

    public HomeVisitDTO(HomeVisit homeVisit) {
        this.uuid = homeVisit.getUuid();
        this.allocation = homeVisit.getAllocation() != null ? new AllocationDTO(homeVisit.getAllocation()) : null;
        this.cohortMember = homeVisit.getCohortMember() != null ? new CohortMemberDTO(homeVisit.getCohortMember()) : null;
        this.visitNumber = homeVisit.getVisitNumber();
        this.visitDate = homeVisit.getVisitDate();
        this.result = homeVisit.getResult();
        this.notes = homeVisit.getNotes();
        this.form = homeVisit.getForm();
    }

    @Override
    public BaseEntity toEntity() {
        return null;
    }
}
