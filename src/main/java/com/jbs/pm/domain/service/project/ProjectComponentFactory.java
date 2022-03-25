package com.jbs.pm.domain.service.project;

import com.aegon.util.components.ApiComponentFactory;
import com.aegon.util.components.ApiComponentGroupType;
import com.jbs.pm.domain.repository.ProjectRepository;
import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectComponentFactory implements ApiComponentFactory<ProjectComponent> {

  private final ProjectRepository projectRepository;

  private final Collection<ProjectChangeHandler> changeHandlers;

  @Override
  public ProjectComponent create() {
    return new ProjectComponentImpl(projectRepository, changeHandlers);
  }

  @Override
  public Class<ProjectComponent> getComponentClass() {
    return ProjectComponent.class;
  }

  @Override
  public InvocationHandler createProxy() {
    return new ProjectComponentProxy(create());
  }

  @Override
  public ApiComponentGroupType getGroupType() {
    return ProjectComponentGroupType.ID;
  }
}
