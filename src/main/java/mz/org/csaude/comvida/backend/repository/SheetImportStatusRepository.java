package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.entity.SheetImportStatus;

import java.util.List;

@Repository
public interface SheetImportStatusRepository extends CrudRepository<SheetImportStatus, Long> {

    List<SheetImportStatus> findByFile(PatientImportFile file);

    Page<SheetImportStatus> findByFile(PatientImportFile file, Pageable pageable);

    void deleteByFile(PatientImportFile file);
}
