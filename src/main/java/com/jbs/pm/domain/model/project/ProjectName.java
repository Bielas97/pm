package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.Preconditions;
import java.util.Locale;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
@EqualsAndHashCode
public final class ProjectName {

  private final String fullName;

  private final String shortName;

  private ProjectName(String name) {
    this.fullName = Preconditions.requireNonEmpty(StringUtils.capitalize(name));
    this.shortName = name.substring(0, 3).toUpperCase(Locale.ROOT);
  }

  private ProjectName(String fullName, String shortName) {
    this.fullName = StringUtils.capitalize(Preconditions.requireNonEmpty(fullName));
    this.shortName = Preconditions.requireNonEmpty(shortName).toUpperCase(Locale.ROOT);
  }

  public static ProjectName of(String name) {
    return new ProjectName(name);
  }

  public static ProjectName of(String fullName, String shortName) {
    return new ProjectName(fullName, shortName);
  }
}
