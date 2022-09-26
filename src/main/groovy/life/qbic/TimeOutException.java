package life.qbic;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public class TimeOutException extends RuntimeException {

  public TimeOutException() {}

  public TimeOutException(String message) {
    super(message);
  }

  public TimeOutException(String message, Throwable cause) {
    super(message, cause);
  }

}
