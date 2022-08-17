package life.qbic.exception;

/**
 * <p>An unspecific exception from the application.</p>
 *
 * @since 2.0.0
 */
public class UnrecoverableException extends RuntimeException {

  private final ErrorCode errorCode;
  private final ErrorParameters errorParameters;



  public UnrecoverableException(ErrorCode errorCode, ErrorParameters errorParameters) {
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public UnrecoverableException(String message, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public UnrecoverableException(String message, Throwable cause) {
    this(message, cause, ErrorCode.GENERAL, ErrorParameters.create());
  }

  public UnrecoverableException(String message, Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message, cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public UnrecoverableException(Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }


  public UnrecoverableException(Throwable cause) {
    this(cause, ErrorCode.GENERAL);
  }

  public UnrecoverableException(String message) {
    this(message, ErrorCode.GENERAL, new ErrorParameters());
  }

  public UnrecoverableException(Throwable cause, ErrorCode errorCode) {
    this(cause, errorCode, new ErrorParameters());
  }
  public UnrecoverableException(String message, Throwable cause, ErrorCode errorCode) {
    this(message, cause, errorCode, new ErrorParameters());
  }

  public UnrecoverableException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, ErrorCode errorCode, ErrorParameters errorParameters) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public ErrorCode errorCode() {
    return errorCode;
  }

  public ErrorParameters errorParameters() {
    return errorParameters;
  }
}
