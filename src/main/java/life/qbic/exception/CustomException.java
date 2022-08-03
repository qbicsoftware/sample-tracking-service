package life.qbic.exception;

/**
 * <p>An unspecific exception from the application.</p>
 *
 * @since 2.0.0
 */
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final ErrorParameters errorParameters;



  public CustomException(ErrorCode errorCode, ErrorParameters errorParameters) {
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public CustomException(String message, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public CustomException(String message, Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(message, cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }

  public CustomException(Throwable cause, ErrorCode errorCode,
      ErrorParameters errorParameters) {
    super(cause);
    this.errorCode = errorCode;
    this.errorParameters = errorParameters;
  }


  public CustomException(Throwable cause) {
    this(cause, ErrorCode.GENERAL);
  }

  public CustomException(String message) {
    this(message, ErrorCode.GENERAL, new ErrorParameters());
  }

  public CustomException(Throwable cause, ErrorCode errorCode) {
    this(cause, errorCode, new ErrorParameters());
  }
  public CustomException(String message, Throwable cause, ErrorCode errorCode) {
    this(message, cause, errorCode, new ErrorParameters());
  }

  public CustomException(String message, Throwable cause, boolean enableSuppression,
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
