package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.SheetImportStatus;
import mz.org.csaude.comvida.backend.entity.SheetImportStatus.SheetStatus;

@Getter
@Setter
@Serdeable
@NoArgsConstructor
public class SheetImportStatusDTO extends BaseEntityDTO {

    @NotEmpty
    private String sheetName;

    @NotNull
    private SheetStatus status;

    @NotNull
    private Integer progress;

    private String message;

    private Long fileId;

    @Creator
    public SheetImportStatusDTO(SheetImportStatus entity) {
        super(entity);
        this.sheetName = entity.getSheetName();
        this.status = entity.getStatus();
        this.progress = entity.getProgress();
        this.message = entity.getMessage();
        this.fileId = entity.getFile() != null ? entity.getFile().getId() : null;
    }

    public SheetImportStatus toEntity() {
        SheetImportStatus entity = new SheetImportStatus();
        entity.setId(this.getId());
        entity.setUuid(this.getUuid());
        entity.setCreatedAt(this.getCreatedAt());
        entity.setCreatedBy(this.getCreatedBy());
        entity.setUpdatedAt(this.getUpdatedAt());
        entity.setUpdatedBy(this.getUpdatedBy());

        entity.setSheetName(this.sheetName);
        entity.setStatus(this.status);
        entity.setProgress(this.progress);
        entity.setMessage(this.message);
        // file deve ser setado manualmente no service
        return entity;
    }
}
