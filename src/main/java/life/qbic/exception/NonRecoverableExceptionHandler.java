package life.qbic.exception;

import static org.slf4j.LoggerFactory.getLogger;

import io.micronaut.context.MessageSource;
import io.micronaut.context.MessageSource.MessageContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@Produces
@Singleton
@Requires(classes = {NonRecoverableException.class, ExceptionHandler.class})
public class NonRecoverableExceptionHandler implements ExceptionHandler<NonRecoverableException, HttpResponse<?>>{

  private final MessageSource messageSource;
  private final ErrorResponseProcessor<?> errorResponseProcessor;
  private static final Logger log = getLogger(NonRecoverableExceptionHandler.class);

  private final String defaultMessage;
  private static final Locale LOCALE_DEFAULT = Locale.US;

  @Inject
  protected NonRecoverableExceptionHandler(MessageSource messageSource,
      ErrorResponseProcessor<?> errorResponseProcessor) {
    this.messageSource = messageSource;
    this.errorResponseProcessor = errorResponseProcessor;
    this.defaultMessage = messageSource.getMessage("GENERAL",
        MessageContext.of(LOCALE_DEFAULT)).orElse("");
  }

  @Override
  public HttpResponse<?> handle(HttpRequest request, NonRecoverableException nonRecoverableException) {
    log.error(nonRecoverableException.getMessage(), nonRecoverableException);
    String errorMessage = getMessage(nonRecoverableException);
    MutableHttpResponse<Object> errorResponse = getBaseResponse(nonRecoverableException);
    return errorResponseProcessor.processResponse(ErrorContext.builder(request)
        .cause(nonRecoverableException)
        .errorMessage(errorMessage)
        .build(), errorResponse);
  }

  private static MutableHttpResponse<Object> getBaseResponse(
      NonRecoverableException nonRecoverableException) {
    if (nonRecoverableException.errorCode().equals(ErrorCode.BAD_SAMPLE_CODE)) {
      return HttpResponse.badRequest();
    }
    if (nonRecoverableException.errorCode().equals(ErrorCode.BAD_SAMPLE_STATUS)) {
      return HttpResponse.badRequest();
    }
    if (nonRecoverableException.errorCode().equals(ErrorCode.BAD_USER)) {
      return HttpResponse.badRequest();
    }

    return HttpResponse.serverError();
  }

  private String getMessage(NonRecoverableException nonRecoverableException) {
    return messageSource.getMessage(nonRecoverableException.errorCode().name(),
        MessageContext.of(LOCALE_DEFAULT, nonRecoverableException.errorParameters().asMap())).orElse(defaultMessage);
  }
}
