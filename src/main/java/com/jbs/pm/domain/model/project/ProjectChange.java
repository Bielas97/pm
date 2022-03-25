package com.jbs.pm.domain.model.project;

import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectChange {

  ProjectId projectId;

  @Nullable ProjectBasicFieldsChange projectBasicFieldsChange;

  public Optional<ProjectBasicFieldsChange> getProjectBasicFieldsChange() {
    return Optional.ofNullable(projectBasicFieldsChange);
  }
}
