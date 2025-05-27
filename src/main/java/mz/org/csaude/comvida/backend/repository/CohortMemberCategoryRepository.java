package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.CohortMemberCategory;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import java.util.List;
import java.util.Optional;

@Repository
public interface CohortMemberCategoryRepository extends CrudRepository<CohortMemberCategory, Long> {

    @Override
    List<CohortMemberCategory> findAll();

    @Override
    Optional<CohortMemberCategory> findById(@NotNull Long id);

    List<CohortMemberCategory> findByCohortMember(CohortMember cohortMember);

    List<CohortMemberCategory> findByEligibilityCriteria(EligibilityCriteria eligibilityCriteria);

    Optional<CohortMemberCategory> findByUuid(String uuid);
}
