package life.qbic.domain.sample;

import java.util.List;
import life.qbic.domain.project.ProjectCode;

/**
 * A repository for domain events.
 *
 * @since 1.0.0
 */
public interface SampleEventDatasource {
  <T extends SampleEvent> void store(T sampleEvent);
  List<SampleEvent> findAllForSample(SampleCode sampleCode);

  List<SampleEvent> findAllMatchingProject(ProjectCode projectCode);
}
