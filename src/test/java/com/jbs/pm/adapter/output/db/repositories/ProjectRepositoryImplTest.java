package com.jbs.pm.adapter.output.db.repositories;

import static com.jbs.pm.domain.TestDataHelper.testProject;
import static com.jbs.pm.domain.TestDataHelper.testProjectDocBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectName;
import java.time.LocalDate;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryImplTest {

  @Mock MongoProjectProvider mongoProjectProvider;

  @InjectMocks ProjectRepositoryImpl sut;

  @Test
  void shouldFindById() {
    val project = testProject();

    sut.findById(project.getId());

    verify(mongoProjectProvider).findById(project.getId().getInternal());
  }

  @Test
  void shouldFindALL() {
    sut.findAll();

    verify(mongoProjectProvider).findAll();
  }

  @Test
  void shouldFindByNameLike() {
    sut.findByNameLike("test");

    verify(mongoProjectProvider).findByFullNameLikeIgnoreCase("test");
  }

  @Test
  void shouldFindByLeaderIdLike() {
    sut.findByLeaderIdLike("test");

    verify(mongoProjectProvider).findByLeaderIdLikeIgnoreCase("test");
  }

  @Test
  void shouldAddProject() {
    val name = ProjectName.of("fullName", "shortName");
    val period =
        DatePeriod.builder().start(LocalDate.of(2021, 1, 1)).end(LocalDate.of(2021, 1, 2)).build();
    val projectDoc =
        testProjectDocBuilder()
            .fullName(name.getFullName())
            .shortName(name.getShortName())
            .startDate(period.getStart())
            .endDate(period.getEnd())
            .build();

    when(mongoProjectProvider.save(any())).thenReturn(projectDoc);

    final ProjectId savedId = sut.add(name, period);

    verify(mongoProjectProvider).save(any());
    assertEquals(projectDoc.getId(), savedId.getInternal());
  }
}
