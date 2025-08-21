// ComVidaAuthenticationProvider.java
package mz.org.csaude.comvida.backend.auth;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.order.Ordered;
import io.micronaut.security.authentication.*;
import io.micronaut.security.authentication.provider.ReactiveAuthenticationProvider;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.*;
import mz.org.csaude.comvida.backend.service.UserService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class ComVidaAuthenticationProvider implements ReactiveAuthenticationProvider, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(ComVidaAuthenticationProvider.class);

    private final UserService userService;

    public ComVidaAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public @NonNull Publisher<AuthenticationResponse> authenticate(
            Object requestContext,
            @NonNull AuthenticationRequest authenticationRequest) {

        final String identity = (String) authenticationRequest.getIdentity();
        final String secret   = (String) authenticationRequest.getSecret();

        LOG.debug("User '{}' is attempting to authenticate...", identity);

        return Flowable.create(emitter -> {
            // 1) Load full graph (roles + groups) to avoid lazy issues
            Optional<User> possibleUser = userService.getGraphByUserName(identity);

            if (possibleUser.isEmpty()) {
                LOG.warn("No user found for username '{}'", identity);
                emitter.onError(new AuthenticationException(new AuthenticationFailed("Utilizador ou senha inválida!")));
                return;
            }

            User user = possibleUser.get();

            if (!user.isActive()) {
                LOG.warn("User '{}' is inactive", identity);
                emitter.onError(new AuthenticationException(new AuthenticationFailed("O utilizador encontra-se inactivo!")));
                return;
            }

            // 2) verify password
            String encryptedInputPassword = Utilities.encryptPassword(secret, user.getSalt());
            if (!encryptedInputPassword.trim().equals(String.valueOf(user.getPassword()).trim())) {
                LOG.warn("Password mismatch for user '{}'", identity);
                emitter.onError(new AuthenticationException(new AuthenticationFailed("Utilizador ou senha inválida!")));
                return;
            }

            // 3) build authorities and attributes
            List<String> authorities = extractRoleNames(user);     // e.g. ["ADMIN", "NURSE"]
            Map<String, Object> attributes = buildAuthAttributes(user); // includes roles+groups detail

            LOG.info("User '{}' authenticated successfully with roles {}", identity, authorities);
            emitter.onNext(AuthenticationResponse.success(identity, authorities, attributes));
            emitter.onComplete();
        }, BackpressureStrategy.ERROR);
    }

    /** Flat list of role names for Micronaut's 'roles' */
    private List<String> extractRoleNames(User user) {
        Set<String> set = new LinkedHashSet<>();
        if (user.getUserServiceRoles() != null) {
            for (UserServiceRole usr : user.getUserServiceRoles()) {
                Role r = usr.getRole();
                if (r != null && r.getName() != null && !r.getName().isBlank()) {
                    set.add(r.getName());
                }
            }
        }
        return new ArrayList<>(set);
    }

    /** Rich payload with roles + groups so the UI/API can filter by scope */
    private Map<String, Object> buildAuthAttributes(User user) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("userId",    user.getId());
        attrs.put("userUuid",  user.getUuid());
        attrs.put("userName",  user.getUsername());
        attrs.put("userNames", user.getNames());

        // --------- serviceRoles (novo) ----------
        List<Map<String, Object>> serviceRoles = new ArrayList<>();

        // Mantém o que já existia
        List<Map<String, Object>> grants = new ArrayList<>();

        if (user.getUserServiceRoles() != null) {
            for (UserServiceRole usr : user.getUserServiceRoles()) {

                // ======== Bloco serviceRole ========
                Map<String, Object> serviceRole = new LinkedHashMap<>();
                serviceRole.put("uuid", usr.getUuid());

                // role
                Role role = usr.getRole();
                if (role != null) {
                    Map<String, Object> roleMap = new LinkedHashMap<>();
                    roleMap.put("uuid", role.getUuid());
                    roleMap.put("name", role.getName());
                    serviceRole.put("role", roleMap);
                } else {
                    serviceRole.put("role", null);
                }

                // programActivity (com program aninhado)
                ProgramActivity pa = usr.getProgramActivity();
                if (pa != null) {
                    Map<String, Object> paMap = new LinkedHashMap<>();
                    paMap.put("id",   pa.getId());
                    paMap.put("uuid", pa.getUuid());
                    paMap.put("name", pa.getName());

                    if (pa.getProgram() != null) {
                        Program p = pa.getProgram();
                        Map<String, Object> pMap = new LinkedHashMap<>();
                        pMap.put("id",   p.getId());
                        pMap.put("uuid", p.getUuid());
                        pMap.put("name", p.getName());
                        paMap.put("program", pMap);
                    }

                    serviceRole.put("programActivity", paMap);
                } else {
                    serviceRole.put("programActivity", null);
                }

                // userServiceRoleGroups (preferindo a associação explícita; cai para groups se necessário)
                List<Map<String, Object>> groupDTOs = new ArrayList<>();

                // Se você tem a entidade de junção UserServiceRoleGroup
                Set<UserServiceRoleGroup> usrgSet = usr.getUserServiceRoleGroups();
                if (usrgSet != null && !usrgSet.isEmpty()) {
                    for (UserServiceRoleGroup usrg : usrgSet) {
                        if (usrg == null) continue;
                        Group g = usrg.getGroup();
                        if (g == null) continue;

                        Map<String, Object> gmap = new LinkedHashMap<>();
                        gmap.put("userServiceRoleGroupUuid", usrg.getUuid());
                        gmap.put("uuid", g.getUuid());
                        gmap.put("name", g.getName());
                        if (g.getProgramActivity() != null) {
                            gmap.put("programActivityId",   g.getProgramActivity().getId());
                            gmap.put("programActivityUuid", g.getProgramActivity().getUuid());
                        }
                        groupDTOs.add(gmap);
                    }
                } else {
                    // fallback: se só existir usr.getGroups()
                    Set<Group> grps = usr.getGroups();
                    if (grps != null) {
                        for (Group g : grps) {
                            if (g == null) continue;
                            Map<String, Object> gmap = new LinkedHashMap<>();
                            gmap.put("uuid", g.getUuid());
                            gmap.put("name", g.getName());
                            if (g.getProgramActivity() != null) {
                                gmap.put("programActivityId",   g.getProgramActivity().getId());
                                gmap.put("programActivityUuid", g.getProgramActivity().getUuid());
                            }
                            groupDTOs.add(gmap);
                        }
                    }
                }

                serviceRole.put("userServiceRoleGroups", groupDTOs);

                serviceRoles.add(serviceRole);

                // ======== (Opcional) manter "grants" legado ========
                Map<String, Object> grant = new LinkedHashMap<>();
                grant.put("roleUuid", role != null ? role.getUuid() : null);
                grant.put("roleName", role != null ? role.getName() : null);

                if (pa != null) {
                    grant.put("programActivityId",   pa.getId());
                    grant.put("programActivityUuid", pa.getUuid());
                    grant.put("programActivityName", pa.getName());
                    if (pa.getProgram() != null) {
                        grant.put("programId",   pa.getProgram().getId());
                        grant.put("programUuid", pa.getProgram().getUuid());
                        grant.put("programName", pa.getProgram().getName());
                    }
                } else {
                    grant.put("programActivityId", null);
                    grant.put("programId", null);
                }

                // groups do grant (podes remover se não precisa duplicar)
                grant.put("groups", groupDTOs);

                grants.add(grant);
            }
        }

        // adiciona aos atributos
        attrs.put("serviceRoles", serviceRoles); // << NOVO
        attrs.put("grants", grants);             // (legado, opcional)

        return attrs;
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
