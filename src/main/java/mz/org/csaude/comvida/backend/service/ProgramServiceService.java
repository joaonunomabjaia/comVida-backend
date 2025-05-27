package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.ProgramService;
import mz.org.csaude.comvida.backend.repository.ProgramServiceRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class ProgramServiceService extends BaseService {

    private final ProgramServiceRepository programServiceRepository;

    public ProgramServiceService(ProgramServiceRepository programServiceRepository) {
        this.programServiceRepository = programServiceRepository;
    }

    public List<ProgramService> findAll() {
        return programServiceRepository.findAll();
    }

    public Optional<ProgramService> findById(Long id) {
        return programServiceRepository.findById(id);
    }

    public Optional<ProgramService> findByUuid(String uuid) {
        return programServiceRepository.findByUuid(uuid);
    }

    @Transactional
    public ProgramService create(ProgramService programService) {
        programService.setCreatedAt(DateUtils.getCurrentDate());
        programService.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return programServiceRepository.save(programService);
    }

    @Transactional
    public ProgramService update(ProgramService programService) {
        Optional<ProgramService> existing = programServiceRepository.findByUuid(programService.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("ProgramService not found");
        }

        ProgramService toUpdate = existing.get();
        toUpdate.setServiceName(programService.getServiceName());
        toUpdate.setProgram(programService.getProgram());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(programService.getUpdatedBy());

        return programServiceRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<ProgramService> existing = programServiceRepository.findByUuid(uuid);
        existing.ifPresent(programServiceRepository::delete);
    }

    public void deleteById(Long id) {

    }
}
