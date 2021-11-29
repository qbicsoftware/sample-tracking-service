package life.qbic.auth

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.yaml.snakeyaml.Yaml

import javax.annotation.PostConstruct
import javax.inject.Singleton

/**
 * <b>Class Authentication</b>
 *
 * <p>Authenticates a user and authorizes the user role in this sample-tracking service context.</p>
 *
 * <p>The roles and user tokens must be provided in a file following the YAML format specification.</p>
 * An exemplary entry looks like this:
 *
 * <pre>
 *     ---
 *        servicereader:
 *          token: 123!
 *          roles:
 *           - READER
 *        servicewriter:
 *          token: 123456!
 *          roles:
 *            - READER
 *            - WRITER
 *    ...
 * </pre>
 * @since 1.2.1
 */
@Log4j2
@Singleton
class Authentication implements AuthenticationProvider{

    @Value('${userroles.config}')
    String configPath

    private Map config

    /**
     * <p>Executed after the bean has been instantiated by Micronaut.</p>
     *
     * <p>Loads the user token and roles from the YAML file specified in the {@link Authentication#configPath} field.</p>
     */
    @PostConstruct
    void initialize() {
        if (configPath.isEmpty()) {
            throw new IllegalStateException("The user role configuration path must be set in the application configuration file.")
        } else {
            this.config = new Yaml().load(new File(configPath).text)
        }
    }

    private UserDetails tryToAuthenticate(AuthenticationRequest request) {
        def user = (String) request.identity
        def secret = (String) request.secret
        if ( isRegisteredUser(user) && secretMatchesUser(secret, user) ) {
            new UserDetails(user, getRolesForUser(user))
        } else {
            throw new AuthenticationException("Authentication failed.")
        }
    }

    private boolean isRegisteredUser(String user) {
        this.config.get(user)
    }

    private boolean secretMatchesUser(String secret, String user) {
        this.config.get(user).get('token') == secret
    }

    private List<String> getRolesForUser(String user){
        (List) this.config.get(user).get('roles')
    }

    @Override
    Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        if (!this.config) {
            return Flowable.just(new AuthenticationFailed())
        }
        try {
            UserDetails userDetails = tryToAuthenticate(authenticationRequest)
            log.info("Sucessfull authentication by user '${authenticationRequest.identity}'.")
            return Flowable.just(userDetails)
        } catch (AuthenticationException e) {
            log.warn("Unauthorized access!")
            return Flowable.just(new AuthenticationFailed())
        }
    }
}
