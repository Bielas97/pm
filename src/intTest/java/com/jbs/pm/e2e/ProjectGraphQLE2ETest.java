package com.jbs.pm.e2e;

import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.service.project.ProjectComponent;
import com.jbs.pm.domain.service.project.ProjectComponentGroupType;
import com.jbs.pm.infrastructure.ApiComponentProvider;
import com.jbs.pm.testutils.MongoRepositorySupport;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = MongoRepositorySupport.class)
@TestPropertySource(properties = {"graphql.servlet.mapping=/api"})
@AutoConfigureMockMvc
@SuppressWarnings({
  "PMD.UnusedPrivateMethod",
})
public abstract class ProjectGraphQLE2ETest {

  @Autowired private ApiComponentProvider apiComponentProvider;
  @LocalServerPort private int randomServerPort;

  protected ProjectComponent projectComponent;
  protected PmTestClient pmTestClient;

  @PostConstruct
  private void setup() {
    this.pmTestClient = new PmTestClient(randomServerPort);
    this.projectComponent =
        apiComponentProvider
            .getFactories(ProjectComponentGroupType.ID)
            .getComponent(ProjectComponent.class);
  }

  @AfterEach
  void cleanUpDatabase() {
    projectComponent.query(ProjectQuery.EMPTY).stream()
        .map(Project::getId)
        .forEach(projectComponent::delete);
  }
}
