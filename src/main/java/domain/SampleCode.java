package domain;

/**
 * <p>The sample code of a sample.</p>
 * <p>TODO: mode detailed information.</p>
 *
 * @since 2.0.0
 */
public class SampleCode {
  private final String text;

  private SampleCode(String text) {
    this.text = text;
  }


  public static SampleCode fromString(String sampleCode) {
    return new SampleCode(sampleCode);
  }

  public String toString() {
    return text;
  }
}
