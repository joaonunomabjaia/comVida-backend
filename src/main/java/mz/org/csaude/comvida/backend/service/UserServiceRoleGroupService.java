package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.entity.UserServiceRoleGroup;
import mz.org.csaude.comvida.backend.repository.GroupRepository;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleGroupRepository;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class UserServiceRoleGroupService extends BaseService {

    private final UserServiceRoleGroupRepository repository;
    private final UserServiceRoleRepository userServiceRoleRepository;
    private final GroupRepository groupRepository;

    public UserServiceRoleGroupService(UserServiceRoleGroupRepository repository,
                                       UserServiceRoleRepository userServiceRoleRepository,
                                       GroupRepository groupRepository) {
        this.repository = repository;
        this.userServiceRoleRepository = userServiceRoleRepository;
        this.groupRepository = groupRepository;
    }

    public Page<UserServiceRoleGroup> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<UserServiceRoleGroup> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    public Page<UserServiceRoleGroup> findByUserServiceRole(String userServiceRoleUuid, Pageable pageable) {
        UserServiceRole usr = userServiceRoleRepository.findByUuid(userServiceRoleUuid)
                .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + userServiceRoleUuid));
        return repository.findByUserServiceRole(usr, pageable);
    }

    @Transactional
    public UserServiceRoleGroup create(UserServiceRoleGroup entity) {
        // Attach refs pelos UUIDs
        if (entity.getUserServiceRole() == null || entity.getUserServiceRole().getUuid() == null) {
            throw new RuntimeException("userServiceRole UUID is required");
        }
        if (entity.getGroup() == null || entity.getGroup().getUuid() == null) {
            throw new RuntimeException("group UUID is required");
        }

        UserServiceRole usr = userServiceRoleRepository.findByUuid(entity.getUserServiceRole().getUuid())
                .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + entity.getUserServiceRole().getUuid()));
        Group grp = groupRepository.findByUuid(entity.getGroup().getUuid())
                .orElseThrow(() -> new RuntimeException("Group not found with UUID: " + entity.getGroup().getUuid()));

        if (repository.existsByUserServiceRoleAndGroup(usr, grp)) {
            throw new RuntimeException("Par (userServiceRole, group) já existe.");
        }

        entity.setUserServiceRole(usr);
        entity.setGroup(grp);
        entity.setCreatedAt(DateUtils.getCurrentDate());
        entity.setLifeCycleStatus(LifeCycleStatus.ACTIVE);

        return repository.save(entity);
    }

    @Transactional
    public UserServiceRoleGroup update(UserServiceRoleGroup entity) {
        Optional<UserServiceRoleGroup> existingOpt = repository.findByUuid(entity.getUuid());
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("UserServiceRoleGroup not found with UUID: " + entity.getUuid());
        }

        UserServiceRoleGroup toUpdate = existingOpt.get();

        // Atualiza refs se vierem com UUID
        if (entity.getUserServiceRole() != null && entity.getUserServiceRole().getUuid() != null) {
            UserServiceRole usr = userServiceRoleRepository.findByUuid(entity.getUserServiceRole().getUuid())
                    .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + entity.getUserServiceRole().getUuid()));
            toUpdate.setUserServiceRole(usr);
        }
        if (entity.getGroup() != null && entity.getGroup().getUuid() != null) {
            Group grp = groupRepository.findByUuid(entity.getGroup().getUuid())
                    .orElseThrow(() -> new RuntimeException("Group not found with UUID: " + entity.getGroup().getUuid()));
            toUpdate.setGroup(grp);
        }

        // Evita duplicado do par após mudanças
        if (repository.existsByUserServiceRoleAndGroupAndIdNot(toUpdate.getUserServiceRole(), toUpdate.getGroup(), toUpdate.getId())) {
            throw new RuntimeException("Par (userServiceRole, group) já existe.");
        }

        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(entity.getUpdatedBy());

        // Se quiser permitir alterar o lifeCycleStatus pelo payload:
        if (entity.getLifeCycleStatus() != null) {
            toUpdate.setLifeCycleStatus(entity.getLifeCycleStatus());
        }

        return repository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        UserServiceRoleGroup existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("UserServiceRoleGroup not found with UUID: " + uuid));

        // Sem dependências conhecidas — deletar diretamente
        repository.delete(existing);
    }

    @Transactional
    public UserServiceRoleGroup updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        UserServiceRoleGroup existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("UserServiceRoleGroup not found with UUID: " + uuid));

        existing.setLifeCycleStatus(newStatus);
        existing.setUpdatedAt(DateUtils.getCurrentDate());
        return repository.update(existing);
    }

    public Optional<UserServiceRoleGroup> findById(Long id) {
        return  repository.findById(id);
    }
}
