package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.Preconditions;
import com.aegon.util.lang.SimpleId;

public final class SideEventId extends SimpleId<String> {

  private SideEventId(String internal) {
    super(internal);
  }

  public static SideEventId valueOf(String id) {
    return new SideEventId(Preconditions.requireNonEmpty(id));
  }
}
