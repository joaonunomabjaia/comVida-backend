package mz.org.csaude.comvida.backend.service;

import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.repository.PatientImportFileRepository;

import java.util.Date;
import java.util.Optional;

@Singleton
public class PatientImportFileService {

    private final PatientImportFileRepository repository;

    public PatientImportFileService(PatientImportFileRepository repository) {
        this.repository = repository;
    }

    public PatientImportFile save(String fileName, byte[] content) {
        PatientImportFile file = new PatientImportFile();
        file.setMessage(fileName);
        file.setFile(content);
        file.setStatus(PatientImportFile.ImportStatus.PENDING);
        file.setProgress(0);
        file.setCreatedAt(new Date());
        return repository.save(file);
    }

    public Optional<PatientImportFile> findById(Long id) {
        return repository.findById(id);
    }

    public Iterable<PatientImportFile> findAll() {
        return repository.findAll();
    }

    public PatientImportFile updateStatusAndProgress(Long id, PatientImportFile.ImportStatus status, int progress) {
        return repository.findById(id)
                .map(file -> {
                    file.setStatus(status);
                    file.setProgress(progress);
                    return repository.update(file);
                })
                .orElseThrow(() -> new RuntimeException("Ficheiro não encontrado com ID: " + id));
    }

    public boolean processFileAsync(Long id) {
        return repository.findById(id).isPresent();
    }
}
