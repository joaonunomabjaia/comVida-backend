package mz.org.csaude.comvida.backend.repository;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Cohort;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface CohortMemberRepository extends CrudRepository<CohortMember, Long> {
    
    Page<CohortMember> findAll(Pageable pageable);

    @Override
    Optional<CohortMember> findById(@NotNull Long id);

    List<CohortMember> findByCohortId(Long cohortId);

    Page<CohortMember> findByCohortId(Long cohortId, Pageable pageable);

    List<CohortMember> findByPatientId(Long patientId);

    Optional<CohortMember> findByOriginId(String originId);

    Optional<CohortMember> findByUuid(String uuid);

    List<CohortMember> findByCohort(Cohort cohort);

    Optional<CohortMember> findFirstByCohortOrderByCreatedAtAsc(Cohort cohort);

    Optional<CohortMember> findFirstByPatientImportFileOrderByCreatedAtAsc(PatientImportFile patientImportFile);

    @Query(
            nativeQuery = true,
            value = """
        SELECT cm.*
        FROM cohort_members cm
        WHERE (:patientImportFileId IS NULL OR cm.import_file_id = :patientImportFileId)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM cohort_members cm
        WHERE (:patientImportFileId IS NULL OR c.import_file_id = :patientImportFileId)
        """
    )
    Page<CohortMember> findByPatientImportFileId(Long patientImportFileId, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = """
        SELECT cm.*
        FROM cohort_members cm
        WHERE (:cohortId IS NULL OR cm.cohort_id = :cohortId)
          AND (:patientImportFileId IS NULL OR cm.import_file_id = :patientImportFileId)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM cohort_members cm
        WHERE (:cohortId IS NULL OR cm.cohort_id = :cohortId)
          AND (:patientImportFileId IS NULL OR cm.import_file_id = :patientImportFileId)
        """
    )
    Page<CohortMember> findByCohortIdAndPatientImportFileId(Long cohortId, Long patientImportFileId, Pageable pageable);

//    @Query(
//            value = """
//        SELECT cm
//        FROM CohortMember cm
//        WHERE (:cohortId IS NULL OR cm.cohort.id = :cohortId)
//          AND (:patientImportFileId IS NULL OR cm.patientImportFile.id = :patientImportFileId)
//    """,
//            countQuery = """
//        SELECT COUNT(cm)
//        FROM CohortMember cm
//        WHERE (:cohortId IS NULL OR cm.cohort.id = :cohortId)
//          AND (:patientImportFileId IS NULL OR cm.patientImportFile.id = :patientImportFileId)
//    """
//    )
//    Page<CohortMember> findByCohortIdAndPatientImportFileId(@Nullable Long cohortId, @Nullable Long patientImportFileId, Pageable pageable);


    @Query(
            nativeQuery = true,
            value = """
        SELECT cm.*
        FROM cohort_members cm
        WHERE cm.cohort_id = :cohortId
          AND cm.import_file_id = :patientImportFileId
    """
    )
    List<CohortMember> findByCohortIdAndPatientImportFileId(
            @NotNull Long cohortId,
            @NotNull Long patientImportFileId
    );


    @Query(
            nativeQuery = true,
            value = """
        SELECT cm.*
        FROM cohort_members cm
        JOIN cohorts c ON cm.cohort_id = c.id
        WHERE (:activityId IS NULL OR c.program_activity_id = :activityId)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM cohort_members cm
        JOIN cohorts c ON cm.cohort_id = c.id
        WHERE (:activityId IS NULL OR c.program_activity_id = :activityId)
        """
    )
    Page<CohortMember> findByProgramActivityId(@Nullable String activityId, Pageable pageable);

    @Query("SELECT cm FROM CohortMember cm WHERE cm.id IN :memberIds")
    List<CohortMember> findAllByIdIn(List<Long> memberIds);

    long countByCohort(Cohort cohort);

    boolean existsByCreatedBy(String createdBy);
    long countByCreatedBy(String createdBy);
}
