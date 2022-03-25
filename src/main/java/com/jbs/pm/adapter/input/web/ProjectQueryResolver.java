package com.jbs.pm.adapter.input.web;

import com.aegon.util.lang.DatePeriod;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.service.project.ProjectComponent;
import com.jbs.pm.domain.service.project.ProjectComponentGroupType;
import com.jbs.pm.infrastructure.ApiComponentProvider;
import java.time.LocalDate;
import java.util.Optional;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class ProjectQueryResolver implements GraphQLQueryResolver {

  private final ProjectComponent projectComponent;

  public ProjectQueryResolver(ApiComponentProvider apiComponentProvider) {
    this.projectComponent =
        apiComponentProvider
            .getFactories(ProjectComponentGroupType.ID)
            .getComponent(ProjectComponent.class);
  }

  public Iterable<ProjectDTO> findAllProjects() {
    return projectComponent.query(ProjectQuery.EMPTY).stream().map(ProjectDTO::fromDomain).toList();
  }

  public Iterable<ProjectDTO> findProjects(ProjectQueryDTO query) {
    val projectId = Optional.ofNullable(query.id).map(ProjectId::valueOf).orElse(null);
    val domainQueryBuilder =
        ProjectQuery.builder()
            .id(projectId)
            .nameWildcard(query.nameWildcard)
            .leaderIdWildcard(query.leaderIdWildcard);

    if (query.startDate != null && query.endDate != null) {
      domainQueryBuilder.period(
          DatePeriod.builder()
              .start(LocalDate.parse(query.startDate))
              .end(LocalDate.parse(query.endDate))
              .build());
    }

    return projectComponent.query(domainQueryBuilder.build()).stream()
        .map(ProjectDTO::fromDomain)
        .toList();
  }
}
