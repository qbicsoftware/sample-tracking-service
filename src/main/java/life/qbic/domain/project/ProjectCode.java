package life.qbic.domain.project;

import static java.util.Objects.isNull;

import java.util.regex.Pattern;
import life.qbic.domain.sample.SampleCode;
import life.qbic.exception.ErrorCode;
import life.qbic.exception.ErrorParameters;
import life.qbic.exception.UnrecoverableException;

/**
 * A project code used to identify a project.
 * @since 2.0.0
 */
public class ProjectCode {

  private static final Pattern codePattern = Pattern.compile("(Q[A-X0-9]{4})");
  private final String code;

  private ProjectCode(String code) {
    if (isNull(code) || code.isEmpty()) {
      throw new UnrecoverableException(String.format(
          String.format("No project code provided: '%s'", code), code),
          ErrorCode.BAD_PROJECT_CODE, ErrorParameters.create().with("projectCode", code));
    }
    if(!codePattern.asPredicate().test(code)) {
      throw new UnrecoverableException(String.format("Project code '%s' is invalid.", code),
          ErrorCode.BAD_PROJECT_CODE, ErrorParameters.create().with("projectCode", code));
    }
    this.code = code;
  }

  public static ProjectCode from(SampleCode sampleCode) {
    String code = sampleCode.toString().substring(0, 5);
    return new ProjectCode(code);
  }

  public static ProjectCode from(String code) {
    return new ProjectCode(code);
  }

  public boolean matches(SampleCode sampleCode) {
    return sampleCode.toString().startsWith(code);
  }

  @Override
  public String toString() {
    return code;
  }
}
