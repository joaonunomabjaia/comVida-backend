package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mz.org.csaude.comvida.backend.entity.Group;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.entity.UserServiceRoleGroup;

import java.util.Optional;

@Repository
public interface UserServiceRoleGroupRepository  extends JpaRepository<UserServiceRoleGroup, Long> {

    Optional<UserServiceRoleGroup> findByUuid(String uuid);

    long countByUserServiceRole(UserServiceRole userServiceRole);

    long countByGroup(Group group);

    boolean existsByUserServiceRoleAndGroup(UserServiceRole userServiceRole, Group group);

    boolean existsByUserServiceRoleAndGroupAndIdNot(UserServiceRole userServiceRole, Group group, Long id);

    Page<UserServiceRoleGroup> findByUserServiceRole(UserServiceRole userServiceRole, Pageable pageable);
}
