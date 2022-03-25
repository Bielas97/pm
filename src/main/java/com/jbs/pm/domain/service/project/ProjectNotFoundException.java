package com.jbs.pm.domain.service.project;

import com.jbs.pm.domain.model.project.ProjectId;

public final class ProjectNotFoundException extends RuntimeException {

  private ProjectNotFoundException(String msg) {
    super(msg);
  }

  public static ProjectNotFoundException error(ProjectId id) {
    return new ProjectNotFoundException(
        String.format("Project with id [%s] has not been found", id.getInternal()));
  }
}
