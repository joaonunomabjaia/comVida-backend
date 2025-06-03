package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.repository.GroupRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class GroupService {

    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public Page<Group> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Group> searchByName(String name, Pageable pageable) {
        return repository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<Group> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Group> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    @Transactional
    public Group create(Group group) {
        group.setCreatedAt(DateUtils.getCurrentDate());
        group.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return repository.save(group);
    }

    @Transactional
    public Group update(Group group) {
        Optional<Group> existing = repository.findByUuid(group.getUuid());
        if (existing.isEmpty()) throw new RuntimeException("Group not found");

        Group toUpdate = existing.get();
        toUpdate.setName(group.getName());
        toUpdate.setDescription(group.getDescription());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(group.getUpdatedBy());

        return repository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        repository.findByUuid(uuid).ifPresent(repository::delete);
    }
}
