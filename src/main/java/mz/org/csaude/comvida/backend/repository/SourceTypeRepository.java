package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.CohortMemberSource;
import java.util.List;
import java.util.Optional;

@Repository
public interface SourceTypeRepository extends CrudRepository<CohortMemberSource, Long> {

    @Override
    List<CohortMemberSource> findAll();

    @Override
    Optional<CohortMemberSource> findById(@NotNull Long id);

    Optional<CohortMemberSource> findByName(String name);

    Optional<CohortMemberSource> findByUuid(String uuid);
}
