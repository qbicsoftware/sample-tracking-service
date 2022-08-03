package life.qbic.exception;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import java.util.Locale;
import javax.inject.Singleton;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@Factory
public class MessageSourceFactory {

  @Singleton
  public MessageSource createMessageSource() {
    return new ResourceBundleMessageSource("i18n.messages", Locale.US);
  }
}
