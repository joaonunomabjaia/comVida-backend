package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.repository.CohortRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class CohortService extends BaseService {

    private final CohortRepository cohortRepository;

    public CohortService(CohortRepository cohortRepository) {
        this.cohortRepository = cohortRepository;
    }

    public List<Cohort> findAll() {
        return cohortRepository.findAll();
    }

    public Optional<Cohort> findById(Long id) {
        return cohortRepository.findById(id);
    }

    public Optional<Cohort> findByUuid(String uuid) {
        return cohortRepository.findByUuid(uuid);
    }

    @Transactional
    public Cohort create(Cohort cohort) {
        cohort.setCreatedAt(DateUtils.getCurrentDate());
        cohort.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return cohortRepository.save(cohort);
    }

    @Transactional
    public Cohort update(Cohort cohort) {
        Optional<Cohort> existing = cohortRepository.findByUuid(cohort.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Cohort not found");
        }

        Cohort toUpdate = existing.get();
        toUpdate.setName(cohort.getName());
        toUpdate.setDescription(cohort.getDescription());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohort.getUpdatedBy());

        return cohortRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Cohort> existing = cohortRepository.findByUuid(uuid);
        existing.ifPresent(cohortRepository::delete);
    }
}
