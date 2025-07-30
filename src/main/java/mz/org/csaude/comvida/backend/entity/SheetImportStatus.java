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
@Table(name = "sheet_import_status")
public class SheetImportStatus extends BaseEntity {

    @Column(name = "sheet_name", nullable = false, length = 255)
    private String sheetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SheetStatus status;

    @Column(nullable = false)
    private Integer progress = 0; // 0 a 100

    @Column(length = 1000)
    private String message;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "import_file_id", nullable = false)
    private PatientImportFile file;

    public enum SheetStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED
    }
}
