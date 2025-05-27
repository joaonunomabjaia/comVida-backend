package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.HomeVisit;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeVisitRepository extends CrudRepository<HomeVisit, Long> {

    @Override
    List<HomeVisit> findAll();

    @Override
    Optional<HomeVisit> findById(@NotNull Long id);

    // Buscar visitas por cohortMemberId usando JPQL com join
    @Query("SELECT hv FROM HomeVisit hv WHERE hv.cohortMember.id = :cohortMemberId")
    List<HomeVisit> findByCohortMemberId(Long cohortMemberId);

    // Buscar visitas por allocationId
    @Query("SELECT hv FROM HomeVisit hv WHERE hv.allocation.id = :allocationId")
    List<HomeVisit> findByAllocationId(Long allocationId);

    Optional<HomeVisit> findByUuid(String uuid);
}
