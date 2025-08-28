package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Person;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    @Override
    List<Person> findAll();

    @Override
    Optional<Person> findById(@NotNull Long id);

    List<Person> findBySex(String sex);

    List<Person> findByBirthdate(Date birthdate);

    Optional<Person> findByUuid(String uuid);

    boolean existsByCreatedBy(String createdBy);
    long countByCreatedBy(String createdBy);
}
