package life.qbic.controller

import groovy.util.logging.Log4j2
import life.qbic.datamodel.identifiers.SampleCodeFunctions

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log4j2
class RegExValidator {
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile('^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$', Pattern.CASE_INSENSITIVE);
  private static final Pattern VALID_QBIC_SAMPLE_CODE = Pattern.compile('(Q[A-X0-9]{4}[0-9]{3}[A-X][A-X0-9]$)|(Q[A-X0-9]{4}ENTITY-[0-9]*$)', Pattern.CASE_INSENSITIVE);

  public static boolean isValidMail(String mail) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
    boolean res = matcher.find()
    return res
  }

  public static boolean isValidSampleCode(String code) {
    boolean res = SampleCodeFunctions.isQbicBarcode(code) || SampleCodeFunctions.isQbicEntityCode(code)
    return res
  }
}
