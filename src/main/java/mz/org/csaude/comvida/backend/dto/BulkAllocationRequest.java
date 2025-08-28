package mz.org.csaude.comvida.backend.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.util.List;

@Data
@Introspected
@Serdeable
public class BulkAllocationRequest {
    private List<Long> memberIds;
    private Long assignedByUserId;
}
