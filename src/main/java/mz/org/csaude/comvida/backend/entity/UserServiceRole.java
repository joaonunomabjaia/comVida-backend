package mz.org.csaude.comvida.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.org.csaude.comvida.backend.base.BaseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Serdeable.Deserializable
@Table(name = "user_service_roles")
public class UserServiceRole extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "program_activity_id")
    private ProgramActivity programActivity;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    /** Vínculos materializados: USR ⇄ Group */
    @OneToMany(
            mappedBy = "userServiceRole",
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @org.hibernate.annotations.BatchSize(size = 50)
    @OrderBy("id ASC")
    private Set<UserServiceRoleGroup> userServiceRoleGroups = new LinkedHashSet<>();

    /** Conveniência: retorna os Groups como Set */
    @Transient
    public Set<Group> getGroups() {
        if (userServiceRoleGroups == null) return Set.of();
        return userServiceRoleGroups.stream()
                .map(UserServiceRoleGroup::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** ===== Helpers DTO-friendly: groupUuids ===== */

    @Transient
    @JsonIgnore
    public List<String> getGroupUuids() {
        if (userServiceRoleGroups == null) return List.of();
        return userServiceRoleGroups.stream()
                .map(UserServiceRoleGroup::getGroup)
                .filter(Objects::nonNull)
                .map(Group::getUuid)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Substitui os vínculos de grupos pelo conjunto informado de UUIDs.
     * Usa cascade+orphanRemoval para persistir/remover links automaticamente.
     */
    @Transient
    @JsonIgnore
    public void setGroupUuids(List<String> uuids) {
        final List<String> desired = (uuids == null ? List.<String>of() : uuids).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // remove links que não estão mais presentes
        userServiceRoleGroups.removeIf(link -> {
            Group g = link.getGroup();
            String id = (g != null ? g.getUuid() : null);
            return id == null || !desired.contains(id);
        });

        // uuids já existentes
        Set<String> current = userServiceRoleGroups.stream()
                .map(UserServiceRoleGroup::getGroup)
                .filter(Objects::nonNull)
                .map(Group::getUuid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // adiciona novos links
        for (String uuid : desired) {
            if (current.contains(uuid)) continue;
            Group g = new Group();
            g.setUuid(uuid);

            UserServiceRoleGroup link = new UserServiceRoleGroup();
            link.setUserServiceRole(this); // backref
            link.setGroup(g);

            userServiceRoleGroups.add(link);
        }
    }

    @Creator
    public UserServiceRole() {}
}
