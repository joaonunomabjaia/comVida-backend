package mz.org.csaude.comvida.backend.dto.request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Introspected
@Serdeable
@Getter @Setter
public class ReplaceUserRolesRequest {
    /** Optional: if null/blank → escopo global (programActivity = null) */
    private String programActivityUuid;

    /** New complete set to keep for this scope (can be empty to remove all) */
    @NotNull
    private List<String> roleUuids;
}
