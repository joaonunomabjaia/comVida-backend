package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mz.org.csaude.comvida.backend.entity.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserServiceRoleRepository extends JpaRepository<UserServiceRole, Long> {

    Optional<UserServiceRole> findByUuid(String uuid);

    Page<UserServiceRole> findByUser(User user, Pageable pageable);

    Page<UserServiceRole> findByRole(Role role, Pageable pageable);

    Page<UserServiceRole> findByProgramActivity(ProgramActivity programActivity, Pageable pageable);

    List<UserServiceRole> findByUserAndProgramActivity(User user, ProgramActivity programActivity);
    List<UserServiceRole> findByUserAndProgramActivityIsNull(User user);

    Optional<UserServiceRole> findByUserAndRoleAndProgramActivity(User user, Role role, ProgramActivity programActivity);
    Optional<UserServiceRole> findByUserAndRoleAndProgramActivityIsNull(User user, Role role);

    boolean existsByUserAndRoleAndProgramActivity(User user, Role role, ProgramActivity programActivity);
    boolean existsByUserAndRoleAndProgramActivityIsNull(User user, Role role);

    long countByRole(Role role);
}
