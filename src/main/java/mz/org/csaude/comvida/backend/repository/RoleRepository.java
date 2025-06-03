package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.entity.User;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<User, Long> {

    //Optional<Role> findByUuid(String uuid);
}
