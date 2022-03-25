package com.jbs.pm.domain.service.project;

import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectBasicChangeHandler implements ProjectChangeHandler {

  private final ProjectRepository projectRepository;

  @Override
  public boolean canHandleChange(ProjectChange change) {
    return change.getProjectBasicFieldsChange().isPresent();
  }

  @Override
  public void handleChange(ProjectChange change) throws ProjectChangeException {
    val basicFieldsChange = change.getProjectBasicFieldsChange().get();
    val project =
        projectRepository
            .findById(change.getProjectId())
            .orElseThrow(() -> ProjectNotFoundException.error(change.getProjectId()));

    basicFieldsChange.getProjectName().ifPresent(project::setName);
    basicFieldsChange.getProjectLeader().ifPresent(project::setLeader);
    basicFieldsChange.getDatePeriod().ifPresent(project::setPeriod);

    projectRepository.update(project);
  }
}
