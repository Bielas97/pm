package com.jbs.pm.domain.model;

import com.aegon.util.lang.Preconditions;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Tag {

  private final String value;

  private Tag(String value) {
    this.value = Preconditions.requireNonEmpty(value);
  }

  public static Tag valueOf(String value) {
    return new Tag(value.toUpperCase(Locale.ROOT));
  }
}
