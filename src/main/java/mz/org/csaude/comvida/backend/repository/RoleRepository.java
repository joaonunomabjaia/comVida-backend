package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mz.org.csaude.comvida.backend.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByUuid(String uuid);


    Page<Role> findByNameIlike(String name, Pageable pageable);

    Page<Role> findByDescriptionIlike(String code, Pageable pageable);

    Page<Role> findByNameIlikeOrDescriptionIlike(String name, String code, Pageable pageable);

    boolean existsByName(String code);

    boolean existsByNameIgnoreCase(String name);
}
