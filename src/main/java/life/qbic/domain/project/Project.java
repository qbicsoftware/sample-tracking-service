package life.qbic.domain.project;

import java.util.ArrayList;
import java.util.List;
import life.qbic.domain.sample.SampleCode;
import life.qbic.exception.UnrecoverableException;

/**
 * A project containing one or more samples.
 *
 * @since 2.0.0
 */
public class Project {

  private final ProjectCode code;

  private final List<SampleCode> samples;


  public Project(ProjectCode code, List<SampleCode> samples) {
    this.code = code;
    this.samples = new ArrayList<>();
    samples.forEach(this::addSample);
  }

  void addSample(SampleCode sampleCode) {
    if (!code.matches(sampleCode)) {
      throw new UnrecoverableException(
          String.format("Sample with code %s cannot be added to project %s", sampleCode, code));
    }
    samples.add(sampleCode);
  }

  public List<SampleCode> samples() {
    return samples;
  }
}
