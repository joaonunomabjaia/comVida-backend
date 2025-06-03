package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;

import java.util.Optional;

@Repository
public interface ProgramActivityRepository extends JpaRepository<ProgramActivity, Long> {
    Optional<ProgramActivity> findByUuid(String uuid);

    Page<ProgramActivity> findByNameIlike(String name, Pageable pageable);
}
