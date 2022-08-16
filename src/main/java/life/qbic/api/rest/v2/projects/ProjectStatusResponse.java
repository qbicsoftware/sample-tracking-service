package life.qbic.api.rest.v2.projects;

import java.util.ArrayList;
import java.util.List;
import life.qbic.api.rest.v2.mapper.SampleStatusDtoMapper;
import life.qbic.domain.sample.Sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ProjectStatusResponse extends ArrayList<SampleStatusBean> {

  public static ProjectStatusResponse from(List<Sample> samples) {
    ProjectStatusResponse response = new ProjectStatusResponse();
    samples.stream()
        .map(it -> SampleStatusBean.create(it.sampleCode().toString(),
            SampleStatusDtoMapper.sampleStatusToDto(it.currentState().status()),
            it.currentState().statusValidSince()))
        .forEach(response::add);
    return response;
  }


}
