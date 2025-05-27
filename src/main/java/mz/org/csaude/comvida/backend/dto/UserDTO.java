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
    private String username;
    private String password;
    private String status;

    // Atributos herdados de Person
    private String names;
    private String sex;
    private String birthdate;
    private String address;
    private String personAttributes;

    public UserDTO() {}

    public UserDTO(User user) {
        this.uuid = user.getUuid();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();

        // Campos herdados de Person
        this.names = user.getNames();
        this.sex = user.getSex();
        this.birthdate = user.getBirthdate() != null ? user.getBirthdate().toString() : null;
        this.address = user.getAddress();
        this.personAttributes = user.getPersonAttributes();
    }
}
