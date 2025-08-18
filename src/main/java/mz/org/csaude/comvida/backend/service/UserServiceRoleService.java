package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.entity.Role;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.entity.UserServiceRole;
import mz.org.csaude.comvida.backend.error.RecordInUseException;
import mz.org.csaude.comvida.backend.repository.ProgramActivityRepository;
import mz.org.csaude.comvida.backend.repository.RoleRepository;
import mz.org.csaude.comvida.backend.repository.UserRepository;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleGroupRepository;
import mz.org.csaude.comvida.backend.repository.UserServiceRoleRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class UserServiceRoleService extends BaseService {

    private final UserServiceRoleRepository repository;
    private final UserServiceRoleGroupRepository userServiceRoleGroupRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProgramActivityRepository programActivityRepository;

    public UserServiceRoleService(UserServiceRoleRepository repository,
                                  UserServiceRoleGroupRepository userServiceRoleGroupRepository,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  ProgramActivityRepository programActivityRepository) {
        this.repository = repository;
        this.userServiceRoleGroupRepository = userServiceRoleGroupRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.programActivityRepository = programActivityRepository;
    }

    /* ============================
       Queries
       ============================ */

    public Page<UserServiceRole> findAll(@Nullable Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<UserServiceRole> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    public Page<UserServiceRole> findByUser(String userUuid, Pageable pageable) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + userUuid));
        return repository.findByUser(user, pageable);
    }

    public Page<UserServiceRole> findByRole(String roleUuid, Pageable pageable) {
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new RuntimeException("Role not found with UUID: " + roleUuid));
        return repository.findByRole(role, pageable);
    }

    public Page<UserServiceRole> findByProgramActivity(String programActivityUuid, Pageable pageable) {
        ProgramActivity pa = programActivityRepository.findByUuid(programActivityUuid)
                .orElseThrow(() -> new RuntimeException("ProgramActivity not found with UUID: " + programActivityUuid));
        return repository.findByProgramActivity(pa, pageable);
    }

    /* ============================
       CRUD básico
       ============================ */

    @Transactional
    public UserServiceRole create(UserServiceRole entity) {
        if (entity.getUser() == null || entity.getUser().getUuid() == null) {
            throw new RuntimeException("User UUID is required");
        }
        if (entity.getRole() == null || entity.getRole().getUuid() == null) {
            throw new RuntimeException("Role UUID is required");
        }

        User user = userRepository.findByUuid(entity.getUser().getUuid())
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + entity.getUser().getUuid()));
        Role role = roleRepository.findByUuid(entity.getRole().getUuid())
                .orElseThrow(() -> new RuntimeException("Role not found with UUID: " + entity.getRole().getUuid()));

        ProgramActivity pa = null;
        if (entity.getProgramActivity() != null && entity.getProgramActivity().getUuid() != null) {
            pa = programActivityRepository.findByUuid(entity.getProgramActivity().getUuid())
                    .orElseThrow(() -> new RuntimeException("ProgramActivity not found with UUID: " + entity.getProgramActivity().getUuid()));
        }

        // Evita duplicado
        if (pa == null) {
            if (repository.existsByUserAndRoleAndProgramActivityIsNull(user, role)) {
                throw new RuntimeException("Combination (user, role, programActivity=NULL) already exists.");
            }
        } else {
            if (repository.existsByUserAndRoleAndProgramActivity(user, role, pa)) {
                throw new RuntimeException("Combination (user, role, programActivity) already exists.");
            }
        }

        entity.setUser(user);
        entity.setRole(role);
        entity.setProgramActivity(pa);
        entity.setCreatedAt(DateUtils.getCurrentDate());
        entity.setLifeCycleStatus(LifeCycleStatus.ACTIVE);

        return repository.save(entity);
    }

    @Transactional
    public UserServiceRole update(UserServiceRole entity) {
        UserServiceRole toUpdate = repository.findByUuid(entity.getUuid())
                .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + entity.getUuid()));

        if (entity.getUser() != null && entity.getUser().getUuid() != null) {
            User user = userRepository.findByUuid(entity.getUser().getUuid())
                    .orElseThrow(() -> new RuntimeException("User not found with UUID: " + entity.getUser().getUuid()));
            toUpdate.setUser(user);
        }

        if (entity.getRole() != null && entity.getRole().getUuid() != null) {
            Role role = roleRepository.findByUuid(entity.getRole().getUuid())
                    .orElseThrow(() -> new RuntimeException("Role not found with UUID: " + entity.getRole().getUuid()));
            toUpdate.setRole(role);
        }

        if (entity.getProgramActivity() != null) {
            if (entity.getProgramActivity().getUuid() == null || entity.getProgramActivity().getUuid().isBlank()) {
                toUpdate.setProgramActivity(null);
            } else {
                ProgramActivity pa = programActivityRepository.findByUuid(entity.getProgramActivity().getUuid())
                        .orElseThrow(() -> new RuntimeException("ProgramActivity not found with UUID: " + entity.getProgramActivity().getUuid()));
                toUpdate.setProgramActivity(pa);
            }
        }

        // Evita duplicado após alterações
        boolean duplicated =
                (toUpdate.getProgramActivity() == null)
                        ? repository.existsByUserAndRoleAndProgramActivityIsNull(toUpdate.getUser(), toUpdate.getRole())
                        && repository.findByUserAndRoleAndProgramActivityIsNull(toUpdate.getUser(), toUpdate.getRole())
                        .map(e -> !e.getId().equals(toUpdate.getId())).orElse(false)
                        : repository.existsByUserAndRoleAndProgramActivity(toUpdate.getUser(), toUpdate.getRole(), toUpdate.getProgramActivity())
                        && repository.findByUserAndRoleAndProgramActivity(toUpdate.getUser(), toUpdate.getRole(), toUpdate.getProgramActivity())
                        .map(e -> !e.getId().equals(toUpdate.getId())).orElse(false);

        if (duplicated) {
            throw new RuntimeException("Combination (user, role, programActivity) already exists.");
        }

        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(entity.getUpdatedBy());

        if (entity.getLifeCycleStatus() != null) {
            toUpdate.setLifeCycleStatus(entity.getLifeCycleStatus());
        }

        return repository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        UserServiceRole existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + uuid));

        long inUse = userServiceRoleGroupRepository.countByUserServiceRole(existing);
        if (inUse > 0) {
            throw new RecordInUseException("Não é possível eliminar: existem grupos associados a este UserServiceRole.");
        }
        repository.delete(existing);
    }

    @Transactional
    public UserServiceRole updateLifeCycleStatus(String uuid, LifeCycleStatus newStatus) {
        UserServiceRole existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("UserServiceRole not found with UUID: " + uuid));
        existing.setLifeCycleStatus(newStatus);
        existing.setUpdatedAt(DateUtils.getCurrentDate());
        return repository.update(existing);
    }

    /* ============================
       Helpers de atribuição de roles (CENTRAL)
       ============================ */

    /** Adiciona um único role ao user (escopo opcional por ProgramActivity). Idempotente. */
    @Transactional
    public UserServiceRole assignRole(String userUuid, String roleUuid, @Nullable String programActivityUuid, String actorUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found: " + userUuid));
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleUuid));

        ProgramActivity pa = null;
        if (programActivityUuid != null && !programActivityUuid.isBlank()) {
            pa = programActivityRepository.findByUuid(programActivityUuid)
                    .orElseThrow(() -> new RuntimeException("ProgramActivity not found: " + programActivityUuid));
        }

        Optional<UserServiceRole> existing =
                (pa == null)
                        ? repository.findByUserAndRoleAndProgramActivityIsNull(user, role)
                        : repository.findByUserAndRoleAndProgramActivity(user, role, pa);

        if (existing.isPresent()) {
            return existing.get(); // idempotente
        }

        UserServiceRole e = new UserServiceRole();
        e.setUser(user);
        e.setRole(role);
        e.setProgramActivity(pa);
        e.setCreatedBy(actorUuid);
        e.setCreatedAt(DateUtils.getCurrentDate());
        e.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return repository.save(e);
    }

    /** Atribui vários roles de uma vez (sem retirar os existentes). */
    @Transactional
    public List<UserServiceRole> assignRoles(String userUuid, @Nullable String programActivityUuid, List<String> roleUuids, String actorUuid) {
        if (roleUuids == null || roleUuids.isEmpty()) return Collections.emptyList();
        List<UserServiceRole> result = new ArrayList<>();
        for (String rUuid : roleUuids) {
            result.add(assignRole(userUuid, rUuid, programActivityUuid, actorUuid));
        }
        return result;
    }

    /** Substitui o conjunto de roles do user para um escopo (programActivity), removendo os que não estiverem na lista. */
    @Transactional
    public List<UserServiceRole> replaceRoles(String userUuid, @Nullable String programActivityUuid, List<String> desiredRoleUuids, String actorUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found: " + userUuid));

        ProgramActivity pa = null;
        if (programActivityUuid != null && !programActivityUuid.isBlank()) {
            pa = programActivityRepository.findByUuid(programActivityUuid)
                    .orElseThrow(() -> new RuntimeException("ProgramActivity not found: " + programActivityUuid));
        }

        // atuais
        List<UserServiceRole> current =
                (pa == null)
                        ? repository.findByUserAndProgramActivityIsNull(user)
                        : repository.findByUserAndProgramActivity(user, pa);

        Set<String> currentRoleUuids = current.stream()
                .map(usr -> usr.getRole().getUuid())
                .collect(Collectors.toSet());

        Set<String> desired = desiredRoleUuids == null ? Collections.emptySet() : new HashSet<>(desiredRoleUuids);

        // remove os que sobraram
        for (UserServiceRole usr : current) {
            if (!desired.contains(usr.getRole().getUuid())) {
                long inUse = userServiceRoleGroupRepository.countByUserServiceRole(usr);
                if (inUse > 0) {
                    throw new RecordInUseException("Não é possível remover role (" + usr.getRole().getUuid() + "): há grupos associados.");
                }
                repository.delete(usr);
            }
        }

        // adiciona os que faltam
        List<UserServiceRole> added = new ArrayList<>();
        for (String toAdd : desired) {
            if (!currentRoleUuids.contains(toAdd)) {
                added.add(assignRole(userUuid, toAdd, programActivityUuid, actorUuid));
            }
        }

        // retorna o estado final
        return (pa == null)
                ? repository.findByUserAndProgramActivityIsNull(user)
                : repository.findByUserAndProgramActivity(user, pa);
    }

    /** Remove um único role do user no escopo indicado. */
    @Transactional
    public void removeRole(String userUuid, String roleUuid, @Nullable String programActivityUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found: " + userUuid));
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleUuid));

        ProgramActivity pa = null;
        if (programActivityUuid != null && !programActivityUuid.isBlank()) {
            pa = programActivityRepository.findByUuid(programActivityUuid)
                    .orElseThrow(() -> new RuntimeException("ProgramActivity not found: " + programActivityUuid));
        }

        Optional<UserServiceRole> existing =
                (pa == null)
                        ? repository.findByUserAndRoleAndProgramActivityIsNull(user, role)
                        : repository.findByUserAndRoleAndProgramActivity(user, role, pa);

        if (existing.isEmpty()) return;

        long inUse = userServiceRoleGroupRepository.countByUserServiceRole(existing.get());
        if (inUse > 0) {
            throw new RecordInUseException("Não é possível remover: existem grupos associados a este UserServiceRole.");
        }
        repository.delete(existing.get());
    }

    public Optional<UserServiceRole> findById(Long id) {
        return repository.findById(id);
    }
}
