package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import mz.org.csaude.comvida.backend.repository.EligibilityCriteriaRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class EligibilityCriteriaService extends BaseService {

    private final EligibilityCriteriaRepository eligibilityCriteriaRepository;

    public EligibilityCriteriaService(EligibilityCriteriaRepository eligibilityCriteriaRepository) {
        this.eligibilityCriteriaRepository = eligibilityCriteriaRepository;
    }

    public List<EligibilityCriteria> findAll() {
        return eligibilityCriteriaRepository.findAll();
    }

    public Optional<EligibilityCriteria> findById(Long id) {
        return eligibilityCriteriaRepository.findById(id);
    }

    public Optional<EligibilityCriteria> findByUuid(String uuid) {
        return eligibilityCriteriaRepository.findByUuid(uuid);
    }

    @Transactional
    public EligibilityCriteria create(EligibilityCriteria eligibilityCriteria) {
        eligibilityCriteria.setCreatedAt(DateUtils.getCurrentDate());
        eligibilityCriteria.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return eligibilityCriteriaRepository.save(eligibilityCriteria);
    }

    @Transactional
    public EligibilityCriteria update(EligibilityCriteria eligibilityCriteria) {
        Optional<EligibilityCriteria> existing = eligibilityCriteriaRepository.findByUuid(eligibilityCriteria.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("EligibilityCriteria not found");
        }

        EligibilityCriteria toUpdate = existing.get();
        toUpdate.setCriteria(eligibilityCriteria.getCriteria());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(eligibilityCriteria.getUpdatedBy());

        return eligibilityCriteriaRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<EligibilityCriteria> existing = eligibilityCriteriaRepository.findByUuid(uuid);
        existing.ifPresent(eligibilityCriteriaRepository::delete);
    }
}
