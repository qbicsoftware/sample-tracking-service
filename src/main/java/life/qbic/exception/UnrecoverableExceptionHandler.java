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
 * Handles unrecoverable exceptions globally. Returns an appropriate HttpResponse.
 * @since 2.0.0
 */
@Produces
@Singleton
@Requires(classes = {UnrecoverableException.class, ExceptionHandler.class})
public class UnrecoverableExceptionHandler implements ExceptionHandler<UnrecoverableException, HttpResponse<?>>{

  private final MessageSource messageSource;
  private final ErrorResponseProcessor<?> errorResponseProcessor;
  private static final Logger log = getLogger(UnrecoverableExceptionHandler.class);

  private final String defaultMessage;
  private static final Locale LOCALE_DEFAULT = Locale.US;

  @Inject
  protected UnrecoverableExceptionHandler(MessageSource messageSource,
      ErrorResponseProcessor<?> errorResponseProcessor) {
    this.messageSource = messageSource;
    this.errorResponseProcessor = errorResponseProcessor;
    this.defaultMessage = messageSource.getMessage("GENERAL",
        MessageContext.of(LOCALE_DEFAULT)).orElse("");
  }

  @Override
  public HttpResponse<?> handle(HttpRequest request, UnrecoverableException unRecoverableException) {
    log.error(unRecoverableException.getMessage(), unRecoverableException);
    String errorMessage = getMessage(unRecoverableException);
    MutableHttpResponse<Object> errorResponse = getBaseResponse(unRecoverableException);
    return errorResponseProcessor.processResponse(ErrorContext.builder(request)
        .cause(unRecoverableException)
        .errorMessage(errorMessage)
        .build(), errorResponse);
  }

  private static MutableHttpResponse<Object> getBaseResponse(
      UnrecoverableException unRecoverableException) {
    if (unRecoverableException.errorCode().equals(ErrorCode.BAD_SAMPLE_CODE)) {
      return HttpResponse.badRequest();
    }
    if (unRecoverableException.errorCode().equals(ErrorCode.BAD_SAMPLE_STATUS)) {
      return HttpResponse.badRequest();
    }
    if (unRecoverableException.errorCode().equals(ErrorCode.BAD_USER)) {
      return HttpResponse.badRequest();
    }
    if (unRecoverableException.errorCode().equals(ErrorCode.SAMPLE_NOT_FOUND)) {
      return HttpResponse.notFound();
    }

    return HttpResponse.serverError();
  }

  private String getMessage(UnrecoverableException unRecoverableException) {
    return messageSource.getMessage(unRecoverableException.errorCode().name(),
        MessageContext.of(LOCALE_DEFAULT, unRecoverableException.errorParameters().asMap())).orElse(defaultMessage);
  }
}
