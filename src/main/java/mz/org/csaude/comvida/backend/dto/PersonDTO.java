package mz.org.csaude.comvida.backend.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Person;

import java.util.Date;

@Getter
@Setter
@Serdeable
public class PersonDTO extends BaseEntityDTO {

    private String uuid;
    private String names;
    private String sex;
    private Date birthdate;
    private String address;
    private String personAttributes;

    public PersonDTO() {}

    public PersonDTO(Person person) {
        this.uuid = person.getUuid();
        this.names = person.getNames();
        this.sex = person.getSex();
        this.birthdate = person.getBirthdate();
        this.address = person.getAddress();
        this.personAttributes = person.getPersonAttributes();
    }
}
