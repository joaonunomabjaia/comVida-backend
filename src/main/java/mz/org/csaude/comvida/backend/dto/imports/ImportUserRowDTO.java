package mz.org.csaude.comvida.backend.dto.imports;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * One row from the import file.
 * integratedSystem can be a SourceSystem code or description.
 * idOnIntegratedSystem is required when integratedSystem is provided.
 */
@Getter @Setter
@NoArgsConstructor
@Serdeable
public class ImportUserRowDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Apelido é obrigatório")
    private String surname;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username inválido (permitido: letras, números, '.', '_' e '-')")
    private String username;

    /** Code or description of a SourceSystem. Empty/null = none. */
    private String integratedSystem;

    /** Required if integratedSystem is provided. */
    private String idOnIntegratedSystem;

    @Email(message = "Email inválido")
    private String email;

    private String phone;
}
