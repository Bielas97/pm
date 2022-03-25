package com.jbs.pm.domain.model.project;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SideEventTest {

  @Test
  void shouldThrowErrorIfBuilderDoesNotSupplyDate() {
    assertThrows(
        NullPointerException.class,
        () -> {
          SideEvent.builder().id(SideEventId.valueOf("id")).name("name").build();
        });
  }

  @Test
  void shouldThrowErrorIfBuilderDoesNotSupplyId() {
    assertThrows(
        NullPointerException.class,
        () -> {
          SideEvent.builder().name("name").date(LocalDate.now()).build();
        });
  }

  @Test
  void shouldThrowErrorIfBuilderDoesNotSupplyname() {
    assertThrows(
        NullPointerException.class,
        () -> {
          SideEvent.builder().id(SideEventId.valueOf("id")).date(LocalDate.now()).build();
        });
  }
}
