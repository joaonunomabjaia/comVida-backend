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

    @Lob
    @Column(name = "file", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] file; // Conteúdo binário do arquivo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImportStatus status;
    // PENDING, PROCESSING, DONE, FAILED

    @Column(nullable = false)
    private Integer progress = 0; // 0 a 100 (% do processamento)

    @Column(length = 500)
    private String message; // Mensagem de erro ou resumo da importação

    public enum ImportStatus {
        PENDING,
        PROCESSING,
        DONE,
        FAILED
    }

}
