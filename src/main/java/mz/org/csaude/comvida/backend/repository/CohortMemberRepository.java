package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import java.util.List;
import java.util.Optional;

@Repository
public interface CohortMemberRepository extends CrudRepository<CohortMember, Long> {

    @Override
    List<CohortMember> findAll();

    @Override
    Optional<CohortMember> findById(@NotNull Long id);

    List<CohortMember> findByCohortId(Long cohortId);

    List<CohortMember> findByPatientId(Long patientId);

    //List<CohortMember> findBySourceTypeId(Long sourceTypeId);

    Optional<CohortMember> findByOriginId(String originId);

    Optional<CohortMember> findByUuid(String uuid);

    long countByCohort(Cohort cohort);
}
