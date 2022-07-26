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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SampleCode)) {
      return false;
    }

    SampleCode that = (SampleCode) o;

    return text.equals(that.text);
  }

  @Override
  public int hashCode() {
    return text.hashCode();
  }
}
