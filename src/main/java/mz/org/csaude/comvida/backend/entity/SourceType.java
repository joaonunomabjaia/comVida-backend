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
@Table(name = "source_type")
public class SourceType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code; // FILE, INTEGRATION, MANUAL

    @Column(nullable = false)
    private String description;
}

