package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.User;

@Getter
@Setter
@Serdeable
public class UserDTO extends BaseEntityDTO {

    private String uuid;
    private PersonDTO person;
    private String username;
    private String password;
    private String status;

    public UserDTO() {}

    public UserDTO(User user) {
        this.uuid = user.getUuid();
        this.person = user.getPerson() != null ? new PersonDTO(user.getPerson()) : null;
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();
    }
}
