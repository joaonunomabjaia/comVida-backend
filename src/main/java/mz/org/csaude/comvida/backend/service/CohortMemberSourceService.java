package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.CohortMemberSource;
import mz.org.csaude.comvida.backend.repository.CohortMemberRepository;
import mz.org.csaude.comvida.backend.repository.CohortMemberSourceRepository;

import java.util.List;
import java.util.Optional;

@Singleton
public class CohortMemberSourceService extends BaseService {

    private final CohortMemberRepository cohortMemberRepository;
    @Inject
    CohortMemberSourceRepository cohortMemberSourceRepository;


    public CohortMemberSourceService(CohortMemberRepository cohortMemberRepository) {
        this.cohortMemberRepository = cohortMemberRepository;
    }

    public List<CohortMemberSource> findAll() {
        return cohortMemberSourceRepository.findAll();
    }

    public Optional<CohortMemberSource> findById(Long id) {
        return cohortMemberSourceRepository.findById(id);
    }

    public Optional<CohortMemberSource> findByUuid(String uuid) {
        return cohortMemberSourceRepository.findByUuid(uuid);
    }

    public Optional<CohortMemberSource> findByName(String uuid) {
        return cohortMemberSourceRepository.findByName(uuid);
    }
}

