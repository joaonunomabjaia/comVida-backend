package mz.org.csaude.comvida.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "cohorts")
public class Cohort extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Lob
    @Column(name = "template_file", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] templateFile;

}
