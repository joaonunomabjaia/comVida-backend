package mz.org.csaude.comvida.backend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntityDTO;
import mz.org.csaude.comvida.backend.entity.Person;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.*;

/**
 * PersonDTO
 * - Entidade Person guarda JSON como String; aqui expomos como List<Map<String,Object>>.
 * - fullName é SEMPRE a concatenação do nome preferido (firstName/givenName + lastName/familyName).
 * - A flag do preferido aceita "preferred", "prefered", "isPreferred", "primary", "default", "preferido", "principal".
 */
@Getter
@Setter
@Serdeable
public class PersonDTO extends BaseEntityDTO {

    /** JSON arrays em formato estruturado */
    private List<Map<String, Object>> names;
    private List<Map<String, Object>> address;
    private List<Map<String, Object>> personAttributes;

    private String sex;
    private Date birthdate;

    /** Campos derivados/achatados (conveniência) */
    private String firstName;
    private String lastName;
    private String fullName;
    private String addressLine1;
    private String city;
    private String district;
    private String province;
    private String fullAddress;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> LIST_OF_MAPS = new TypeReference<>() {};
    private static final List<String> PREFERRED_FLAGS = List.of(
            "preferred", "prefered", "isPreferred", "primary", "default", "preferido", "principal"
    );

    public PersonDTO() {}

    public PersonDTO(Person person) {
        super(person);

        this.names            = parseList(person.getNames());
        this.address          = parseList(person.getAddress());
        this.personAttributes = parseList(person.getPersonAttributes());

        this.sex       = person.getSex();
        this.birthdate = person.getBirthdate();

        // ======= NAMES (preferido) =======
        Map<String, Object> pref = pickPreferred(this.names);
        if (pref != null) {
            this.firstName = firstNonBlank(
                    str(pref, "firstName"), str(pref, "givenName"), str(pref, "first")
            );
            this.lastName = firstNonBlank(
                    str(pref, "lastName"), str(pref, "familyName"), str(pref, "last")
            );
            this.fullName = joinWithSpace(this.firstName, this.lastName);
        } else if (names != null && !names.isEmpty()) {
            Map<String, Object> first = names.get(0);
            this.firstName = firstNonBlank(
                    str(first, "firstName"), str(first, "givenName"), str(first, "first")
            );
            this.lastName = firstNonBlank(
                    str(first, "lastName"), str(first, "familyName"), str(first, "last")
            );
            this.fullName = joinWithSpace(this.firstName, this.lastName);
        }

        // ======= ADDRESS (primeiro item) =======
        if (address != null && !address.isEmpty()) {
            Map<String, Object> addr = address.get(0);
            this.addressLine1 = str(addr, "addressLine1");
            this.city         = str(addr, "city");
            this.district     = str(addr, "district");
            this.province     = str(addr, "province");
            this.fullAddress  = joinWithComma(addressLine1, city, district, province);
        }
    }

    @Override
    public Person toEntity() {
        Person person = new Person();

        person.setId(this.getId());
        person.setUuid(this.getUuid());
        person.setSex(this.sex);
        person.setBirthdate(this.birthdate);

        // Escreve as listas de volta como JSON String
        person.setNames(writeJson(this.names));
        person.setAddress(writeJson(this.address));
        person.setPersonAttributes(writeJson(this.personAttributes));

        person.setCreatedAt(this.getCreatedAt());
        person.setCreatedBy(this.getCreatedBy());
        person.setUpdatedAt(this.getUpdatedAt());
        person.setUpdatedBy(this.getUpdatedBy());

        if (this.getLifeCycleStatus() != null && !this.getLifeCycleStatus().isBlank()) {
            try {
                person.setLifeCycleStatus(LifeCycleStatus.valueOf(this.getLifeCycleStatus()));
            } catch (IllegalArgumentException ex) {
                // Ignora valor inválido para não quebrar a conversão
            }
        }

        return person;
    }

    /* ===================== Helpers ===================== */

    private List<Map<String, Object>> parseList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return MAPPER.readValue(json, LIST_OF_MAPS);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String writeJson(Object obj) {
        try {
            return obj != null ? MAPPER.writeValueAsString(obj) : "[]";
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    /** Escolhe o primeiro item marcado como preferido; se nenhum, devolve null. */
    private Map<String, Object> pickPreferred(List<Map<String, Object>> list) {
        if (list == null) return null;
        for (Map<String, Object> m : list) {
            if (m == null) continue;
            for (String key : PREFERRED_FLAGS) {
                Object v = m.get(key);
                if (v instanceof Boolean && (Boolean) v) return m;
                if (v instanceof String && "true".equalsIgnoreCase((String) v)) return m;
                if (v instanceof Number && ((Number) v).intValue() == 1) return m;
            }
        }
        return null;
    }

    /** Primeira string não vazia. */
    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    /** Acessa mapa como string (null se vazio). */
    private String str(Map<String, Object> m, String key) {
        if (m == null) return null;
        Object v = m.get(key);
        if (v == null) return null;
        String s = String.valueOf(v);
        return s.isBlank() ? null : s;
    }

    /** Junta com espaço, ignorando nulos/vazios. */
    private String joinWithSpace(String... parts) {
        if (parts == null) return null;
        List<String> valid = new ArrayList<>();
        for (String p : parts) if (p != null && !p.isBlank()) valid.add(p.trim());
        return String.join(" ", valid).trim();
    }

    /** Junta com vírgula, ignorando nulos/vazios. */
    private String joinWithComma(String... parts) {
        if (parts == null) return null;
        List<String> valid = new ArrayList<>();
        for (String p : parts) if (p != null && !p.isBlank()) valid.add(p.trim());
        return String.join(", ", valid).trim();
    }
}
