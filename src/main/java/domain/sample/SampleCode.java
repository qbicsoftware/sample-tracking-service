package domain.sample;

import life.qbic.controller.RegExValidator;

/**
 * <p>The sample code of a sample.</p>
 *
 * @since 2.0.0
 */
public class SampleCode {
  private final String text;

  private SampleCode(String text) {
    if(!RegExValidator.isValidSampleCode(text)) {
      throw new IllegalArgumentException(String.format("Sample code '%s' is invalid.", text));
    }
    this.text = text;
  }


  public static SampleCode fromString(String sampleCode) {
    return new SampleCode(sampleCode);
  }

  public String toString() {
    return text;
  }
}
