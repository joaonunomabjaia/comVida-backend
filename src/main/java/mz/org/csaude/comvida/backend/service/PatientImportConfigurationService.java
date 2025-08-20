package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.*;
import mz.org.csaude.comvida.backend.repository.PatientImportConfigurationRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.Utilities;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.List;
import java.util.Optional;

@Singleton
public class PatientImportConfigurationService {

    @Inject
    private CohortService cohortService;
    @Inject
    private SourceTypeService sourceTypeService;
    @Inject
    ProgramActivityService programActivityService;
    @Inject
    private CohortMemberService cohortMemberService;

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

    public Page<PatientImportConfiguration> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public PatientImportConfiguration registeNew(String cohortDescription, PatientImportFile file) {

        Cohort cohort = cohortService.findByDescription(cohortDescription)
                .orElseThrow(() -> new IllegalArgumentException("Cohort não encontrado: " + cohortDescription));

        PatientImportConfiguration patientImportConfiguration = new PatientImportConfiguration();
        patientImportConfiguration.setCohort(cohort);
        patientImportConfiguration.setProgramActivity(file.getProgramActivity());
        patientImportConfiguration.setImportFile(file);


        patientImportConfiguration.setCreatedBy("System");
        patientImportConfiguration.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        patientImportConfiguration.setCreatedAt(DateUtils.getCurrentDate());
        patientImportConfiguration.setUuid(Utilities.generateUUID());

        return repository.save(patientImportConfiguration);
    }

    public PatientImportConfiguration updateSchedule(Long cohortId, Long patientImportfileId, @Nullable String entryDate, @Nullable String exitDate) {
        PatientImportConfiguration patientImportConfiguration = repository.findByCohortIdAndImportFileId(cohortId, patientImportfileId)
                .orElseThrow(() -> new IllegalArgumentException("Configuração não encontrada"));

        if (entryDate != null) {
            patientImportConfiguration.setEntryDate(DateUtils.createDate(entryDate, "DD-MM-YYYY"));
        }

        if (exitDate != null) {
            patientImportConfiguration.setExitDate(DateUtils.createDate(exitDate,  "DD-MM-YYYY"));
        }
        patientImportConfiguration.setUpdatedAt(DateUtils.getCurrentDate());
        patientImportConfiguration.setUpdatedBy("System");

        // a seguir vamos pegar todos cohortMember pertencentes a esta comfig
        List<CohortMember> cohortMembers = cohortMemberService.findByCohortIdAndPatientImportFileId(cohortId, patientImportConfiguration.getId());

        // vamos fazer um loop que actualiza o inclusionDate e exclusionDate de cada Membro

        cohortMembers.forEach(member -> {
            member.setUpdatedAt(DateUtils.getCurrentDate());
            member.setUpdatedBy("System");
            member.setInclusionDate(DateUtils.createDate(entryDate, "DD-MM-YYYY"));
            member.setExclusionDate(DateUtils.createDate(exitDate, "DD-MM-YYYY"));

            cohortMemberService.update(member);
        });


        return repository.update(patientImportConfiguration);
    }
}
