package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Cohort;
import java.util.List;
import java.util.Optional;

@Repository
public interface CohortRepository extends CrudRepository<Cohort, Long> {

    @Override
    List<Cohort> findAll();

    @Override
    Optional<Cohort> findById(@NotNull Long id);

    Optional<Cohort> findByName(String name);

    Optional<Cohort> findByUuid(String uuid);
}
