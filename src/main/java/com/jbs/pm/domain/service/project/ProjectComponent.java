package com.jbs.pm.domain.service.project;

import com.aegon.util.components.ApiComponent;
import com.jbs.pm.domain.model.project.CreateProjectRequest;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectQuery;
import java.util.Collection;

public interface ProjectComponent extends ApiComponent {

  Project get(ProjectId id);

  Collection<Project> query(ProjectQuery query);

  ProjectId add(CreateProjectRequest request);

  void update(ProjectChange change);

  void delete(ProjectId projectId);
}
