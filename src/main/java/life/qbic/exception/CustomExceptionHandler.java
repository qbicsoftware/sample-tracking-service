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
@Requires(classes = {CustomException.class, ExceptionHandler.class})
public class CustomExceptionHandler implements ExceptionHandler<CustomException, HttpResponse>{

  private final MessageSource messageSource;
  private final ErrorResponseProcessor<?> errorResponseProcessor;
  private static final Logger log = getLogger(CustomExceptionHandler.class);

  private final String defaultMessage;
  private static final Locale LOCALE_DEFAULT = Locale.US;

  @Inject
  protected CustomExceptionHandler(MessageSource messageSource,
      ErrorResponseProcessor<?> errorResponseProcessor) {
    this.messageSource = messageSource;
    this.errorResponseProcessor = errorResponseProcessor;
    this.defaultMessage = messageSource.getMessage("GENERAL",
        MessageContext.of(LOCALE_DEFAULT)).orElse("");
  }

  @Override
  public HttpResponse handle(HttpRequest request, CustomException customException) {
    log.error(customException.getMessage(), customException);
    String errorMessage = getMessage(customException);
    MutableHttpResponse<Object> errorResponse = getBaseResponse(customException);
    return errorResponseProcessor.processResponse(ErrorContext.builder(request)
        .cause(customException)
        .errorMessage(errorMessage)
        .build(), errorResponse);
  }

  private static MutableHttpResponse<Object> getBaseResponse(CustomException customException) {
    if (customException.errorCode().equals(ErrorCode.BAD_SAMPLE_CODE)) {
      return HttpResponse.badRequest();
    }
    if (customException.errorCode().equals(ErrorCode.BAD_SAMPLE_STATUS)) {
      return HttpResponse.badRequest();
    }
    if (customException.errorCode().equals(ErrorCode.BAD_USER)) {
      return HttpResponse.badRequest();
    }

    return HttpResponse.serverError();
  }

  private String getMessage(CustomException customException) {
    return messageSource.getMessage(customException.errorCode().name(),
        MessageContext.of(LOCALE_DEFAULT, customException.errorParameters().asMap())).orElse(defaultMessage);
  }
}
