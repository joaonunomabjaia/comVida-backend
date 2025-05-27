package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Program;

@Getter
@Setter
@Serdeable
public class ProgramDTO extends BaseEntityDTO {

    private String uuid;
    private String name;

    public ProgramDTO() {}

    public ProgramDTO(Program program) {
        this.uuid = program.getUuid();
        this.name = program.getName();
    }
}
