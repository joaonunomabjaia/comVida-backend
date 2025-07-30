package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Serdeable
@Table(name = "programs")
public class Program extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

}

