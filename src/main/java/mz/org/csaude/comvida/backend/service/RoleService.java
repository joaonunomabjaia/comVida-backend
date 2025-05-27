package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.repository.RoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class RoleService extends BaseService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


//    public Optional<Role> findByUuid(String uuid) {
//        return roleRepository.findByUuid(uuid);
//    }

//    @Transactional
//    public Role create(Role role) {
//        role.setCreatedAt(DateUtils.getCurrentDate());
//        role.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
//        return roleRepository.save(role);
//    }

//    @Transactional
//    public Role update(Role role) {
//        Optional<Role> existing = roleRepository.findByUuid(role.getUuid());
//        if (existing.isEmpty()) {
//            throw new RuntimeException("Role not found");
//        }
//
//        Role toUpdate = existing.get();
//        toUpdate.setName(role.getName());
//        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
//        toUpdate.setUpdatedBy(role.getUpdatedBy());
//
//        return roleRepository.update(toUpdate);
//    }
//
//    @Transactional
//    public void delete(String uuid) {
//        Optional<Role> existing = roleRepository.findByUuid(uuid);
//        existing.ifPresent(roleRepository::delete);
//    }
}
