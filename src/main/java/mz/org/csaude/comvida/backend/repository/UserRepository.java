package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(@NotNull Long id);

    Optional<User> findByUsername(String username);

    List<User> findByStatus(String status);

    Optional<User> findByUuid(String uuid);
}
