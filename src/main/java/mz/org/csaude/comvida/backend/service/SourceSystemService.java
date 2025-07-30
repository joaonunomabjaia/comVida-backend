package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.SourceSystem;
import mz.org.csaude.comvida.backend.repository.SourceSystemRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class SourceSystemService extends BaseService {

    private final SourceSystemRepository sourceSystemRepository;

    public SourceSystemService(SourceSystemRepository sourceSystemRepository) {
        this.sourceSystemRepository = sourceSystemRepository;
    }

    public Page<SourceSystem> findAll(@Nullable Pageable pageable) {
        return sourceSystemRepository.findAll(pageable);
    }

    public Page<SourceSystem> findByCodeIlike(String code, Pageable pageable) {
        return sourceSystemRepository.findByCodeIlike("%" + code + "%", pageable);
    }

    public Optional<SourceSystem> findByCode(String code) {
        return sourceSystemRepository.findByCode(code);
    }

    public Optional<SourceSystem> findById(Long id) {
        return sourceSystemRepository.findById(id);
    }

    public Optional<SourceSystem> findByUuid(String uuid) {
        return sourceSystemRepository.findByUuid(uuid);
    }

    @Transactional
    public SourceSystem create(SourceSystem sourceSystem) {
        sourceSystem.setCreatedAt(DateUtils.getCurrentDate());
        sourceSystem.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return sourceSystemRepository.save(sourceSystem);
    }

    @Transactional
    public SourceSystem update(SourceSystem updated) {
        Optional<SourceSystem> existing = sourceSystemRepository.findByUuid(updated.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("SourceSystem not found");
        }

        SourceSystem current = existing.get();
        current.setCode(updated.getCode());
        current.setUpdatedBy(updated.getUpdatedBy());
        current.setUpdatedAt(DateUtils.getCurrentDate());

        return sourceSystemRepository.update(current);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<SourceSystem> existing = sourceSystemRepository.findByUuid(uuid);
        existing.ifPresent(sourceSystemRepository::delete);
    }

    @Transactional
    public SourceSystem updateLifeCycleStatus(String uuid, LifeCycleStatus status) {
        Optional<SourceSystem> existing = sourceSystemRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("SourceSystem not found");
        }

        SourceSystem system = existing.get();
        system.setLifeCycleStatus(status);
        system.setUpdatedAt(DateUtils.getCurrentDate());
        return sourceSystemRepository.update(system);
    }
}
