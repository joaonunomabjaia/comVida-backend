package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Program;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends CrudRepository<Program, Long> {

    @Override
    List<Program> findAll();

    @Override
    Optional<Program> findById(@NotNull Long id);

    Optional<Program> findByName(String name);

    Optional<Program> findByUuid(String uuid);
}
