package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.ProgramService;

@Getter
@Setter
@Serdeable
public class ProgramServiceDTO extends BaseEntityDTO {

    private String uuid;
    private ProgramDTO program;
    private String serviceName;

    public ProgramServiceDTO() {}

    public ProgramServiceDTO(ProgramService programService) {
        this.uuid = programService.getUuid();
        this.program = programService.getProgram() != null ? new ProgramDTO(programService.getProgram()) : null;
        this.serviceName = programService.getServiceName();
    }
}
