package mz.org.csaude.comvida.backend.auth;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.*;
import io.micronaut.security.authentication.provider.ReactiveAuthenticationProvider;
import io.micronaut.core.order.Ordered;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.entity.UserGroupRole;
import mz.org.csaude.comvida.backend.service.UserService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.*;

@Singleton
public class ComVidaAuthenticationProvider implements ReactiveAuthenticationProvider, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(ComVidaAuthenticationProvider.class);

    private final UserService userService;

    public ComVidaAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public @NonNull Publisher<AuthenticationResponse> authenticate(Object requestContext, @NonNull AuthenticationRequest authenticationRequest) {
        final String identity = (String) authenticationRequest.getIdentity();
        final String secret = (String) authenticationRequest.getSecret();

        LOG.debug("User '{}' is attempting to authenticate...", identity);

        return Flowable.create(emitter -> {
            Optional<User> possibleUser = userService.getByUserName(identity);

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

            String encryptedInputPassword = Utilities.encryptPassword(secret, user.getSalt());

            if (!encryptedInputPassword.trim().equals(user.getPassword().trim())) {
                LOG.warn("Password mismatch for user '{}'", identity);
                emitter.onError(new AuthenticationException(new AuthenticationFailed("Utilizador ou senha inválida!")));
                return;
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userId", user.getId());
            attributes.put("userUuid", user.getUuid());

            List<String> roles = extractRoles(user);

            LOG.info("User '{}' authenticated successfully with roles {}", identity, roles);
            emitter.onNext(AuthenticationResponse.success(identity, roles, attributes));
            emitter.onComplete();

        }, BackpressureStrategy.ERROR);
    }



    private List<String> extractRoles(User user) {
        List<String> roles = new ArrayList<>();
        if (user.getUserGroupRoles() != null) {
            for (UserGroupRole ugr : user.getUserGroupRoles()) {
                if (ugr.getRole() != null && ugr.getRole().getName() != null) {
                    roles.add(ugr.getRole().getName());
                }
            }
        }
        return roles;
    }

    @Override
    public int getOrder() {
        return 0; // prioridade padrão
    }

}
