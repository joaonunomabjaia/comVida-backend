package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;

@Repository
public interface PatientImportFileRepository extends CrudRepository<PatientImportFile, Long> {
}
