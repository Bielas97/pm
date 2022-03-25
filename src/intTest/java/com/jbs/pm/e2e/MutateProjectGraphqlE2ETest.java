package com.jbs.pm.e2e;

import static com.jbs.pm.domain.TestDataHelper.testCreateProjectRequest;
import static com.jbs.pm.domain.TestDataHelper.testPojectChange;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jbs.pm.UpdateBasicFieldsMutation;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import org.junit.jupiter.api.Test;

public class MutateProjectGraphqlE2ETest extends ProjectGraphQLE2ETest {

  @Test
  void shouldUpdateBasicFields() {
    final ProjectId projectId = projectComponent.add(testCreateProjectRequest());
    final ProjectChange projectChange = testPojectChange(projectId);
    final UpdateBasicFieldsMutation.UpdateProject updatedProject =
        pmTestClient.updateBasicFields(
            projectChange.getProjectId(), projectChange.getProjectBasicFieldsChange().get());

    final Project project = projectComponent.get(projectId);
    assertEquals(project.getId().getInternal(), updatedProject.id());
    assertEquals(project.getPeriod().getStart().toString(), updatedProject.startDate());
    assertEquals(project.getPeriod().getEnd().toString(), updatedProject.endDate());
    assertEquals(project.getName().getShortName(), updatedProject.shortName());
    assertEquals(project.getName().getFullName(), updatedProject.fullName());
    assertEquals(
        project.getLeader().map(ProjectLeader::getInternalDeep).orElse(null),
        updatedProject.leaderId());
  }
}
