package mz.org.csaude.comvida.backend.dto.imports;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Aggregated validation errors for a specific input row. */
@Getter
@AllArgsConstructor
@Serdeable
public class ImportErrorItemDTO {
    /** 0-based index of the row in the received array. */
    private final int index;

    /** Username provided on that row (may be null). */
    private final String username;

    /** Human-readable validation messages. */
    private final List<String> messages;
}
