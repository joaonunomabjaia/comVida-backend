package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mz.org.csaude.comvida.backend.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameIlike(String username, Pageable pageable);

    @Query("""
        select distinct u from User u
          left join fetch u.userServiceRoles usr
          left join fetch usr.role r
          left join fetch usr.programActivity pa
          left join fetch pa.program p
        where u.id in (:ids)
    """)
    List<User> findByIdInFetchRoles(Collection<Long> ids);

    // ✅ explicit fetch-graph by UUID (name can be anything when using @Query)
    @Query("""
        select u from User u
          left join fetch u.userServiceRoles usr
          left join fetch usr.role r
          left join fetch usr.programActivity pa
          left join fetch usr.userServiceRoleGroups usrg
          left join fetch usrg.group g
        where u.uuid = :uuid
    """)
    Optional<User> fetchByUuidWithGraph(String uuid);

    @Query("""
        select u from User u
          left join fetch u.userServiceRoles usr
          left join fetch usr.role r
          left join fetch usr.programActivity pa
          left join fetch pa.program p
          left join fetch usr.userServiceRoleGroups usrg
          left join fetch usrg.group g
        where u.username = :username
    """)
    Optional<User> findByUsernameWithGraph(String username);

    Optional<User> findByUuid(String uuid);
    Optional<User> findByUsername(String username);
    List<User> findByStatus(String status);
}
