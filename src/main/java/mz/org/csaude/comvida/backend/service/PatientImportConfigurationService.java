package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.PatientImportConfiguration;
import mz.org.csaude.comvida.backend.repository.PatientImportConfigurationRepository;

import java.util.List;
import java.util.Optional;

@Singleton
public class PatientImportConfigurationService {

    private final PatientImportConfigurationRepository repository;

    public PatientImportConfigurationService(PatientImportConfigurationRepository repository) {
        this.repository = repository;
    }

    public List<PatientImportConfiguration> findAll() {
        return (List<PatientImportConfiguration>) repository.findAll();
    }

    public Optional<PatientImportConfiguration> findById(Long id) {
        return repository.findById(id);
    }

    public PatientImportConfiguration save(PatientImportConfiguration config) {
        return repository.save(config);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
