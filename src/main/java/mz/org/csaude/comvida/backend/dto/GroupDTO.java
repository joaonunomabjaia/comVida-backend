package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Group;

@Getter
@Setter
@Serdeable
public class GroupDTO extends BaseEntityDTO {

    private String name;
    private String description;
    private ProgramActivityDTO programActivity;

    @Creator
    public GroupDTO() {
    }

    public GroupDTO(Group group) {
        super(group);
        this.name = group.getName();
        this.description = group.getDescription();
        if (group.getProgramActivity() != null) {
            this.programActivity = new ProgramActivityDTO(group.getProgramActivity());
        }
    }

    public Group toEntity() {
        Group group = new Group();
        group.setId(this.getId());
        group.setUuid(this.getUuid());
        group.setName(this.getName());
        group.setDescription(this.getDescription());
        group.setProgramActivity(this.programActivity.toEntity());
        group.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        group.setCreatedAt(this.getCreatedAt());
        group.setCreatedBy(this.getCreatedBy());
        group.setUpdatedAt(this.getUpdatedAt());
        group.setUpdatedBy(this.getUpdatedBy());
        return group;
    }
}
