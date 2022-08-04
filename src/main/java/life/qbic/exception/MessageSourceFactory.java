package life.qbic.exception;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import java.util.Locale;
import javax.inject.Singleton;

/**
 * This factory provides a concrete implementation of micronaut MessageSource
 * @since 2.0.0
 */
@Factory
public class MessageSourceFactory {

  @Singleton
  public MessageSource createMessageSource() {
    return new ResourceBundleMessageSource("i18n.messages", Locale.US);
  }
}
