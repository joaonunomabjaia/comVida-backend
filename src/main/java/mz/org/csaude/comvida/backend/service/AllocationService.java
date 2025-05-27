package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.entity.Allocation;
import mz.org.csaude.comvida.backend.repository.AllocationRepository;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class AllocationService extends BaseService {

    private final AllocationRepository allocationRepository;

    public AllocationService(AllocationRepository allocationRepository) {
        this.allocationRepository = allocationRepository;
    }

    public List<Allocation> findAll(@Nullable Pageable pageable) {
        return allocationRepository.findAll();
    }

    public Optional<Allocation> findById(Long id) {
        return allocationRepository.findById(id);
    }

    public Optional<Allocation> findByUuid(String uuid) {
        return allocationRepository.findByUuid(uuid);
    }

    @Transactional
    public Allocation create(Allocation allocation) {
        allocation.setCreatedAt(DateUtils.getCurrentDate());
        allocation.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return allocationRepository.save(allocation);
    }

    @Transactional
    public Allocation update(Allocation allocation) {
        Optional<Allocation> existing = allocationRepository.findByUuid(allocation.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Allocation not found");
        }

        Allocation toUpdate = existing.get();
        toUpdate.setCohortMember(allocation.getCohortMember());
        toUpdate.setUserServiceRole(allocation.getUserServiceRole());
        toUpdate.setAssignedBy(allocation.getAssignedBy());
        toUpdate.setAllocationDate(allocation.getAllocationDate());
        toUpdate.setForm(allocation.getForm());
        toUpdate.setStatus(allocation.getStatus());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(allocation.getUpdatedBy());

        return allocationRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Allocation> existing = allocationRepository.findByUuid(uuid);
        existing.ifPresent(allocationRepository::delete);
    }
}
