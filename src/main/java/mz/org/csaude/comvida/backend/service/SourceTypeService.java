package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.SourceType;
import mz.org.csaude.comvida.backend.repository.SourceTypeRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.Utilities;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Singleton
public class SourceTypeService extends BaseService {

    private final SourceTypeRepository sourceTypeRepository;

    public SourceTypeService(SourceTypeRepository sourceTypeRepository) {
        this.sourceTypeRepository = sourceTypeRepository;
    }

    public List<SourceType> findAll() {
        return sourceTypeRepository.findAll();
    }

    public Optional<SourceType> findById(Long id) {
        return sourceTypeRepository.findById(id);
    }

    public Optional<SourceType> findByUuid(String uuid) {
        return sourceTypeRepository.findByUuid(uuid);
    }

    @Transactional
    public SourceType create(SourceType sourceType) {
        sourceType.setCreatedAt(DateUtils.getCurrentDate());
        sourceType.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return sourceTypeRepository.save(sourceType);
    }

    @Transactional
    public SourceType update(SourceType sourceType) {
        Optional<SourceType> existing = sourceTypeRepository.findByUuid(sourceType.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("SourceType not found");
        }

        SourceType toUpdate = existing.get();
        toUpdate.setName(sourceType.getName());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(sourceType.getUpdatedBy());

        return sourceTypeRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<SourceType> existing = sourceTypeRepository.findByUuid(uuid);
        existing.ifPresent(sourceTypeRepository::delete);
    }
}
