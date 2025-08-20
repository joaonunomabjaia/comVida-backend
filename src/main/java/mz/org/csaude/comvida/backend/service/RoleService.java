package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.error.RecordInUseException;
import mz.org.csaude.comvida.backend.repository.RoleRepository;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class RoleService extends BaseService {

    private final RoleRepository roleRepository;
    private final UserServiceRoleRepository userServiceRoleRepository;

    public RoleService(RoleRepository roleRepository,
                       UserServiceRoleRepository userServiceRoleRepository) {
        this.roleRepository = roleRepository;
        this.userServiceRoleRepository = userServiceRoleRepository;
    }

    public Page<Role> findAll(@Nullable Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Page<Role> searchByName(String name, Pageable pageable) {
        return roleRepository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> findByUuid(String uuid) {
        return roleRepository.findByUuid(uuid);
    }

    @Transactional
    public Role create(Role role) {
        role.setCreatedAt(DateUtils.getCurrentDate());
        role.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Role role) {
        Optional<Role> existingOpt = roleRepository.findByUuid(role.getUuid());
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Role não encontrada");
        }

        Role toUpdate = existingOpt.get();
        toUpdate.setName(role.getName());
        toUpdate.setDescription(role.getDescription());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(role.getUpdatedBy());

        return roleRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Role> existingOpt = roleRepository.findByUuid(uuid);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Role não encontrada com UUID: " + uuid);
        }

        Role role = existingOpt.get();

        long inUse = userServiceRoleRepository.countByRole(role);
        if (inUse > 0) {
            throw new RecordInUseException("A função não pode ser eliminada porque está associada a um ou mais vínculos de utilizador.");
        }

        roleRepository.delete(role);
    }

    @Transactional
    public Role updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        Optional<Role> existingOpt = roleRepository.findByUuid(uuid);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Role não encontrada com UUID: " + uuid);
        }

        Role role = existingOpt.get();
        role.setLifeCycleStatus(newStatus);
        role.setUpdatedAt(DateUtils.getCurrentDate());

        return roleRepository.update(role);
    }
}
