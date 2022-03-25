package com.jbs.pm.domain.model.user;

import com.aegon.util.lang.Preconditions;
import com.aegon.util.lang.SimpleId;

public final class UserId extends SimpleId<String> {
  private UserId(String internal) {
    super(internal);
  }

  public static UserId valueOf(String id) {
    return new UserId(Preconditions.requireNonEmpty(id));
  }
}
