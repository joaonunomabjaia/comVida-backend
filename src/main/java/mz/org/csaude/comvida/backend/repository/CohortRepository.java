package mz.org.csaude.comvida.backend.repository;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Cohort;
import java.util.List;
import java.util.Optional;

@Repository
public interface CohortRepository extends CrudRepository<Cohort, Long> {

    Page<Cohort> findAll(Pageable pageable);

    @Override
    Optional<Cohort> findById(@NotNull Long id);

    Optional<Cohort> findByName(String name);

    Optional<Cohort> findByUuid(String uuid);

    Page<Cohort> findByNameIlike(String name, Pageable pageable);
    
    Page<Cohort> findByProgramActivityId(Long programActivityId, Pageable pageable);

    Optional<Cohort> findByDescription(String description);

    @Query(value = """
    SELECT DISTINCT c.* FROM cohorts c
    INNER JOIN cohort_members cm ON cm.cohort_id = c.id
    WHERE (:serviceId IS NULL OR c.program_activity_id = :serviceId)
    """,
            countQuery = """
    SELECT COUNT(DISTINCT c.id) FROM cohorts c
    INNER JOIN cohort_members cm ON cm.cohort_id = c.id
    WHERE (:serviceId IS NULL OR c.program_activity_id = :serviceId)
    """,
            nativeQuery = true)
    Page<Cohort> findWithMembersByServiceId(@Nullable Long serviceId, Pageable pageable);


    @Query(value = """
    SELECT DISTINCT c.* FROM cohorts c
    INNER JOIN cohort_members cm ON cm.cohort_id = c.id
    """,
            countQuery = """
    SELECT COUNT(DISTINCT c.id) FROM cohorts c
    INNER JOIN cohort_members cm ON cm.cohort_id = c.id
    """,
            nativeQuery = true)
    Page<Cohort> findAllWithMembers(Pageable pageable);
}
