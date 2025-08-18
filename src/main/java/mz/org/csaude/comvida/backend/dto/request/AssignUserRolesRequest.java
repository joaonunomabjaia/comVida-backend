package mz.org.csaude.comvida.backend.dto.request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Introspected
@Serdeable
@Getter @Setter
public class AssignUserRolesRequest {
    /** Optional: if null/blank → escopo global (programActivity = null) */
    private String programActivityUuid;

    @NotEmpty
    private List<String> roleUuids;
}
