package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;
import mz.org.csaude.comvida.backend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRoleRepository extends CrudRepository<UserGroupRole, Long> {

    @Override
    List<UserGroupRole> findAll();

    @Override
    Optional<UserGroupRole> findById(@NotNull Long id);

    List<UserGroupRole> findByUser(User user);

    Optional<UserGroupRole> findByUuid(String uuid);
}
