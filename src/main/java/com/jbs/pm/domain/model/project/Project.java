package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.DatePeriod;
import com.aegon.util.lang.Preconditions;
import com.jbs.pm.domain.model.Tag;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Project {

  private final ProjectId id;

  private ProjectName name;

  @Nullable private ProjectLeader leader;

  private DatePeriod period;

  private final Set<Tag> tags = new HashSet<>();

  public Project(
      ProjectId id, ProjectName name, @Nullable ProjectLeader leader, DatePeriod period) {
    this.id = Preconditions.requireNonNull(id);
    this.name = Preconditions.requireNonNull(name);
    this.leader = leader;
    this.period = Preconditions.requireNonNull(period);
  }

  public Optional<ProjectLeader> getLeader() {
    return Optional.ofNullable(leader);
  }

  public Set<Tag> getTags() {
    return new HashSet<>(tags);
  }

  // TODO change aegon util lang to mutable object
  public void extendPeriod(LocalDate endDate) {
    this.period = period.extend(endDate);
  }

  public void addTag(Tag tag) {
    this.tags.add(tag);
  }

  public void removeTag(Tag tag) {
    this.tags.remove(tag);
  }
}
