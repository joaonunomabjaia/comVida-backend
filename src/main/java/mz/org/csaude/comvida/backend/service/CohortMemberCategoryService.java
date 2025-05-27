package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.CohortMemberCategory;
import mz.org.csaude.comvida.backend.repository.CohortMemberCategoryRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class CohortMemberCategoryService extends BaseService {

    private final CohortMemberCategoryRepository cohortMemberCategoryRepository;

    public CohortMemberCategoryService(CohortMemberCategoryRepository cohortMemberCategoryRepository) {
        this.cohortMemberCategoryRepository = cohortMemberCategoryRepository;
    }

    public List<CohortMemberCategory> findAll() {
        return cohortMemberCategoryRepository.findAll();
    }

    public Optional<CohortMemberCategory> findById(Long id) {
        return cohortMemberCategoryRepository.findById(id);
    }

    public Optional<CohortMemberCategory> findByUuid(String uuid) {
        return cohortMemberCategoryRepository.findByUuid(uuid);
    }

    @Transactional
    public CohortMemberCategory create(CohortMemberCategory cohortMemberCategory) {
        cohortMemberCategory.setCreatedAt(DateUtils.getCurrentDate());
        cohortMemberCategory.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return cohortMemberCategoryRepository.save(cohortMemberCategory);
    }

    @Transactional
    public CohortMemberCategory update(CohortMemberCategory cohortMemberCategory) {
        Optional<CohortMemberCategory> existing = cohortMemberCategoryRepository.findByUuid(cohortMemberCategory.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("CohortMemberCategory not found");
        }

        CohortMemberCategory toUpdate = existing.get();
        toUpdate.setCohortMember(cohortMemberCategory.getCohortMember());
        toUpdate.setEligibilityCriteria(cohortMemberCategory.getEligibilityCriteria());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohortMemberCategory.getUpdatedBy());

        return cohortMemberCategoryRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<CohortMemberCategory> existing = cohortMemberCategoryRepository.findByUuid(uuid);
        existing.ifPresent(cohortMemberCategoryRepository::delete);
    }
}
