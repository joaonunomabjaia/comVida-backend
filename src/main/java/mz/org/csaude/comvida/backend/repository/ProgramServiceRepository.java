package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.ProgramService;
import mz.org.csaude.comvida.backend.entity.Program;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramServiceRepository extends CrudRepository<ProgramService, Long> {

    @Override
    List<ProgramService> findAll();

    @Override
    Optional<ProgramService> findById(@NotNull Long id);

    List<ProgramService> findByProgram(Program program);

    Optional<ProgramService> findByUuid(String uuid);
}
