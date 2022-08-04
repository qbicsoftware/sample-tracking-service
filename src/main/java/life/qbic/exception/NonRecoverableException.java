package life.qbic.exception;

/**
 * <p>An unspecific exception from the application.</p>
 *
 * @since 2.0.0
 */
public class NonRecoverableException extends RuntimeException {

  private final ErrorCode errorCode;
  private final ErrorParameters errorParameters;



  public NonRecoverableException(ErrorCode errorCode, ErrorParameters errorParameters) {
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public NonRecoverableException(String message, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public NonRecoverableException(String message, Throwable cause) {
    this(message, cause, ErrorCode.GENERAL, ErrorParameters.create());
  }

  public NonRecoverableException(String message, Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message, cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public NonRecoverableException(Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }


  public NonRecoverableException(Throwable cause) {
    this(cause, ErrorCode.GENERAL);
  }

  public NonRecoverableException(String message) {
    this(message, ErrorCode.GENERAL, new ErrorParameters());
  }

  public NonRecoverableException(Throwable cause, ErrorCode errorCode) {
    this(cause, errorCode, new ErrorParameters());
  }
  public NonRecoverableException(String message, Throwable cause, ErrorCode errorCode) {
    this(message, cause, errorCode, new ErrorParameters());
  }

  public NonRecoverableException(String message, Throwable cause, boolean enableSuppression,
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
