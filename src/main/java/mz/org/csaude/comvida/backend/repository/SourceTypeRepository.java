package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.SourceType;
import java.util.List;
import java.util.Optional;

@Repository
public interface SourceTypeRepository extends CrudRepository<SourceType, Long> {

    @Override
    List<SourceType> findAll();

    @Override
    Optional<SourceType> findById(@NotNull Long id);

    Optional<SourceType> findByName(String name);

    Optional<SourceType> findByUuid(String uuid);
}
