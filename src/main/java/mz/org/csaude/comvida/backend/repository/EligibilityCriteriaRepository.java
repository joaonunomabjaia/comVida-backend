package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityCriteriaRepository extends CrudRepository<EligibilityCriteria, Long> {

    @Override
    List<EligibilityCriteria> findAll();

    @Override
    Optional<EligibilityCriteria> findById(@NotNull Long id);

    List<EligibilityCriteria> findByCriteria(String criteria);

    Optional<EligibilityCriteria> findByUuid(String uuid);
}
