package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.CohortMemberSource;
import mz.org.csaude.comvida.backend.repository.SourceTypeRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class SourceTypeService extends BaseService {

    private final SourceTypeRepository sourceTypeRepository;

    public SourceTypeService(SourceTypeRepository sourceTypeRepository) {
        this.sourceTypeRepository = sourceTypeRepository;
    }

    public List<CohortMemberSource> findAll() {
        return sourceTypeRepository.findAll();
    }

    public Optional<CohortMemberSource> findById(Long id) {
        return sourceTypeRepository.findById(id);
    }

    public Optional<CohortMemberSource> findByUuid(String uuid) {
        return sourceTypeRepository.findByUuid(uuid);
    }

    @Transactional
    public CohortMemberSource create(CohortMemberSource cohortMemberSource) {
        cohortMemberSource.setCreatedAt(DateUtils.getCurrentDate());
        cohortMemberSource.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return sourceTypeRepository.save(cohortMemberSource);
    }

    @Transactional
    public CohortMemberSource update(CohortMemberSource cohortMemberSource) {
        Optional<CohortMemberSource> existing = sourceTypeRepository.findByUuid(cohortMemberSource.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("SourceType not found");
        }

        CohortMemberSource toUpdate = existing.get();
        toUpdate.setName(cohortMemberSource.getName());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohortMemberSource.getUpdatedBy());

        return sourceTypeRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<CohortMemberSource> existing = sourceTypeRepository.findByUuid(uuid);
        existing.ifPresent(sourceTypeRepository::delete);
    }
}
