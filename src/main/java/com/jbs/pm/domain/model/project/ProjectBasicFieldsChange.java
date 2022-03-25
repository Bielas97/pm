package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.DatePeriod;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectBasicFieldsChange {

  @Nullable ProjectName projectName;

  @Nullable ProjectLeader projectLeader;

  @Nullable DatePeriod datePeriod;

  public Optional<ProjectName> getProjectName() {
    return Optional.ofNullable(projectName);
  }

  public Optional<ProjectLeader> getProjectLeader() {
    return Optional.ofNullable(projectLeader);
  }

  public Optional<DatePeriod> getDatePeriod() {
    return Optional.ofNullable(datePeriod);
  }
}
