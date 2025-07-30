package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import java.util.List;
import java.util.Optional;

@Repository
public interface EligibilityCriteriaRepository extends CrudRepository<EligibilityCriteria, Long> {

    Page<EligibilityCriteria> findAll(Pageable pageable);

    @Override
    Optional<EligibilityCriteria> findById(@NotNull Long id);

    List<EligibilityCriteria> findByCriteria(String criteria);


    Optional<EligibilityCriteria> findByUuid(String uuid);

    Page<EligibilityCriteria> findByCriteriaIlike(String criteria, Pageable pageable);
}
