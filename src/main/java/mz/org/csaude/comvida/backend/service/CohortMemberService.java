package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.repository.CohortMemberRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class CohortMemberService extends BaseService {

    private final CohortMemberRepository cohortMemberRepository;

    public CohortMemberService(CohortMemberRepository cohortMemberRepository) {
        this.cohortMemberRepository = cohortMemberRepository;
    }

    public List<CohortMember> findAll() {
        return cohortMemberRepository.findAll();
    }

    public Optional<CohortMember> findById(Long id) {
        return cohortMemberRepository.findById(id);
    }

    public Optional<CohortMember> findByUuid(String uuid) {
        return cohortMemberRepository.findByUuid(uuid);
    }

    @Transactional
    public CohortMember create(CohortMember cohortMember) {
        cohortMember.setCreatedAt(DateUtils.getCurrentDate());
        cohortMember.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return cohortMemberRepository.save(cohortMember);
    }

    @Transactional
    public CohortMember update(CohortMember cohortMember) {
        Optional<CohortMember> existing = cohortMemberRepository.findByUuid(cohortMember.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("CohortMember not found");
        }

        CohortMember toUpdate = existing.get();
        toUpdate.setCohort(cohortMember.getCohort());
        toUpdate.setPatient(cohortMember.getPatient());
        toUpdate.setSourceType(cohortMember.getSourceType());
        toUpdate.setOriginId(cohortMember.getOriginId());
        toUpdate.setInclusionDate(cohortMember.getInclusionDate());
        toUpdate.setExclusionDate(cohortMember.getExclusionDate());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohortMember.getUpdatedBy());

        return cohortMemberRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<CohortMember> existing = cohortMemberRepository.findByUuid(uuid);
        existing.ifPresent(cohortMemberRepository::delete);
    }
}
