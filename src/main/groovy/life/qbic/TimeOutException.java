package life.qbic;

/**
 * Timeout Exception
 * <p>
 * Is thrown when connections to a data source cannot be established.
 *
 * @since 2.2.3
 */
public class TimeOutException extends RuntimeException {

  public TimeOutException() {
  }

  public TimeOutException(String message) {
    super(message);
  }

  public TimeOutException(String message, Throwable cause) {
    super(message, cause);
  }

}
