package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Table(name = "source_systems")
@Getter
@Setter
@Serdeable
public class SourceSystem extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code; // Ex: "OPENMRS", "IDMED"

    @Column(nullable = false, length = 255)
    private String description; // Ex: "Sistema OpenMRS" ou "Sistema IDMED"
}
