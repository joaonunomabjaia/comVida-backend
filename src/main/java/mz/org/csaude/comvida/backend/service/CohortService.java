package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.dto.CohortDTO;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.repository.CohortMemberRepository;
import mz.org.csaude.comvida.backend.repository.CohortRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Singleton
public class CohortService {

    private final CohortRepository repository;
    private final CohortMemberRepository cohortMemberRepository;

    public CohortService(CohortRepository repository, CohortMemberRepository cohortMemberRepository) {
        this.repository = repository;
        this.cohortMemberRepository = cohortMemberRepository;
    }

    public Page<Cohort> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Cohort> searchByName(String name, Pageable pageable) {
        return repository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<Cohort> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Cohort> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    @Transactional
    public Cohort create(Cohort cohort) {
        cohort.setCreatedAt(DateUtils.getCurrentDate());
        cohort.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return repository.save(cohort);
    }

    @Transactional
    public Cohort update(Cohort cohort) {
        Optional<Cohort> existing = repository.findByUuid(cohort.getUuid());
        if (existing.isEmpty()) throw new RuntimeException("Cohort not found");

        Cohort toUpdate = existing.get();
        toUpdate.setName(cohort.getName());
        toUpdate.setDescription(cohort.getDescription());
        toUpdate.setProgramActivity(cohort.getProgramActivity());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(cohort.getUpdatedBy());

        return repository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        repository.findByUuid(uuid).ifPresent(repository::delete);
    }

    public Page<Cohort> findByProgramActivityId(Long programActivityId, Pageable pageable) {
        return repository.findByProgramActivityId(programActivityId, pageable);
    }

    public Optional<Cohort> findByDescription(String description) {
        return repository.findByDescription(description);
    }

    public Page<CohortDTO> findAllWithMembersAndFilters(@Nullable Long serviceId, Pageable pageable) {
        Page<Cohort> cohorts;

        if (serviceId != null) {
            cohorts = repository.findWithMembersByServiceId(serviceId, pageable);
        } else {
            cohorts = repository.findAllWithMembers(pageable);
        }

        // Converter para DTO, buscando só o primeiro member com as datas
        return cohorts.map(cohort -> {
            CohortDTO dto = new CohortDTO(cohort);

            // Buscar o primeiro membro (com menor createdAt, por exemplo)
            Optional<CohortMember> firstMember = cohortMemberRepository
                    .findFirstByCohortOrderByCreatedAtAsc(cohort);

            firstMember.ifPresent(member -> {
                dto.setInclusionDate(DateUtils.toLocalDateSafe(member.getInclusionDate()));
                dto.setExclusionDate(DateUtils.toLocalDateSafe(member.getExclusionDate()));
                dto.setMemberCreatedAt(DateUtils.toLocalDateSafe(member.getCreatedAt()));
            });

            return dto;
        });
    }


}
