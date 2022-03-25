package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.Preconditions;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class SideEvent {

  private final SideEventId id;

  private final String name;

  private final String notes;

  private final LocalDate date;

  public SideEvent(SideEventId id, String name, String notes, LocalDate date) {
    this.id = Preconditions.requireNonNull(id);
    this.name = Preconditions.requireNonEmpty(name);
    this.notes = notes;
    this.date = Preconditions.requireNonNull(date);
  }
}
