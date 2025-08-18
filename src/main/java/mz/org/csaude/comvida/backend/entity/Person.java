package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Introspected;
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

    // JSONB <-> String (cast no write para jsonb)
    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String names;              // Ex.: [{"firstName":"Admin","lastName":"User","prefered":true}]

    @Column(length = 10)
    private String sex;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String address;            // **ARRAY** de endereços: [ { ... }, ... ]

    @Column(name = "person_attributes", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String personAttributes;   // Pode ser [] ou [{...}]

    /* ======= NAMES (lista) ======= */
    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getNamesAsList() {
        if (this.names == null || this.names.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(this.names, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transient
    @JsonIgnore
    public void setNamesAsList(List<Map<String, Object>> namesList) {
        try {
            this.names = objectMapper.writeValueAsString(
                    namesList != null ? namesList : new ArrayList<>()
            );
        } catch (JsonProcessingException e) {
            this.names = "[]";
        }
    }

    /* ======= ADDRESS (sempre ARRAY) ======= */
    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getAddressAsList() {
        if (this.address == null || this.address.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(this.address, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transient
    @JsonIgnore
    public void setAddressAsList(List<Map<String, Object>> addressList) {
        try {
            this.address = objectMapper.writeValueAsString(
                    addressList != null ? addressList : new ArrayList<>()
            );
        } catch (Exception e) {
            this.address = "[]";
        }
    }

    /** Retorna o primeiro endereço, se existir (convenience). */
    @Transient
    @JsonIgnore
    public Map<String, Object> getFirstAddressAsMap() {
        List<Map<String, Object>> list = getAddressAsList();
        return list.isEmpty() ? new HashMap<>() : list.get(0);
    }

    /* ======= PERSON ATTRIBUTES (lista) ======= */
    @Transient
    @JsonIgnore
    public List<Map<String, Object>> getPersonAttributesAsList() {
        if (this.personAttributes == null || this.personAttributes.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(this.personAttributes, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transient
    @JsonIgnore
    public void setPersonAttributesAsList(List<Map<String, Object>> attributesList) {
        try {
            this.personAttributes = objectMapper.writeValueAsString(
                    attributesList != null ? attributesList : new ArrayList<>()
            );
        } catch (Exception e) {
            this.personAttributes = "[]";
        }
    }
}
