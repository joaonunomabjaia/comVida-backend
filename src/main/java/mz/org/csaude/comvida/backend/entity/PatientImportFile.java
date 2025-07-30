package mz.org.csaude.comvida.backend.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

@Entity
@Getter
@Setter
@Serdeable.Deserializable
@Table(name = "patient_import_files")
public class PatientImportFile extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name; // Nome original ou descritivo do arquivo

    @Column(name = "file", columnDefinition = "bytea")
    private byte[] file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImportStatus status;
    // PENDING, PROCESSING, DONE, FAILED

    @Column(nullable = false)
    private Integer progress = 0; // 0 a 100 (% do processamento)

    @Column(length = 500)
    private String message; // Mensagem de erro ou resumo da importação

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "source_system_id", nullable = false)
    private SourceSystem sourceSystem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_activity_id", nullable = false)
    private ProgramActivity programActivity;

    public enum ImportStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED,
//        PARTIALLY_FAILED
    }

}
