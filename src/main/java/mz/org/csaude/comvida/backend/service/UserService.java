package mz.org.csaude.comvida.backend.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.dto.imports.ImportErrorItemDTO;
import mz.org.csaude.comvida.backend.dto.imports.ImportResultDTO;
import mz.org.csaude.comvida.backend.dto.imports.ImportUserRowDTO;
import mz.org.csaude.comvida.backend.dto.imports.ValidateImportResultDTO;
import mz.org.csaude.comvida.backend.entity.*;
import mz.org.csaude.comvida.backend.repository.*;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.Utilities;
import mz.org.fgh.mentoring.util.LifeCycleStatus;
import org.apache.commons.lang3.StringUtils;

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
    private final CohortMemberRepository cohortMemberRepository;
    private final PersonRepository personRepository;
    private final SourceSystemRepository  sourceSystemRepository;
    private static final ObjectMapper M = new ObjectMapper();
    private static String norm(String v){ return v==null? "": v.trim(); }
    private static String lower(String v){ return norm(v).toLowerCase(); }

    public UserService(UserRepository userRepository,
                       UserServiceRoleRepository userServiceRoleRepository,
                       UserServiceRoleGroupRepository userServiceRoleGroupRepository,
                       RoleRepository roleRepository,
                       ProgramActivityRepository programActivityRepository,
                       GroupRepository groupRepository, CohortMemberRepository cohortMemberRepository, PersonRepository personRepository, SourceSystemRepository sourceSystemRepository) {
        this.userRepository = userRepository;
        this.userServiceRoleRepository = userServiceRoleRepository;
        this.userServiceRoleGroupRepository = userServiceRoleGroupRepository;
        this.roleRepository = roleRepository;
        this.programActivityRepository = programActivityRepository;
        this.groupRepository = groupRepository;
        this.cohortMemberRepository = cohortMemberRepository;
        this.personRepository = personRepository;
        this.sourceSystemRepository = sourceSystemRepository;
    }

    private static String buildNamesJson(String first, String last) {
        try {
            var node = new LinkedHashMap<String,Object>();
            node.put("firstName", norm(first));
            node.put("lastName",  norm(last));
            node.put("prefered",  true);
            var arr = new ArrayList<Map<String,Object>>();
            arr.add(node);
            return M.writeValueAsString(arr);
        } catch (JsonProcessingException e) { return "[]"; }
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
    public User update(User incoming) {
        User db = userRepository.findByUuid(incoming.getUuid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ----- basic fields (NO password / salt changes) -----
        db.setUsername(incoming.getUsername());
        if (incoming.getStatus() != null) db.setStatus(incoming.getStatus());
        if (incoming.getLifeCycleStatus() != null) db.setLifeCycleStatus(incoming.getLifeCycleStatus());
        db.setSex(incoming.getSex());
        db.setBirthdate(incoming.getBirthdate());
        db.setNames(incoming.getNames());
        db.setAddress(incoming.getAddress());
        db.setPersonAttributes(incoming.getPersonAttributes());
        db.setAttributes(incoming.getAttributes());
        db.setUpdatedAt(DateUtils.getCurrentDate());
        db.setUpdatedBy(incoming.getUpdatedBy());

        // ----- normalize payload roles -----
        final List<UserServiceRole> payloadRoles =
                incoming.getUserServiceRoles() != null ? new ArrayList<>(incoming.getUserServiceRoles())
                        : new ArrayList<>();

        // current roles by key roleUuid|programActivityId
        Map<String, UserServiceRole> currentByKey = db.getUserServiceRoles().stream()
                .collect(Collectors.toMap(
                        usr -> (usr.getRole()!=null?usr.getRole().getUuid():"null") + "|" +
                                (usr.getProgramActivity()!=null? String.valueOf(usr.getProgramActivity().getId()):"null"),
                        usr -> usr, (a,b)->a, LinkedHashMap::new));

        Set<String> seen = new HashSet<>();
        List<UserServiceRole> finalRoles = new ArrayList<>();

        for (UserServiceRole in : payloadRoles) {
            String roleUuid = (in.getRole()!=null ? in.getRole().getUuid() : null);
            Long paId = (in.getProgramActivity()!=null ? in.getProgramActivity().getId() : null);
            if (roleUuid == null || roleUuid.isBlank()) continue;

            String key = roleUuid + "|" + String.valueOf(paId);
            if (!seen.add(key)) continue;

            UserServiceRole existingUsr = currentByKey.remove(key);

            if (existingUsr == null) {
                // ----- create new USR -----
                Role role = roleRepository.findByUuid(roleUuid)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleUuid));
                ProgramActivity pa = (paId != null)
                        ? programActivityRepository.findById(paId)
                        .orElseThrow(() -> new RuntimeException("ProgramActivity not found with ID: " + paId))
                        : null;

                UserServiceRole created = new UserServiceRole();
                created.setUser(db);
                created.setRole(role);
                created.setProgramActivity(pa);
                created.setLifeCycleStatus(
                        in.getLifeCycleStatus() != null ? in.getLifeCycleStatus() : LifeCycleStatus.ACTIVE);
                created.setCreatedAt(DateUtils.getCurrentDate());
                created.setCreatedBy(db.getUpdatedBy());

                UserServiceRole savedUsr = userServiceRoleRepository.save(created);

                // groups: persist AND add to the in-memory Set
                if (in.getGroupUuids() != null) {
                    for (String gUuid : new LinkedHashSet<>(in.getGroupUuids())) {
                        if (gUuid == null || gUuid.isBlank()) continue;

                        Group grp = groupRepository.findByUuid(gUuid)
                                .orElseThrow(() -> new RuntimeException("Group not found: " + gUuid));

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
                            link.setCreatedBy(db.getUpdatedBy());
                            userServiceRoleGroupRepository.save(link);

                            // keep in-memory graph in sync
                            savedUsr.getUserServiceRoleGroups().add(link);
                        }
                    }
                }

                finalRoles.add(savedUsr);
            } else {
                // ----- update existing USR -----
                if (in.getLifeCycleStatus() != null) {
                    existingUsr.setLifeCycleStatus(in.getLifeCycleStatus());
                }
                existingUsr.setUpdatedAt(DateUtils.getCurrentDate());
                existingUsr.setUpdatedBy(db.getUpdatedBy());

                Set<String> desired = new LinkedHashSet<>(
                        Optional.ofNullable(in.getGroupUuids()).orElseGet(Collections::emptyList));

                Set<String> existingUuids = existingUsr.getUserServiceRoleGroups().stream()
                        .map(UserServiceRoleGroup::getGroup)
                        .filter(Objects::nonNull)
                        .map(Group::getUuid)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                // additions
                for (String gUuid : desired) {
                    if (existingUuids.contains(gUuid)) continue;
                    Group grp = groupRepository.findByUuid(gUuid)
                            .orElseThrow(() -> new RuntimeException("Group not found: " + gUuid));

                    UserServiceRoleGroup link = new UserServiceRoleGroup();
                    link.setUserServiceRole(existingUsr);
                    link.setGroup(grp);
                    link.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
                    link.setCreatedAt(DateUtils.getCurrentDate());
                    link.setCreatedBy(db.getUpdatedBy());
                    userServiceRoleGroupRepository.save(link);

                    // sync in-memory set
                    existingUsr.getUserServiceRoleGroups().add(link);
                }

                // removals
                for (UserServiceRoleGroup link : new ArrayList<>(existingUsr.getUserServiceRoleGroups())) {
                    String gUuid = link.getGroup() != null ? link.getGroup().getUuid() : null;
                    if (gUuid == null || !desired.contains(gUuid)) {
                        existingUsr.getUserServiceRoleGroups().remove(link);
                        userServiceRoleGroupRepository.delete(link);
                    }
                }

                finalRoles.add(existingUsr);
            }
        }

        // remove USRs absent from payload
        for (UserServiceRole stale : currentByKey.values()) {
            for (UserServiceRoleGroup link : new ArrayList<>(stale.getUserServiceRoleGroups())) {
                userServiceRoleGroupRepository.delete(link);
            }
            userServiceRoleRepository.delete(stale);
        }

        // reattach final set
        db.getUserServiceRoles().clear();
        db.getUserServiceRoles().addAll(finalRoles);

        // persist and RETURN a hydrated user (fresh graph)
        userRepository.update(db);
        return userRepository.fetchByUuidWithGraph(db.getUuid()).orElse(db);
    }


    @Transactional
    public void delete(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + uuid));

        // createdBy stores the creator's UUID (string)
        String creator = user.getUuid();

        boolean createdCohortMembers = cohortMemberRepository.existsByCreatedBy(creator);
        boolean createdPersons       = personRepository.existsByCreatedBy(creator);

        if (createdCohortMembers || createdPersons) {
            long cm = cohortMemberRepository.countByCreatedBy(creator);
            long ps = personRepository.countByCreatedBy(creator);
            throw new IllegalStateException(
                    "Não é possível apagar o utilizador: existem registos criados por ele. " +
                            "CohortMembers=" + cm + ", Pessoas=" + ps
            );
        }

        userRepository.delete(user);
    }


    @Transactional
    public User updateLifeCycleStatus(String uuid, LifeCycleStatus status) {
        Optional<User> existing = userRepository.findByUuidFetchRolesAndGroups(uuid);
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

    public void updatePassword(String uuid, String newPassword, String updatedByUuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        user.setPassword(Utilities.encryptPassword(newPassword, user.getSalt()));
        user.setUpdatedBy(updatedByUuid);
        user.setUpdatedAt(DateUtils.getCurrentDate());

        userRepository.update(user);
    }

    /**
     * Importação em lote:
     * - valida obrigatórios e duplicados (ficheiro + BD)
     * - integratedSystem: aceita code/description de SourceSystem ativo
     * - grava Person.names e User.attributes conforme padrão atual
     * - gera password temporária e marca shouldResetPassword=true
     */
    @Transactional
    public ImportResultDTO importUsers(List<ImportUserRowDTO> rows, String actorUuid) {
        if (rows == null || rows.isEmpty()) return new ImportResultDTO(0,0,List.of());

        // 1) Mapear SourceSystems ativos por token (code/description)
        Map<String, SourceSystem> sysMap = new HashMap<>();
        for (SourceSystem ss : sourceSystemRepository.findAll()) {
            boolean active = ss.getLifeCycleStatus() == null
                    || !LifeCycleStatus.INACTIVE.equals(ss.getLifeCycleStatus());
            if (!active) continue;
            if (StringUtils.isNotBlank(ss.getCode()))        sysMap.put(lower(ss.getCode()), ss);
            if (StringUtils.isNotBlank(ss.getDescription())) sysMap.put(lower(ss.getDescription()), ss);
        }

        // 2) Validações base + duplicados no ficheiro
        List<ImportErrorItemDTO> errors = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i=0;i<rows.size();i++){
            var r = rows.get(i);
            List<String> msgs = new ArrayList<>();
            if (StringUtils.isBlank(r.getName()))     msgs.add("Nome é obrigatório");
            if (StringUtils.isBlank(r.getSurname()))  msgs.add("Apelido é obrigatório");
            if (StringUtils.isBlank(r.getUsername())) msgs.add("Username é obrigatório");
            String u = lower(r.getUsername());
            if (StringUtils.isNotBlank(u) && !seen.add(u)) msgs.add("Username duplicado no ficheiro");

            String sys = norm(r.getIntegratedSystem());
            if (StringUtils.isNotBlank(sys)) {
                if (!sysMap.containsKey(lower(sys)))
                    msgs.add("Sistema Integrado inválido: \"" + r.getIntegratedSystem() + "\"");
                if (StringUtils.isBlank(r.getIdOnIntegratedSystem()))
                    msgs.add("ID no Sistema é obrigatório quando Sistema Integrado é informado");
            }
            if (!msgs.isEmpty()) errors.add(new ImportErrorItemDTO(i, r.getUsername(), msgs));
        }

        // 3) Duplicados na BD
        Set<String> toCheck = rows.stream()
                .map(ImportUserRowDTO::getUsername)
                .filter(StringUtils::isNotBlank)
                .map(UserService::lower).collect(Collectors.toSet());
        if (!toCheck.isEmpty()) {
            Set<String> exists = new HashSet<>(userRepository.findExistingUsernamesLower(toCheck));
            for (int i=0;i<rows.size();i++){
                if (exists.contains(lower(rows.get(i).getUsername()))) {
                    int finalI = i;
                    Optional<ImportErrorItemDTO> item = errors.stream().filter(e -> e.getIndex()== finalI).findFirst();
                    if (item.isPresent()) item.get().getMessages().add("Username já existente no sistema");
                    else errors.add(new ImportErrorItemDTO(i, rows.get(i).getUsername(), List.of("Username já existente no sistema")));
                }
            }
        }

        // 4) Filtrar válidos
        Set<Integer> bad = errors.stream().map(ImportErrorItemDTO::getIndex).collect(Collectors.toSet());
        List<ImportUserRowDTO> valids = new ArrayList<>();
        for (int i=0;i<rows.size();i++) if (!bad.contains(i)) valids.add(rows.get(i));
        if (valids.isEmpty()) return new ImportResultDTO(0, rows.size(), errors);

        // 5) Persistir válidos
        int imported = 0;
        for (var r : valids) {
            // display do sistema integrado
            String sysToken = lower(r.getIntegratedSystem());
            String systemName = null;
            if (StringUtils.isNotBlank(sysToken)) {
                SourceSystem ss = sysMap.get(sysToken);
                systemName = (ss != null)
                        ? (StringUtils.defaultIfBlank(ss.getDescription(), ss.getCode()))
                        : norm(r.getIntegratedSystem());
            }

            User u = new User();
            // Person.* (JSON strings)
            u.setNames(buildNamesJson(r.getName(), r.getSurname()));
            u.setAddress("[]");
            u.setPersonAttributes("[]");

            // User básicos
            u.setUsername(norm(r.getUsername()));
            u.setStatus(LifeCycleStatus.ACTIVE.name());
            u.setLifeCycleStatus(LifeCycleStatus.ACTIVE);

            // password temporária + reset
            String temp = UUID.randomUUID().toString().replace("-", "").substring(0,10);
            String salt = Utilities.generateSalt();
            u.setSalt(salt);
            u.setPassword(Utilities.encryptPassword(temp, salt));
            u.setShouldResetPassword(true);

            // attributes JSONB (integratedSystemName + idOnIntegratedSystem)
            List<Map<String,Object>> attrs = new ArrayList<>();
            if (StringUtils.isNotBlank(systemName)) {
                var node = new LinkedHashMap<String,Object>();
                node.put("type", "integratedSystem");
                node.put("idOnIntegratedSystem", norm(r.getIdOnIntegratedSystem()));
                node.put("integratedSystemName", systemName);
                attrs.add(node);
            }
            u.setAttributesAsMap(attrs);

            // auditoria
            u.setCreatedAt(DateUtils.getCurrentDate());
            u.setCreatedBy(actorUuid);

            userRepository.save(u);
            imported++;
        }

        return new ImportResultDTO(imported, rows.size() - imported, errors);
    }

    @Transactional
    public ValidateImportResultDTO validateImport(List<ImportUserRowDTO> rows) {
        if (rows == null || rows.isEmpty())
            return new ValidateImportResultDTO(List.of());

        // map de SourceSystems ativos por code/description (lowercase)
        Map<String, SourceSystem> sysMap = new HashMap<>();
        for (SourceSystem ss : sourceSystemRepository.findAll()) {
            boolean active = ss.getLifeCycleStatus() == null
                    || !LifeCycleStatus.INACTIVE.equals(ss.getLifeCycleStatus());
            if (!active) continue;
            if (StringUtils.isNotBlank(ss.getCode()))        sysMap.put(ss.getCode().trim().toLowerCase(), ss);
            if (StringUtils.isNotBlank(ss.getDescription())) sysMap.put(ss.getDescription().trim().toLowerCase(), ss);
        }

        List<ImportErrorItemDTO> errors = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // validações base + duplicado no ficheiro
        for (int i = 0; i < rows.size(); i++) {
            final int idx = i;
            var r = rows.get(i);
            List<String> msgs = new ArrayList<>();

            //if (StringUtils.isBlank(r.getName()))     msgs.add("Nome é obrigatório");
            //if (StringUtils.isBlank(r.getSurname()))  msgs.add("Apelido é obrigatório");
            //if (StringUtils.isBlank(r.getUsername())) msgs.add("Username é obrigatório");

            //String uname = StringUtils.trimToEmpty(r.getUsername()).toLowerCase();
            //if (!uname.isEmpty() && !seen.add(uname)) msgs.add("Username duplicado no ficheiro");

            //String sys = StringUtils.trimToEmpty(r.getIntegratedSystem());
            /*if (!sys.isEmpty()) {
                if (!sysMap.containsKey(sys.toLowerCase()))
                    msgs.add("Sistema Integrado inválido: \"" + r.getIntegratedSystem() + "\"");
                if (StringUtils.isBlank(r.getIdOnIntegratedSystem()))
                    msgs.add("ID no Sistema é obrigatório quando Sistema Integrado é informado");
            }*/

            if (!msgs.isEmpty())
                errors.add(new ImportErrorItemDTO(idx, r.getUsername(), msgs));
        }

        // duplicados na BD
        Set<String> toCheck = rows.stream()
                .map(ImportUserRowDTO::getUsername)
                .filter(StringUtils::isNotBlank)
                .map(s -> s.trim().toLowerCase())
                .collect(java.util.stream.Collectors.toSet());

        if (!toCheck.isEmpty()) {
            Set<String> exists = new HashSet<>(userRepository.findExistingUsernamesLower(toCheck));
            for (int i = 0; i < rows.size(); i++) {
                final int idx = i;
                String uname = StringUtils.trimToEmpty(rows.get(i).getUsername()).toLowerCase();
                if (exists.contains(uname)) {
                    Optional<ImportErrorItemDTO> item = errors.stream().filter(e -> e.getIndex() == idx).findFirst();
                    if (item.isPresent()) item.get().getMessages().add("Username já existente no sistema");
                    else errors.add(new ImportErrorItemDTO(idx, rows.get(i).getUsername(),
                            List.of("Username já existente no sistema")));
                }
            }
        }

        return new ValidateImportResultDTO(errors);
    }
}
