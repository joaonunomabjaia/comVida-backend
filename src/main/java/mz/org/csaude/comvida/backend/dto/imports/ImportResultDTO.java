package mz.org.csaude.comvida.backend.dto.imports;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Summary of the import operation. */
@Getter
@AllArgsConstructor
@Serdeable
public class ImportResultDTO {
    /** Number of users successfully created. */
    private final int imported;

    /** Number of rows that failed validation/persistence. */
    private final int failed;

    /** Per-row errors (empty when everything imported). */
    private final List<ImportErrorItemDTO> errors;
}
