package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.CohortMemberSource;
import mz.org.csaude.comvida.backend.entity.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface CohortMemberSourceRepository extends CrudRepository<CohortMemberSource, Long> {

    @Override
    List<CohortMemberSource> findAll();

    @Override
    Optional<CohortMemberSource> findById(@NotNull Long id);

    Optional<CohortMemberSource> findByUuid(String uuid);

    Optional<CohortMemberSource> findByName(String uuid);
}

