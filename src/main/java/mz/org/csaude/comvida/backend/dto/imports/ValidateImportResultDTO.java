package mz.org.csaude.comvida.backend.dto.imports;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Serdeable
public class ValidateImportResultDTO {
    private final List<ImportErrorItemDTO> errors; // index (0-based) + messages
}
