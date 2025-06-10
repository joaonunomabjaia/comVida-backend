package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Program;

@Getter
@Setter
@Serdeable
@Schema(name = "ProgramDTO", description = "DTO representing a Program")
public class ProgramDTO extends BaseEntityDTO {

    @NotEmpty(message = "The name of the program is required.")
    private String name;

    private String description;

    @Creator
    public ProgramDTO() {
        super();
    }

    public ProgramDTO(Program program) {
        super(program);
        this.name = program.getName();
        this.description = program.getDescription();
    }

    public Program toEntity() {
        Program program = new Program();
        program.setId(this.getId());
        program.setUuid(this.getUuid());
        program.setName(this.getName());
        program.setDescription(this.getDescription());
        //program.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        program.setCreatedAt(this.getCreatedAt());
        program.setCreatedBy(this.getCreatedBy());
        program.setUpdatedAt(this.getUpdatedAt());
        program.setUpdatedBy(this.getUpdatedBy());
        return program;
    }
}
