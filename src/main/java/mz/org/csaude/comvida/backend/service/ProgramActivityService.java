package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.error.RecordInUseException;
import mz.org.csaude.comvida.backend.repository.GroupRepository;
import mz.org.csaude.comvida.backend.repository.ProgramActivityRepository;
import mz.org.csaude.comvida.backend.repository.ProgramRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.Optional;

@Singleton
public class ProgramActivityService extends BaseService {

    private final ProgramActivityRepository programActivityRepository;
    private final ProgramRepository programRepository;
    private final GroupRepository  groupRepository;

    public ProgramActivityService(ProgramActivityRepository programActivityRepository, ProgramRepository programRepository, GroupRepository groupRepository) {
        this.programActivityRepository = programActivityRepository;
        this.programRepository = programRepository;
        this.groupRepository = groupRepository;
    }

    public Page<ProgramActivity> findAll(@Nullable Pageable pageable) {
        return programActivityRepository.findAll(pageable);
    }

    public Page<ProgramActivity> searchByName(String name, Pageable pageable) {
        return programActivityRepository.findByNameIlike("%" + name + "%", pageable);
    }

    public Optional<ProgramActivity> findById(Long id) {
        return programActivityRepository.findById(id);
    }

    public Optional<ProgramActivity> findByUuid(String uuid) {
        return programActivityRepository.findByUuid(uuid);
    }

    @Transactional
    public ProgramActivity create(ProgramActivity activity) {
        activity.setCreatedAt(DateUtils.getCurrentDate());
        activity.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return programActivityRepository.save(activity);
    }

    @Transactional
    public ProgramActivity update(ProgramActivity activity) {
        Optional<ProgramActivity> existing = programActivityRepository.findByUuid(activity.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("ProgramActivity not found with UUID: " + activity.getUuid());
        }

        Program newProgram = programRepository.findByUuid(activity.getProgram().getUuid()).get();
        ProgramActivity toUpdate = existing.get();
        toUpdate.setName(activity.getName());
        toUpdate.setProgram(newProgram);
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(activity.getUpdatedBy());

        return programActivityRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<ProgramActivity> existing = programActivityRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("ProgramActivity not found with UUID: " + uuid);
        }

        ProgramActivity programActivity = existing.get();

         long count = groupRepository.countByProgramActivity(programActivity);
         if (count > 0) {
             throw new RecordInUseException("A atividade não pode ser eliminada porque está associada a outros registos.");
         }

        programActivityRepository.delete(programActivity);
    }

    @Transactional
    public ProgramActivity updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        Optional<ProgramActivity> existing = programActivityRepository.findByUuid(uuid);
        if (existing.isEmpty()) {
            throw new RuntimeException("ProgramActivity not found with UUID: " + uuid);
        }

        ProgramActivity programActivity = existing.get();
        programActivity.setLifeCycleStatus(newStatus);
        programActivity.setUpdatedAt(DateUtils.getCurrentDate());
        return programActivityRepository.update(programActivity);
    }

}
