package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Cohort;
import java.util.List;
import java.util.Optional;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {

    Optional<Cohort> findByUuid(String uuid);

    Page<Cohort> findByNameIlike(String name, Pageable pageable);
    
    Page<Cohort> findByProgramActivityId(Long programActivityId, Pageable pageable);

}
