package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.SourceSystem;

import java.util.Optional;

@Repository
public interface SourceSystemRepository extends CrudRepository<SourceSystem, Long> {

    Page<SourceSystem> findAll(Pageable pageable);

    Optional<SourceSystem> findById(@NotNull Long id);

    Optional<SourceSystem> findByUuid(String uuid);

    Optional<SourceSystem> findByCode(String code);

    Page<SourceSystem> findByCodeIlike(String name, Pageable pageable);
}
