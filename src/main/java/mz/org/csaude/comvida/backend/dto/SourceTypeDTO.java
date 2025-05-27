package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.SourceType;

@Getter
@Setter
@Serdeable
public class SourceTypeDTO extends BaseEntityDTO {

    private String uuid;
    private String name;

    public SourceTypeDTO() {}

    public SourceTypeDTO(SourceType sourceType) {
        this.uuid = sourceType.getUuid();
        this.name = sourceType.getName();
    }
}
