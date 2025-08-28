package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.dto.CohortMemberDTO;
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
    private GroupService groupService;
    @Inject
    private SourceTypeService sourceTypeService;
    @Inject
    private SourceSystemService sourceSystemService;
    @Inject
    private UserService userService;

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



    public Page<CohortMember> findAll(@Nullable Pageable pageable) {
        return cohortMemberRepository.findAll(pageable);
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

    public void createFromExcel(String patientOriginId, String cohortDescription, PatientImportFile file) {
        Patient patient = patientService.findByUuid(patientOriginId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + patientOriginId));
//
        Cohort cohort = cohortService.findByDescription(cohortDescription)
                .orElseThrow(() -> new IllegalArgumentException("Cohort não encontrado: " + cohortDescription));

        Group group = groupService.findById(file.getGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado: " + file.getGroup().getId()));
//
        SourceSystem source = sourceSystemService.findByCode(file.getSourceSystem().getCode())
                .orElseThrow(() -> new IllegalArgumentException("Fonte não encontrada: " + file.getSourceSystem().getCode()));

        CohortMember member = new CohortMember();
        member.setPatient(patient);
        member.setCohort(cohort);
        member.setGroup(group);
        member.setSourceSystem(source);
        member.setInclusionDate(DateUtils.getCurrentDate());
        member.setCreatedAt(DateUtils.getCurrentDate());
        member.setOriginId(patientOriginId);
        member.setSourceType(SourceTypeEnum.FILE);
        member.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        member.setCreatedBy("System");
        member.setUuid(Utilities.generateUUID());
        member.setPatientImportFile(file);

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
    public CohortMember allocation(Long cohortMemberId, Long assignedByUserId, String authUuid) {

        CohortMember MemberToUpdate = cohortMemberRepository.findById(cohortMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado: " + cohortMemberId));
        User assignedByUser = userService.findById(assignedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario não encontrado: " + assignedByUserId));


        MemberToUpdate.setAssignedBy(assignedByUser);
        MemberToUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        MemberToUpdate.setUpdatedBy(authUuid);

        return cohortMemberRepository.update(MemberToUpdate);
    }

    @Transactional
    public List<CohortMember> bulkAllocation(List<Long> memberIds, Long assignedByUserId, String authUuid) {
        User assignedByUser = userService.findById(assignedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + assignedByUserId));

        List<CohortMember> members = cohortMemberRepository.findAllByIdIn(memberIds);

        if (members.size() != memberIds.size()) {
            List<Long> foundIds = members.stream()
                    .map(CohortMember::getId)
                    .toList();

            List<Long> missing = memberIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new IllegalArgumentException("Membros não encontrados: " + missing);
        }

        for (CohortMember m : members) {
            m.setAssignedBy(assignedByUser);
            m.setUpdatedAt(DateUtils.getCurrentDate());
            m.setUpdatedBy(authUuid);
        }

        return cohortMemberRepository.updateAll(members);
    }



    @Transactional
    public void delete(String uuid) {
        Optional<CohortMember> existing = cohortMemberRepository.findByUuid(uuid);
        existing.ifPresent(cohortMemberRepository::delete);
    }

    public Page<CohortMember> findByCohortId(Long cohortId, Pageable pageable) {
        return cohortMemberRepository.findByCohortId(cohortId, pageable);
    }

    public Page<CohortMember> findByPatientIportFileId(Long patientImportFileId, Pageable pageable) {
        return cohortMemberRepository.findByPatientImportFileId(patientImportFileId, pageable);
    }

    public Page<CohortMember> findByCohortIdAndPatientImportFileId(Long cohortId, Long patientImportFileId, Pageable pageable) {
        return cohortMemberRepository.findByCohortIdAndPatientImportFileId(cohortId, patientImportFileId, pageable);
    }

    public Page<CohortMember> searchByActivity(@Nullable String activityId, Pageable pageable) {
        return cohortMemberRepository.findByProgramActivityId(activityId, pageable);
    }

    public List<CohortMember> findByCohortIdAndPatientImportFileId(Long cohortId, Long patientImportFileId) {
        return cohortMemberRepository.findByCohortIdAndPatientImportFileId(cohortId, patientImportFileId);
    }

}
