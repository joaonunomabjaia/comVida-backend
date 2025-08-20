package mz.org.csaude.comvida.backend.repository;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.PatientImportConfiguration;
import mz.org.csaude.comvida.backend.entity.Program;

import java.util.Optional;

@Repository
public interface PatientImportConfigurationRepository extends CrudRepository<PatientImportConfiguration, Long> {

    Page<PatientImportConfiguration> findAll(Pageable pageable);

    @Override
    Optional<PatientImportConfiguration> findById(@NotNull Long id);

    Optional<PatientImportConfiguration> findByCohortIdAndImportFileId(Long cohortId, Long importFileId);


}
