package com.jbs.pm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jbs.pm.domain.model.project.ProjectId;
import lombok.val;
import org.junit.jupiter.api.Test;

class ProjectIdTest {

  @Test
  void shouldNotPass() {
    val projectId = ProjectId.valueOf("siemka");

    assertEquals(projectId.getInternal(), "siemka");
  }
}
