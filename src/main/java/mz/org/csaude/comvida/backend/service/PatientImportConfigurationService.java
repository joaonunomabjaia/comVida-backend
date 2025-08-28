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

    public PatientImportConfiguration updateSchedule(Long cohortId, Long patientImportFileId,
                                                     @Nullable String entryDate, @Nullable String exitDate, String userUuid) {

        // 1️⃣ Buscar configuração
        PatientImportConfiguration config = repository.findByCohortIdAndImportFileId(cohortId, patientImportFileId)
                .orElseThrow(() -> new IllegalArgumentException("Configuração não encontrada"));

        // 2️⃣ Atualizar datas da configuração
        if (entryDate != null) {
            config.setEntryDate(DateUtils.createDate(entryDate, "dd-MM-yyyy"));
        }

        if (exitDate != null) {
            config.setExitDate(DateUtils.createDate(exitDate, "dd-MM-yyyy"));
        }

        config.setUpdatedAt(DateUtils.getCurrentDate());
        config.setUpdatedBy(userUuid);

        repository.update(config);

        // 3️⃣ Buscar membros relacionados
        List<CohortMember> cohortMembers = cohortMemberService.findByCohortIdAndPatientImportFileId(cohortId, patientImportFileId);

        // 4️⃣ Atualizar datas dos membros
        for (CohortMember member : cohortMembers) {
            if (entryDate != null) {
                member.setInclusionDate(DateUtils.createDate(entryDate, "dd-MM-yyyy"));
            }
            if (exitDate != null) {
                member.setExclusionDate(DateUtils.createDate(exitDate, "dd-MM-yyyy"));
            }
            member.setUpdatedAt(DateUtils.getCurrentDate());
            member.setUpdatedBy("System");

            cohortMemberService.update(member);
        }

        return config;
    }

}
