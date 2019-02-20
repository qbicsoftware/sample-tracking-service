package life.qbic

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegExValidator {
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile('^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$', Pattern.CASE_INSENSITIVE);
  private static final Pattern VALID_QBIC_SAMPLE_CODE = Pattern.compile('(Q[A-Z0-9]{4}[0-9]{3}[A-Z][A-Z0-9]$)|(Q[A-Z0-9]{4}ENTITY-[0-9]*$)', Pattern.CASE_INSENSITIVE);

  public static isValidMail(String mail) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
    return matcher.find();
  }
  
  public static isValidSampleCode(String code) {
    Matcher matcher = VALID_QBIC_SAMPLE_CODE.matcher(code);
    return matcher.find();
  }
}
