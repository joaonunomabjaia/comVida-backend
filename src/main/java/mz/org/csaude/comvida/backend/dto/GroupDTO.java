package mz.org.csaude.comvida.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Group;

@Getter
@Setter
@NoArgsConstructor
public class GroupDTO extends BaseEntityDTO {

    private String name;
    private String description;

    public GroupDTO(Group group) {
        super(group);
        this.name = group.getName();
        this.description = group.getDescription();
    }

    public Group toEntity() {
        Group group = new Group();
        group.setId(this.getId());
        group.setUuid(this.getUuid());
        group.setName(this.getName());
        group.setDescription(this.getDescription());
        group.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        group.setCreatedAt(this.getCreatedAt());
        group.setCreatedBy(this.getCreatedBy());
        group.setUpdatedAt(this.getUpdatedAt());
        group.setUpdatedBy(this.getUpdatedBy());
        return group;
    }
}
