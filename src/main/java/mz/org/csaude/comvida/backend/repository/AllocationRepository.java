package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Allocation;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends CrudRepository<Allocation, Long> {

    @Override
    List<Allocation> findAll();

    @Override
    Optional<Allocation> findById(@NotNull Long id);

    List<Allocation> findByStatus(String status);

    List<Allocation> findByCohortMember(CohortMember cohortMember);

    List<Allocation> findByUserServiceRole(UserGroupRole userServiceRole);

    @Query("SELECT a FROM Allocation a WHERE a.assignedBy.id = :userId")
    List<Allocation> findByAssignedByUserId(Long userId);

    Optional<Allocation> findByUuid(String uuid);
}
