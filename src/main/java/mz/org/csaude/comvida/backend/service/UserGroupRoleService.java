package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;
import mz.org.csaude.comvida.backend.repository.UserGroupRoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class UserGroupRoleService extends BaseService {

    private final UserGroupRoleRepository userGroupRoleRepository;

    public UserGroupRoleService(UserGroupRoleRepository userGroupRoleRepository) {
        this.userGroupRoleRepository = userGroupRoleRepository;
    }

    public List<UserGroupRole> findAll(@Nullable Pageable pageable) {
        return userGroupRoleRepository.findAll();
    }

    public Optional<UserGroupRole> findById(Long id) {
        return userGroupRoleRepository.findById(id);
    }

    public Optional<UserGroupRole> findByUuid(String uuid) {
        return userGroupRoleRepository.findByUuid(uuid);
    }

    @Transactional
    public UserGroupRole create(UserGroupRole userServiceRole) {
        userServiceRole.setCreatedAt(DateUtils.getCurrentDate());
        userServiceRole.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return userGroupRoleRepository.save(userServiceRole);
    }

    @Transactional
    public UserGroupRole update(UserGroupRole userServiceRole) {
        Optional<UserGroupRole> existing = userGroupRoleRepository.findByUuid(userServiceRole.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("UserServiceRole not found");
        }

        UserGroupRole toUpdate = existing.get();
        toUpdate.setUser(userServiceRole.getUser());
        toUpdate.setGroup(userServiceRole.getGroup());
        toUpdate.setRole(userServiceRole.getRole());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(userServiceRole.getUpdatedBy());

        return userGroupRoleRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<UserGroupRole> existing = userGroupRoleRepository.findByUuid(uuid);
        existing.ifPresent(userGroupRoleRepository::delete);
    }

//    public UserServiceRoleDTO saveOrUpdate(Long userInfo, @NonNull UserServiceRoleDTO dto) {
//
//    }
}
