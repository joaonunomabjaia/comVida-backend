package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "program_services")
public class ProgramService extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(name = "service_name")
    private String serviceName;
}
