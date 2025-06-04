package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.repository.ProgramRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class ProgramService extends BaseService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public Page<Program> findAll(@Nullable Pageable pageable) {
        return programRepository.findAll(pageable);
    }

    public Page<Program> searchByName(String name, Pageable pageable) {
        return programRepository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<Program> findById(Long id) {
        return programRepository.findById(id);
    }

    public Optional<Program> findByUuid(String uuid) {
        return programRepository.findByUuid(uuid);
    }

    @Transactional
    public Program create(Program program) {
        program.setCreatedAt(DateUtils.getCurrentDate());
        program.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return programRepository.save(program);
    }

    @Transactional
    public Program update(Program program) {
        Optional<Program> existing = programRepository.findByUuid(program.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Program not found");
        }

        Program toUpdate = existing.get();
        toUpdate.setName(program.getName());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(program.getUpdatedBy());

        return programRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Program> existing = programRepository.findByUuid(uuid);
        existing.ifPresent(programRepository::delete);
    }
}
