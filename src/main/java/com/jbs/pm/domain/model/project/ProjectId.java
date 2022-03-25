package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.Preconditions;
import com.aegon.util.lang.SimpleId;

public final class ProjectId extends SimpleId<String> {

  private ProjectId(String internal) {
    super(Preconditions.requireNonNull(internal));
  }

  public static ProjectId valueOf(String id) {
    return new ProjectId(id);
  }
}
