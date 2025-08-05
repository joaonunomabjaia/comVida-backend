package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@Serdeable
public class Person extends BaseEntity {

    // Alterado para armazenar JSON como string
    @Column(columnDefinition = "json")
    private String names;

    @Column(length = 10)
    private String sex;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    @Column(columnDefinition = "json")
    private String address;

    @Column(name = "person_attributes", columnDefinition = "json")
    private String personAttributes;

    @Transient
    public List<Map<String, Object>> getNamesAsList() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(this.names, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Transient
    public void setNamesAsList(List<Map<String, Object>> namesList) {
        try {
            this.names = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(namesList);
        } catch (Exception e) {
            this.names = "[]";
        }
    }

}
