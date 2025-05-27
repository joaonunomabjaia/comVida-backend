package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Patient;
import mz.org.csaude.comvida.backend.repository.PatientRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class PatientService extends BaseService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> findByUuid(String uuid) {
        return patientRepository.findByUuid(uuid);
    }

    @Transactional
    public Patient create(Patient patient) {
        patient.setCreatedAt(DateUtils.getCurrentDate());
        patient.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient update(Patient patient) {
        Optional<Patient> existing = patientRepository.findByUuid(patient.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Patient toUpdate = existing.get();
        toUpdate.setPerson(patient.getPerson());
        toUpdate.setPatientIdentifier(patient.getPatientIdentifier());
        toUpdate.setStatus(patient.getStatus());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(patient.getUpdatedBy());

        return patientRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Patient> existing = patientRepository.findByUuid(uuid);
        existing.ifPresent(patientRepository::delete);
    }
}
