package life.qbic.auth

import groovy.util.logging.Log4j2
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject
import javax.inject.Singleton

@Log4j2
@Requires(beans = ApplicationContext.class)
@Singleton
class Authentication implements AuthenticationProvider{

    private final Map config

    @Inject
    private final ApplicationContext context

    Authentication() {
        String configPath = this.context.getEnvironment().getProperty("userroles.config", String.class) ?: ""
        println "init"
        if (configPath.isEmpty()) {
            throw new IllegalStateException("The user role configuration path must be set in the application configuration file.")
        } else {
            this.config = new Yaml().load(new File(configPath).text)
        }
    }

    @Override
    Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        if (!this.config) {
            println("test")
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
}
