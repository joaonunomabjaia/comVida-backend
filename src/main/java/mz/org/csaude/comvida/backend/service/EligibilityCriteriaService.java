package mz.org.csaude.comvida.backend.service;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.repository.EligibilityCriteriaRepository;

import java.util.Optional;

@Singleton
public class EligibilityCriteriaService extends BaseService {

    private final EligibilityCriteriaRepository eligibilityCriteriaRepository;

    public EligibilityCriteriaService(EligibilityCriteriaRepository eligibilityCriteriaRepository) {
        this.eligibilityCriteriaRepository = eligibilityCriteriaRepository;
    }


    public Page<EligibilityCriteria> findAll(Pageable pageable) {
        return eligibilityCriteriaRepository.findAll(pageable);
    }

    public Page<EligibilityCriteria> searchByCriteria(String criteria, Pageable pageable) {
        return eligibilityCriteriaRepository.findByCriteriaIlike("%" + criteria + "%", pageable);
    }

    public Optional<EligibilityCriteria> findById(Long id) {
        return eligibilityCriteriaRepository.findById(id);
    }

    public Optional<EligibilityCriteria> findByUuid(String uuid) {
        return eligibilityCriteriaRepository.findByUuid(uuid);
    }

    @Transactional
    public EligibilityCriteria save(EligibilityCriteria entity) {
        return eligibilityCriteriaRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        eligibilityCriteriaRepository.deleteById(id);
    }
}
