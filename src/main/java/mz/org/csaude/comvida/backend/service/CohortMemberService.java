package mz.org.csaude.comvida.backend.service;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.dto.CohortWithMembersDTO;
import mz.org.csaude.comvida.backend.entity.*;
import mz.org.csaude.comvida.backend.repository.CohortMemberRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.repository.CohortRepository;
import mz.org.csaude.comvida.backend.repository.PatientRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.SimplePage;
import mz.org.csaude.comvida.backend.util.SourceTypeEnum;
import mz.org.csaude.comvida.backend.util.Utilities;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Singleton
public class CohortMemberService extends BaseService {

    private final CohortMemberRepository cohortMemberRepository;
    @Inject
    PatientRepository patientRepository;
    @Inject
    CohortRepository cohortRepository;
    @Inject
    private PatientService patientService;
    @Inject
    private CohortService cohortService;
    @Inject
    private SourceTypeService sourceTypeService;
    @Inject
    private SourceSystemService sourceSystemService;

    public CohortMemberService(CohortMemberRepository cohortMemberRepository) {
        this.cohortMemberRepository = cohortMemberRepository;
    }

    public Page<CohortWithMembersDTO> getCohortsWithMembers(Pageable pageable) {
        Page<Cohort> pagedCohorts = cohortRepository.findAll(pageable);
        List<CohortWithMembersDTO> result = new ArrayList<>();

        for (Cohort cohort : pagedCohorts.getContent()) {
            List<CohortMember> members = cohortMemberRepository.findByCohort(cohort);
            result.add(new CohortWithMembersDTO(cohort, members));
        }

        // Aqui retornamos um Page manualmente
        return new SimplePage<>(
                result,
                pageable,
                pagedCohorts.getTotalSize()
        );
    }



    public List<CohortMember> findAll() {
        return cohortMemberRepository.findAll();
    }

    public Optional<CohortMember> findById(Long id) {
        return cohortMemberRepository.findById(id);
    }

    public Optional<CohortMember> findByUuid(String uuid) {
        return cohortMemberRepository.findByUuid(uuid);
    }

    @Transactional
    public CohortMember create(CohortMember cohortMember) {
        cohortMember.setCreatedAt(DateUtils.getCurrentDate());
        cohortMember.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return cohortMemberRepository.save(cohortMember);
    }

    public void createFromExcel(String patientUUID, String cohortDescription, String sourceName) {
        Patient patient = patientService.findByUuid(patientUUID)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + patientUUID));
//
        Cohort cohort = cohortService.findByDescription(cohortDescription)
                .orElseThrow(() -> new IllegalArgumentException("Cohort não encontrado: " + cohortDescription));
//
        SourceSystem source = sourceSystemService.findByCode(sourceName)
                .orElseThrow(() -> new IllegalArgumentException("Fonte não encontrada: " + sourceName));

        CohortMember member = new CohortMember();
        member.setPatient(patient);
        member.setCohort(cohort);
        member.setSourceSystem(source);
        member.setInclusionDate(DateUtils.getCurrentDate());
        member.setCreatedAt(DateUtils.getCurrentDate());
        member.setOriginId("Importação via Excel");
        member.setSourceType(SourceTypeEnum.FILE);
        member.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        member.setCreatedBy("System");
        member.setUuid(Utilities.generateUUID());

        cohortMemberRepository.save(member);
    }


    @Transactional
    public CohortMember update(CohortMember cohortMember) {
        Optional<CohortMember> existing = cohortMemberRepository.findByUuid(cohortMember.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("CohortMember not found");
        }

        CohortMember toUpdate = existing.get();
        toUpdate.setCohort(cohortMember.getCohort());
        toUpdate.setPatient(cohortMember.getPatient());
        toUpdate.setSourceSystem(cohortMember.getSourceSystem());
        toUpdate.setOriginId(cohortMember.getOriginId());
        toUpdate.setInclusionDate(cohortMember.getInclusionDate());
        toUpdate.setExclusionDate(cohortMember.getExclusionDate());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohortMember.getUpdatedBy());

        return cohortMemberRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<CohortMember> existing = cohortMemberRepository.findByUuid(uuid);
        existing.ifPresent(cohortMemberRepository::delete);
    }
}
