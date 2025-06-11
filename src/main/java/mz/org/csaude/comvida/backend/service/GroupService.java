package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.error.RecordInUseException;
import mz.org.csaude.comvida.backend.repository.GroupRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class GroupService extends BaseService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Page<Group> findAll(@Nullable Pageable pageable) {
        return groupRepository.findAll(pageable);
    }

    public Page<Group> searchByName(String name, Pageable pageable) {
        return groupRepository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    public Optional<Group> findByUuid(String uuid) {
        return groupRepository.findByUuid(uuid);
    }

    @Transactional
    public Group create(Group group) {
        group.setCreatedAt(DateUtils.getCurrentDate());
        group.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return groupRepository.save(group);
    }

    @Transactional
    public Group update(Group group) {
        Optional<Group> existing = groupRepository.findByUuid(group.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Group not found with UUID: " + group.getUuid());
        }

        Group toUpdate = existing.get();
        toUpdate.setName(group.getName());
        toUpdate.setDescription(group.getDescription());
        toUpdate.setProgramActivity(group.getProgramActivity());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(group.getUpdatedBy());

        return groupRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Group> existing = groupRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("Group not found with UUID: " + uuid);
        }

        Group group = existing.get();

        // Se futuramente houver entidades que referenciam Group, colocar aqui a verificação:
        // long count = anotherRepository.countByGroup(group);
        // if (count > 0) {
        //     throw new RecordInUseException("O grupo não pode ser eliminado porque está associado a outros registos.");
        // }

        groupRepository.delete(group);
    }

    @Transactional
    public Group updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        Optional<Group> existing = groupRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("Group not found with UUID: " + uuid);
        }

        Group group = existing.get();
        group.setLifeCycleStatus(newStatus);
        group.setUpdatedAt(DateUtils.getCurrentDate());
        return groupRepository.update(group);
    }
}
