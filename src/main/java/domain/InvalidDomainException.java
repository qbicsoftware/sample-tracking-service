package domain;

/**
 * <p>Thrown when domain policies could not be validated and would lead to an invalid domain state.</p>
 */
public class InvalidDomainException extends RuntimeException {

  public InvalidDomainException() {
  }

  public InvalidDomainException(String message) {
    super(message);
  }

  public InvalidDomainException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDomainException(Throwable cause) {
    super(cause);
  }

  public InvalidDomainException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
