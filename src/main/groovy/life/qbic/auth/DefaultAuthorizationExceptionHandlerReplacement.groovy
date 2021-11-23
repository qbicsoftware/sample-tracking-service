package life.qbic.auth

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.security.authentication.AuthorizationException
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler

import static io.micronaut.http.HttpHeaders.WWW_AUTHENTICATE
import static io.micronaut.http.HttpStatus.FORBIDDEN
import static io.micronaut.http.HttpStatus.UNAUTHORIZED

/**
 * <b>Introduces proper authentication header</b>
 *
 * <p>Following the HTTP 1.1 specification, we MUST provide a
 * WWW-Authenticate header information  in case we generate a http response
 * with status code 401:</p>
 * <a href="https://datatracker.ietf.org/doc/html/rfc7235#section-4.1">https://datatracker.ietf.org/doc/html/rfc7235#section-4.1</a>
 *
 * @since 1.2.1
 */
@Replaces(DefaultAuthorizationExceptionHandler.class)
class DefaultAuthorizationExceptionHandlerReplacement extends DefaultAuthorizationExceptionHandler {

    @Override
    protected MutableHttpResponse<?> httpResponseWithStatus(HttpRequest request, AuthorizationException e) {
        if (e.isForbidden()) {
            return HttpResponse.status(FORBIDDEN);
        }
        return HttpResponse.status(UNAUTHORIZED)
                .header(WWW_AUTHENTICATE, "Basic realm=\"QBiC Services\"");
    }
}
