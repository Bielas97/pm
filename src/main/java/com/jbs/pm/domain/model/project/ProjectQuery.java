package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.DatePeriod;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectQuery {

  public static final ProjectQuery EMPTY = ProjectQuery.builder().build();

  @Nullable ProjectId id;

  @Nullable String nameWildcard;

  @Nullable String leaderIdWildcard;

  @Nullable DatePeriod period;

  public boolean isEmpty() {
    return EMPTY.equals(this);
  }

  public Optional<ProjectId> getId() {
    return Optional.ofNullable(id);
  }

  public Optional<String> getNameWildcard() {
    return Optional.ofNullable(nameWildcard);
  }

  public Optional<String> getLeaderIdWildcard() {
    return Optional.ofNullable(leaderIdWildcard);
  }

  public Optional<DatePeriod> getPeriod() {
    return Optional.ofNullable(period);
  }
}
