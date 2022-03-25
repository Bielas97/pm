package com.jbs.pm.domain.service.project;

import com.jbs.pm.domain.model.project.ProjectChange;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectChangeHandler {

  boolean canHandleChange(ProjectChange change);

  @Transactional
  void handleChange(ProjectChange change) throws ProjectChangeException;
}
