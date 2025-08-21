package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;
import org.hibernate.annotations.ColumnTransformer;

import java.util.*;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@Serdeable
public class Person extends BaseEntity {

    /* ===================== Infra JSON ===================== */

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP =
            new TypeReference<List<Map<String, Object>>>() {};
    private static final List<String> PREFERRED_FLAGS = Arrays.asList(
            "prefered", "preferred", "isPreferred", "primary", "default", "preferido", "principal"
    );

    /* ===================== Campos persistidos ===================== */

    // JSONB <-> String (cast no write para jsonb)
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String names;              // Ex.: [{"firstName":"Admin","lastName":"User","prefered":true,"fullName":"Admin User"}]

    @Column(length = 10)
    private String sex;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String address;            // ARRAY: [ { addressLine1, city, district, province, fullAddress, prefered }, ... ]

    @Column(name = "person_attributes", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String personAttributes;   // Geralmente ARRAY: []

    /* ===================== Acessores estruturados (List< Map >) ===================== */

    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getNamesAsList() {
        return readList(names);
    }

    @Transient
    @JsonIgnore
    public void setNamesAsList(List<Map<String, Object>> namesList) {
        this.names = writeList(namesList);
    }

    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getAddressAsList() {
        return readList(address);
    }

    @Transient
    @JsonIgnore
    public void setAddressAsList(List<Map<String, Object>> addressList) {
        this.address = writeList(addressList);
    }

    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getPersonAttributesAsList() {
        return readList(personAttributes);
    }

    @Transient
    @JsonIgnore
    public void setPersonAttributesAsList(List<Map<String, Object>> attributesList) {
        this.personAttributes = writeList(attributesList);
    }

    /* ===================== FULL NAME (fonte: API/JSON) ===================== */

    /** Retorna o registro de NOME preferido. Se não houver, retorna o primeiro; se vazio, null. */
    @Transient
    @JsonIgnore
    public Map<String, Object> getPreferredName() {
        List<Map<String, Object>> list = getNamesAsList();
        if (list.isEmpty()) return null;
        for (Map<String, Object> m : list) {
            if (m == null) continue;
            for (String key : PREFERRED_FLAGS) {
                Object v = m.get(key);
                if ((v instanceof Boolean && (Boolean) v)
                        || (v instanceof String && "true".equalsIgnoreCase((String) v))
                        || (v instanceof Number && ((Number) v).intValue() == 1)) {
                    return m;
                }
            }
        }
        return list.get(0);
    }

    @Transient
    @JsonIgnore
    public String getFirstName() {
        Map<String, Object> pref = getPreferredName();
        if (pref == null) return null;
        String given = firstNonBlank(
                asString(pref.get("firstName")),
                asString(pref.get("givenName")),
                asString(pref.get("first"))
        );
        return blankToNull(given);
    }

    @Transient
    @JsonIgnore
    public String getLastName() {
        Map<String, Object> pref = getPreferredName();
        if (pref == null) return null;
        String family = firstNonBlank(
                asString(pref.get("lastName")),
                asString(pref.get("familyName")),
                asString(pref.get("last"))
        );
        return blankToNull(family);
    }

    /**
     * Fonte da verdade: campo "fullName" (ou "display") dentro do nome preferido.
     * Fallback: concatena firstName + lastName do registro preferido.
     */
    @Transient
    @JsonIgnore
    public String getFullName() {
        Map<String, Object> pref = getPreferredName();
        if (pref == null) return null;

        String explicit = firstNonBlank(
                asString(pref.get("fullName")),
                asString(pref.get("display"))
        );
        if (explicit != null) return explicit;

        String built = joinSpace(getFirstName(), getLastName());
        return blankToNull(built);
    }

    /**
     * Grava "fullName" no objeto preferido da lista `names`.
     * Cria a lista/registro preferido se necessário.
     */
    @Transient
    @JsonIgnore
    public void setFullName(String fullName) {
        String v = blankToNull(fullName);
        List<Map<String, Object>> list = getNamesAsList();
        Map<String, Object> pref = getPreferredName();

        if (pref == null) {
            pref = new LinkedHashMap<>();
            pref.put("prefered", true);
            list.add(pref);
        }
        if (v == null) {
            pref.remove("fullName");
        } else {
            pref.put("fullName", v);
        }
        setNamesAsList(list);
    }

    /* ===================== FULL ADDRESS (API/JSON) ===================== */

    @Transient
    @JsonIgnore
    public Map<String, Object> getPreferredAddress() {
        List<Map<String, Object>> list = getAddressAsList();
        if (list.isEmpty()) return null;
        for (Map<String, Object> m : list) {
            if (m == null) continue;
            Object v = m.get("prefered");
            if ((v instanceof Boolean && (Boolean) v)
                    || (v instanceof String && "true".equalsIgnoreCase((String) v))
                    || (v instanceof Number && ((Number) v).intValue() == 1)) {
                return m;
            }
        }
        return list.get(0);
    }

    /**
     * Usa "fullAddress" do endereço preferido se existir; fallback: addressLine1, city, district, province.
     */
    @Transient
    @JsonIgnore
    public String getFullAddress() {
        Map<String, Object> pref = getPreferredAddress();
        if (pref == null) return null;

        String explicit = asString(pref.get("fullAddress"));
        if (explicit != null && !explicit.isBlank()) return explicit;

        String addressLine1 = asString(pref.get("addressLine1"));
        String city         = asString(pref.get("city"));
        String district     = asString(pref.get("district"));
        String province     = asString(pref.get("province"));
        String built = joinComma(addressLine1, city, district, province);
        return blankToNull(built);
    }

    /** Grava "fullAddress" no endereço preferido. */
    @Transient
    @JsonIgnore
    public void setFullAddress(String fullAddress) {
        String v = blankToNull(fullAddress);
        List<Map<String, Object>> list = getAddressAsList();
        Map<String, Object> pref = getPreferredAddress();

        if (pref == null) {
            pref = new LinkedHashMap<>();
            pref.put("prefered", true);
            list.add(pref);
        }
        if (v == null) {
            pref.remove("fullAddress");
        } else {
            pref.put("fullAddress", v);
        }
        setAddressAsList(list);
    }

    /* ===================== Utilitários ===================== */

    private List<Map<String, Object>> readList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, LIST_MAP);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String writeList(List<Map<String, Object>> list) {
        try {
            return objectMapper.writeValueAsString(
                    (list != null) ? list : new ArrayList<>()
            );
        } catch (Exception e) {
            return "[]";
        }
    }

    private static String asString(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String joinSpace(String... parts) {
        if (parts == null) return null;
        List<String> valid = new ArrayList<>();
        for (String p : parts) if (p != null && !p.isBlank()) valid.add(p.trim());
        return String.join(" ", valid);
    }

    private static String joinComma(String... parts) {
        if (parts == null) return null;
        List<String> valid = new ArrayList<>();
        for (String p : parts) if (p != null && !p.isBlank()) valid.add(p.trim());
        return String.join(", ", valid);
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
