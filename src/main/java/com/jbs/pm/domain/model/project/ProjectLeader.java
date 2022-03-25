package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.SimpleId;
import com.jbs.pm.domain.model.user.UserId;
import lombok.Getter;

@Getter
public final class ProjectLeader extends SimpleId<UserId> {

  public ProjectLeader(UserId internal) {
    super(internal);
  }

  public String getInternalDeep() {
    return getInternal().getInternal();
  }
}
