package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Tag;

@Getter
@Setter
@Serdeable
public class TagDTO extends BaseEntityDTO {

    private String uuid;
    private String shortName;
    private String description;

    public TagDTO() {}

    public TagDTO(Tag tag) {
        this.uuid = tag.getUuid();
        this.shortName = tag.getShortName();
        this.description = tag.getDescription();
    }
}
