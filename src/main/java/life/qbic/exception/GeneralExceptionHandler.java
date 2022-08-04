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
 * Handles all exceptions not handled by other handlers. Provides a customizable general error
 * message to the client.
 *
 * @since 2.0.0
 */
@Produces
@Singleton
@Requires(classes = {Exception.class, io.micronaut.http.server.exceptions.ExceptionHandler.class})
public class GeneralExceptionHandler implements ExceptionHandler<Exception, HttpResponse<?>> {

  private final MessageSource messageSource;
  private final ErrorResponseProcessor<?> errorResponseProcessor;
  private static final Logger log = getLogger(GeneralExceptionHandler.class);


  private final String defaultMessage;
  private static final Locale LOCALE_DEFAULT = Locale.US;

  @Inject
  protected GeneralExceptionHandler(MessageSource messageSource,
      ErrorResponseProcessor<?> errorResponseProcessor) {
    this.messageSource = messageSource;
    this.errorResponseProcessor = errorResponseProcessor;
    this.defaultMessage = messageSource.getMessage("GENERAL",
        MessageContext.of(LOCALE_DEFAULT)).orElse("");
  }

  @Override
  public HttpResponse<?> handle(HttpRequest request, Exception e) {
    log.error(e.getMessage(), e);
    String errorMessage = messageSource.getMessage("GENERAL",
        MessageContext.of(LOCALE_DEFAULT)).orElse(defaultMessage);
    MutableHttpResponse<Object> errorResponse = HttpResponse.serverError();
    return errorResponseProcessor.processResponse(ErrorContext.builder(request)
        .cause(e)
        .errorMessage(errorMessage)
        .build(), errorResponse);
  }
}
