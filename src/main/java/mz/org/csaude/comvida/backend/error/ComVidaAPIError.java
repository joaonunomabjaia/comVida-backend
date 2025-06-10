package mz.org.csaude.comvida.backend.error;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mz.org.csaude.comvida.backend.api.RestAPIResponse;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class ComVidaAPIError implements RestAPIResponse {
    private int status;
    private String error;
    private String message;

    public ComVidaAPIError(int code, String message) {
    }
}