package mz.org.csaude.comvida.backend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Person;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.*;

@Getter
@Setter
@Serdeable
public class PersonDTO extends BaseEntityDTO {

    private List<Map<String, Object>> names;
    private List<Map<String, Object>> address;
    private String sex;
    private Date birthdate;
    private Map<String, Object> personAttributes;

    private String firstName;
    private String lastName;
    private String fullName;
    private String addressLine1;
    private String city;
    private String district;
    private String province;
    private String fullAddress;

    private static final ObjectMapper mapper = new ObjectMapper();

    public PersonDTO() {}

    public PersonDTO(Person person) {
        super(person);

        // Conversões JSON -> Objeto
        this.names = parseList(person.getNames());
        this.address = parseList(person.getAddress());
        this.personAttributes = parseMap(person.getPersonAttributes());

        this.sex = person.getSex();
        this.birthdate = person.getBirthdate();

        if (names != null && !names.isEmpty()) {
            Map<String, Object> first = names.get(0);
            this.firstName = String.valueOf(first.getOrDefault("firstName", ""));
            this.lastName = String.valueOf(first.getOrDefault("lastName", ""));
            this.fullName = String.format("%s %s", firstName, lastName).trim();
        }

        if (address != null && !address.isEmpty()) {
            Map<String, Object> addr = address.get(0);
            this.addressLine1 = String.valueOf(addr.getOrDefault("addressLine1", ""));
            this.city = String.valueOf(addr.getOrDefault("city", ""));
            this.district = String.valueOf(addr.getOrDefault("district", ""));
            this.province = String.valueOf(addr.getOrDefault("province", ""));
            this.fullAddress = String.join(", ",
                    Arrays.asList(addressLine1, city, district, province)
                            .stream().filter(s -> s != null && !s.isBlank()).toList());
        }
    }

    @Override
    public Person toEntity() {
        Person person = new Person();

        person.setId(this.getId());
        person.setUuid(this.getUuid());
        person.setSex(this.sex);
        person.setBirthdate(this.birthdate);

        // Conversões Objeto -> JSON
        person.setNames(writeJson(names));
        person.setAddress(writeJson(address));
        person.setPersonAttributes(writeJson(personAttributes));

        person.setCreatedAt(this.getCreatedAt());
        person.setCreatedBy(this.getCreatedBy());
        person.setUpdatedAt(this.getUpdatedAt());
        person.setUpdatedBy(this.getUpdatedBy());

        if (this.getLifeCycleStatus() != null) {
            person.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
        }

        return person;
    }

    // Utilitários JSON
    private List<Map<String, Object>> parseList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return mapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private String writeJson(Object obj) {
        try {
            return obj != null ? mapper.writeValueAsString(obj) : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
