package life.qbic.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import life.qbic.domain.project.Project;
import life.qbic.domain.project.ProjectCode;
import life.qbic.domain.project.ProjectRepository;
import life.qbic.domain.sample.Sample;
import life.qbic.domain.sample.SampleRepository;

/**
 * Handles project status related requests.
 *
 * @since 2.0.0
 */
@Singleton
public class ProjectStatusService {

  private final ProjectRepository projectRepository;
  private final SampleRepository sampleRepository;

  @Inject
  public ProjectStatusService(ProjectRepository projectRepository,
      SampleRepository sampleRepository) {
    this.projectRepository = projectRepository;
    this.sampleRepository = sampleRepository;
  }

  public List<Sample> sampleStatuses(String projectCode) {
    ProjectCode code = ProjectCode.from(projectCode);
    Optional<Project> project = projectRepository.get(code);
    if (!project.isPresent()) {
      return new ArrayList<>();
    }
    ArrayList<Sample> result = new ArrayList<>();
    project.get().samples()
        .forEach(sampleCode -> sampleRepository.get(sampleCode).ifPresent(result::add));
    return result;
  }

}
