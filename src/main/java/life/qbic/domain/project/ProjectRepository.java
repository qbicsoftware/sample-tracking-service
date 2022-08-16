package life.qbic.domain.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;
import life.qbic.domain.sample.SampleEventDatasource;

/**
 * ProjectRepository used to read project information from persistence.
 *
 * @since 2.0.0
 */
@Singleton
public class ProjectRepository {

  private final SampleEventDatasource sampleEventDatasource;

  public ProjectRepository(SampleEventDatasource sampleEventDatasource) {
    this.sampleEventDatasource = sampleEventDatasource;
  }

  public Optional<Project> get(ProjectCode projectCode) {
    List<SampleEvent> allMatchingProject = sampleEventDatasource.findAllMatchingProject(
        projectCode);
    if (allMatchingProject.isEmpty()) {
      return Optional.empty();
    }
    List<SampleCode> sampleCodes = allMatchingProject.stream()
        .map(SampleEvent::sampleCode)
        .distinct().collect(Collectors.toList());
    Project project = new Project(projectCode, sampleCodes);

    return Optional.of(project);
  }

}
