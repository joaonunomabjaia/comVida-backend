package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;

import java.util.List;

@Repository
public interface PatientImportFileRepository extends CrudRepository<PatientImportFile, Long> {

    List<PatientImportFile> findByStatus(PatientImportFile.ImportStatus importStatus);

    Page<PatientImportFile> findByStatusIn(List<PatientImportFile.ImportStatus> statusList, Pageable pageable);

    Page<PatientImportFile> findAll(Pageable pageable);

    Page<PatientImportFile> findByStatusInAndNameIlike(List<PatientImportFile.ImportStatus> statusList, String name, Pageable pageable);

    Page<PatientImportFile> findByNameIlike(String name, Pageable pageable);

}
