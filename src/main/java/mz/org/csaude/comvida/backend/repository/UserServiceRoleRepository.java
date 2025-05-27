package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserServiceRoleRepository extends CrudRepository<UserServiceRole, Long> {

    @Override
    List<UserServiceRole> findAll();

    @Override
    Optional<UserServiceRole> findById(@NotNull Long id);

    List<UserServiceRole> findByUser(User user);

    Optional<UserServiceRole> findByUuid(String uuid);
}
