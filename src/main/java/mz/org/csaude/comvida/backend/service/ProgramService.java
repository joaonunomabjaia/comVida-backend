package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.error.RecordInUseException;
import mz.org.csaude.comvida.backend.repository.ProgramActivityRepository;
import mz.org.csaude.comvida.backend.repository.ProgramRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class ProgramService extends BaseService {

    private final ProgramRepository programRepository;
    private final ProgramActivityRepository programActivityRepository;

    public ProgramService(ProgramRepository programRepository, ProgramActivityRepository programActivityRepository) {
        this.programRepository = programRepository;
        this.programActivityRepository = programActivityRepository;
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
        toUpdate.setDescription(program.getDescription());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(program.getUpdatedBy());

        return programRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Program> existing = programRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("Program not found with UUID: " + uuid);
        }

        Program program = existing.get();

        long count = programActivityRepository.countByProgram(program);
        if (count > 0) {
            throw new RecordInUseException("O programa não pode ser eliminado porque está associado a uma ou mais actividades.");
        }

        programRepository.delete(program);
    }

    @Transactional
    public Program updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        Optional<Program> existing = programRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("Program not found with UUID: " + uuid);
        }

        Program program = existing.get();
        program.setLifeCycleStatus(newStatus);
        program.setUpdatedAt(DateUtils.getCurrentDate());
        return programRepository.update(program);
    }

}
