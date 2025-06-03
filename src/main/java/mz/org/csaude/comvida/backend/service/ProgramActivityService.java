package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.repository.ProgramActivityRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class ProgramActivityService {

    private final ProgramActivityRepository repository;

    public ProgramActivityService(ProgramActivityRepository repository) {
        this.repository = repository;
    }

    public Page<ProgramActivity> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ProgramActivity> searchByName(String name, Pageable pageable) {
        return repository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<ProgramActivity> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<ProgramActivity> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    @Transactional
    public ProgramActivity create(ProgramActivity activity) {
        activity.setCreatedAt(DateUtils.getCurrentDate());
        activity.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return repository.save(activity);
    }

    @Transactional
    public ProgramActivity update(ProgramActivity activity) {
        Optional<ProgramActivity> existing = repository.findByUuid(activity.getUuid());
        if (existing.isEmpty()) throw new RuntimeException("ProgramActivity not found");

        ProgramActivity toUpdate = existing.get();
        toUpdate.setName(activity.getName());
        toUpdate.setProgram(activity.getProgram());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(activity.getUpdatedBy());

        return repository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        repository.findByUuid(uuid).ifPresent(repository::delete);
    }
}
