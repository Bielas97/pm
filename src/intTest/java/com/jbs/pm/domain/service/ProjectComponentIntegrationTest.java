package com.jbs.pm.domain.service;

import static com.jbs.pm.domain.TestDataHelper.testCreateProjectRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.service.project.ProjectComponent;
import com.jbs.pm.domain.service.project.ProjectComponentGroupType;
import com.jbs.pm.infrastructure.ApiComponentProvider;
import com.jbs.pm.testutils.MongoRepositorySupport;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = MongoRepositorySupport.class)
@AutoConfigureMockMvc
public class ProjectComponentIntegrationTest {

  private final ProjectComponent projectComponent;

  @Autowired
  public ProjectComponentIntegrationTest(ApiComponentProvider apiComponentProvider) {
    this.projectComponent =
        apiComponentProvider
            .getFactories(ProjectComponentGroupType.ID)
            .getComponent(ProjectComponent.class);
  }

  @Test
  void shouldAddExactlyOneProject() {
    final ProjectId projectId = projectComponent.add(testCreateProjectRequest());

    final Collection<Project> allProjects = projectComponent.query(ProjectQuery.EMPTY);

    assertEquals(1, allProjects.size());
    assertEquals(projectId, allProjects.iterator().next().getId());
  }
}
