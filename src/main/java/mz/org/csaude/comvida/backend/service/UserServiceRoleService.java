package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.dto.UserServiceRoleDTO;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class UserServiceRoleService extends BaseService {

    private final UserServiceRoleRepository userServiceRoleRepository;

    public UserServiceRoleService(UserServiceRoleRepository userServiceRoleRepository) {
        this.userServiceRoleRepository = userServiceRoleRepository;
    }

    public List<UserServiceRole> findAll(@Nullable Pageable pageable) {
        return userServiceRoleRepository.findAll();
    }

    public Optional<UserServiceRole> findById(Long id) {
        return userServiceRoleRepository.findById(id);
    }

    public Optional<UserServiceRole> findByUuid(String uuid) {
        return userServiceRoleRepository.findByUuid(uuid);
    }

    @Transactional
    public UserServiceRole create(UserServiceRole userServiceRole) {
        userServiceRole.setCreatedAt(DateUtils.getCurrentDate());
        userServiceRole.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return userServiceRoleRepository.save(userServiceRole);
    }

    @Transactional
    public UserServiceRole update(UserServiceRole userServiceRole) {
        Optional<UserServiceRole> existing = userServiceRoleRepository.findByUuid(userServiceRole.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("UserServiceRole not found");
        }

        UserServiceRole toUpdate = existing.get();
        toUpdate.setUser(userServiceRole.getUser());
        toUpdate.setService(userServiceRole.getService());
        toUpdate.setRole(userServiceRole.getRole());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(userServiceRole.getUpdatedBy());

        return userServiceRoleRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<UserServiceRole> existing = userServiceRoleRepository.findByUuid(uuid);
        existing.ifPresent(userServiceRoleRepository::delete);
    }

//    public UserServiceRoleDTO saveOrUpdate(Long userInfo, @NonNull UserServiceRoleDTO dto) {
//
//    }
}
