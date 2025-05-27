package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.HomeVisit;
import mz.org.csaude.comvida.backend.repository.HomeVisitRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class HomeVisitService extends BaseService {

    private final HomeVisitRepository homeVisitRepository;

    public HomeVisitService(HomeVisitRepository homeVisitRepository) {
        this.homeVisitRepository = homeVisitRepository;
    }

    public List<HomeVisit> findAll() {
        return homeVisitRepository.findAll();
    }

    public Optional<HomeVisit> findById(Long id) {
        return homeVisitRepository.findById(id);
    }

    public Optional<HomeVisit> findByUuid(String uuid) {
        return homeVisitRepository.findByUuid(uuid);
    }

    @Transactional
    public HomeVisit create(HomeVisit homeVisit) {
        homeVisit.setCreatedAt(DateUtils.getCurrentDate());
        homeVisit.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return homeVisitRepository.save(homeVisit);
    }

    @Transactional
    public HomeVisit update(HomeVisit homeVisit) {
        Optional<HomeVisit> existing = homeVisitRepository.findByUuid(homeVisit.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("HomeVisit not found");
        }

        HomeVisit toUpdate = existing.get();
        toUpdate.setAllocation(homeVisit.getAllocation());
        toUpdate.setCohortMember(homeVisit.getCohortMember());
        toUpdate.setVisitNumber(homeVisit.getVisitNumber());
        toUpdate.setVisitDate(homeVisit.getVisitDate());
        toUpdate.setResult(homeVisit.getResult());
        toUpdate.setNotes(homeVisit.getNotes());
        toUpdate.setForm(homeVisit.getForm());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(homeVisit.getUpdatedBy());

        return homeVisitRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<HomeVisit> existing = homeVisitRepository.findByUuid(uuid);
        existing.ifPresent(homeVisitRepository::delete);
    }
}
