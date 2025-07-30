package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.entity.SheetImportStatus;
import mz.org.csaude.comvida.backend.repository.SheetImportStatusRepository;

import java.util.List;

@Singleton
@RequiredArgsConstructor
public class SheetImportStatusService {

    private final SheetImportStatusRepository repository;

    @Transactional
    public SheetImportStatus save(SheetImportStatus status) {
        return repository.save(status);
    }

    public List<SheetImportStatus> findByPatientImportFile(PatientImportFile file) {
        return repository.findByFile(file);
    }
}
