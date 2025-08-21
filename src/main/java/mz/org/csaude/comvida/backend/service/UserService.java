package mz.org.csaude.comvida.backend.service;


import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.*;
import mz.org.csaude.comvida.backend.repository.*;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.Utilities;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class UserService extends BaseService {

    private final UserServiceRoleRepository userServiceRoleRepository;
    private final UserServiceRoleGroupRepository userServiceRoleGroupRepository;
    private final RoleRepository roleRepository;
    private final ProgramActivityRepository programActivityRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository,
                       UserServiceRoleRepository userServiceRoleRepository,
                       UserServiceRoleGroupRepository userServiceRoleGroupRepository,
                       RoleRepository roleRepository,
                       ProgramActivityRepository programActivityRepository,
                       GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.userServiceRoleRepository = userServiceRoleRepository;
        this.userServiceRoleGroupRepository = userServiceRoleGroupRepository;
        this.roleRepository = roleRepository;
        this.programActivityRepository = programActivityRepository;
        this.groupRepository = groupRepository;
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public Page<User> findAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return hydrateRolesAndGroups(page);
    }

    @Transactional
    public Page<User> searchByName(String name, Pageable pageable) {
        String like = (name == null || name.isBlank()) ? "%" : "%" + name + "%";
        Page<User> page = userRepository.findByUsernameIlike(like, pageable);
        return hydrateRolesAndGroups(page);
    }

    // UserService.java (only the hydrateRolesAndGroups method)
    private Page<User> hydrateRolesAndGroups(Page<User> page) {
        if (page.isEmpty()) return page;

        // 1) users + roles
        List<Long> userIds = page.getContent().stream()
                .map(User::getId).filter(Objects::nonNull).toList();

        List<User> usersWithRoles = userRepository.findByIdInFetchRoles(userIds);

        // 2) collect the **exact** USR ids we just attached
        List<Long> usrIds = usersWithRoles.stream()
                .flatMap(u -> u.getUserServiceRoles().stream())
                .map(UserServiceRole::getId)
                .filter(Objects::nonNull)
                .toList();

        // 3) re-hydrate groups for those USR ids (attaches to same managed instances)
        if (!usrIds.isEmpty()) {
            userServiceRoleRepository.fetchWithGroupsByUsrIds(usrIds);

            // force init (optional)
            usersWithRoles.forEach(u ->
                    u.getUserServiceRoles().forEach(usr ->
                            usr.getUserServiceRoleGroups().size()
                    )
            );
        }

        // keep original page order
        Map<Long, User> byId = usersWithRoles.stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a,b)->a, LinkedHashMap::new));
        List<User> ordered = page.getContent().stream()
                .map(u -> byId.getOrDefault(u.getId(), u)).toList();

        return Page.of(ordered, page.getPageable(), page.getTotalSize());
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Transactional
    public User create(User user) {
        // 0) Hold incoming roles & groups, but DO NOT replace the collection on the entity
        final List<UserServiceRole> payloadRoles =
                user.getUserServiceRoles() != null ? new ArrayList<>(user.getUserServiceRoles()) : new ArrayList<>();
        if (user.getUserServiceRoles() != null) {
            // mutate the same list instance to avoid cascade on persist (no cascade anyway)
            user.getUserServiceRoles().clear();
        }

        // 1) Persist the user (no children yet)
        user.setCreatedAt(DateUtils.getCurrentDate());
        user.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        user.setStatus(user.getStatus() != null ? user.getStatus() : LifeCycleStatus.ACTIVE.name());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setSalt(Utilities.generateSalt());
            user.setPassword(Utilities.encryptPassword(user.getPassword(), user.getSalt()));
        }
        final User saved = userRepository.save(user);

        // 2) Persist roles (and role->groups) manually
        final Set<String> seen = new HashSet<>();
        final List<UserServiceRole> persistedRoles = new ArrayList<>();

        for (UserServiceRole incoming : payloadRoles) {
            // validate & normalize
            final String roleUuid = incoming.getRole() != null ? incoming.getRole().getUuid() : null;
            final Long paId = incoming.getProgramActivity() != null ? incoming.getProgramActivity().getId() : null;
            if (roleUuid == null || roleUuid.isBlank()) {
                throw new RuntimeException("roleUuid is required for UserServiceRole");
            }
            final String dupeKey = roleUuid + "|" + String.valueOf(paId);
            if (!seen.add(dupeKey)) {
                throw new RuntimeException("Duplicate (role, programActivity) in payload: " + dupeKey);
            }

            // attach user
            incoming.setUser(saved);

            // resolve role
            Role role = roleRepository.findByUuid(roleUuid)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleUuid));
            incoming.setRole(role);

            // resolve program activity (by ID; switch to UUID if you prefer)
            ProgramActivity pa = null;
            if (paId != null) {
                pa = programActivityRepository.findById(paId)
                        .orElseThrow(() -> new RuntimeException("ProgramActivity not found with ID: " + paId));
            }
            incoming.setProgramActivity(pa);

            // check duplicate in DB
            boolean exists = (pa == null)
                    ? userServiceRoleRepository.existsByUserAndRoleAndProgramActivityIsNull(saved, role)
                    : userServiceRoleRepository.existsByUserAndRoleAndProgramActivity(saved, role, pa);
            if (exists) {
                throw new RuntimeException("Combination (user, role, programActivity) already exists.");
            }

            // defaults
            incoming.setLifeCycleStatus(
                    incoming.getLifeCycleStatus() != null ? incoming.getLifeCycleStatus() : LifeCycleStatus.ACTIVE);
            incoming.setCreatedAt(DateUtils.getCurrentDate());
            incoming.setCreatedBy(saved.getCreatedBy());

            // persist role
            UserServiceRole savedUsr = userServiceRoleRepository.save(incoming);
            persistedRoles.add(savedUsr);

            // groups (optional transient list on the DTO/entity)
            if (incoming.getGroupUuids() != null && !incoming.getGroupUuids().isEmpty()) {
                for (String gUuid : new HashSet<>(incoming.getGroupUuids())) {
                    if (gUuid == null || gUuid.isBlank()) continue;

                    Group grp = groupRepository.findByUuid(gUuid)
                            .orElseThrow(() -> new RuntimeException("Group not found: " + gUuid));

                    // optional: ensure group belongs to same PA
                    if (pa != null && grp.getProgramActivity() != null) {
                        Long grpPaId = grp.getProgramActivity().getId();
                        if (grpPaId != null && !grpPaId.equals(pa.getId())) {
                            throw new RuntimeException("Group " + gUuid + " does not belong to ProgramActivity ID " + pa.getId());
                        }
                    }

                    if (!userServiceRoleGroupRepository.existsByUserServiceRoleAndGroup(savedUsr, grp)) {
                        UserServiceRoleGroup link = new UserServiceRoleGroup();
                        link.setUserServiceRole(savedUsr);
                        link.setGroup(grp);
                        link.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                        link.setCreatedAt(DateUtils.getCurrentDate());
                        link.setCreatedBy(savedUsr.getCreatedBy());
                        userServiceRoleGroupRepository.save(link);
                    }
                }
            }
        }

        // 3) Attach roles to the user WITHOUT replacing the collection instance
        saved.getUserServiceRoles().clear();
        saved.getUserServiceRoles().addAll(persistedRoles);

        return saved;
    }

    @Transactional
    public User update(User user) {
        Optional<User> existing = userRepository.findByUuid(user.getUuid());
        if (existing.isEmpty()) throw new RuntimeException("User not found");

        User toUpdate = existing.get();
        toUpdate.setUsername(user.getUsername());
        toUpdate.setPassword(user.getPassword());
        toUpdate.setStatus(user.getStatus());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(user.getUpdatedBy());

        return userRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<User> existing = userRepository.findByUuid(uuid);
        existing.ifPresent(userRepository::delete);
    }

    @Transactional
    public User updateLifeCycleStatus(String uuid, LifeCycleStatus status) {
        Optional<User> existing = userRepository.findByUuid(uuid);
        if (existing.isEmpty()) throw new RuntimeException("User not found");

        User user = existing.get();
        user.setStatus(String.valueOf(status));
        user.setUpdatedAt(DateUtils.getCurrentDate());
        return userRepository.update(user);
    }

    public Optional<User> getByUserName(String identity) {
        return userRepository.findByUsername(identity);
    }

    @Transactional
    public Optional<User> getGraphByUserName(String identity) {
        return userRepository.findByUsernameWithGraph(identity);
    }

}
