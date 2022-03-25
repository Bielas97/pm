package com.jbs.pm.domain.service.project;

import static com.jbs.pm.domain.TestDataHelper.testProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectBasicFieldsChange;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.repository.ProjectRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectComponentImplTest {

  @Mock ProjectRepository projectRepository;

  @Mock ProjectChangeHandler changeHandler;

  ProjectComponentImpl sut;

  @BeforeEach
  void setUp() {
    sut = new ProjectComponentImpl(projectRepository, List.of(changeHandler));
  }

  @Test
  void shouldFindAllIfQueryIsEmpty() {
    val emptyQuery = ProjectQuery.EMPTY;

    sut.query(emptyQuery);

    verify(projectRepository).findAll();
  }

  @Test
  void shouldFindByIdIfQueryContainsProjectId() {
    val query =
        ProjectQuery.builder()
            .id(ProjectId.valueOf("test"))
            .nameWildcard("test")
            .leaderIdWildcard("test")
            .build();

    sut.query(query);

    verify(projectRepository).findById(query.getId().get());
  }

  @Test
  void shouldQueryWithArgsWhenProjectIdIsNotPResent() {
    val query = ProjectQuery.builder().nameWildcard("test").leaderIdWildcard("test").build();

    sut.query(query);

    verify(projectRepository, never()).findById(any());
    verify(projectRepository).findByNameLike(query.getNameWildcard().get());
    verify(projectRepository).findByLeaderIdLike(query.getLeaderIdWildcard().get());
  }

  @Test
  void shouldQueryWithCorrectResults() {
    val queryPeriodStart = LocalDate.of(2021, 1, 1);
    val queryPeriodEnd = LocalDate.of(2021, 1, 15);

    val query =
        ProjectQuery.builder()
            .nameWildcard("test")
            .leaderIdWildcard("test")
            .period(DatePeriod.builder().start(queryPeriodStart).end(queryPeriodEnd).build())
            .build();
    val p1 = Mockito.mock(Project.class);
    val p2 = Mockito.mock(Project.class);
    val p3 = Mockito.mock(Project.class);
    val p4 = Mockito.mock(Project.class);

    val wrongProjectPeriod =
        DatePeriod.builder()
            .start(queryPeriodStart.minusMonths(12))
            .end(queryPeriodEnd.minusMonths(10))
            .build();
    val correctProjectPeriod =
        DatePeriod.builder().start(queryPeriodStart).end(queryPeriodEnd.minusDays(1)).build();

    val nameQueryProjects = List.of(p1, p2, p3);
    val leaderQueryProjects = List.of(p2, p3, p4);

    when(projectRepository.findByNameLike(query.getNameWildcard().get()))
        .thenReturn(nameQueryProjects);
    when(projectRepository.findByLeaderIdLike(query.getLeaderIdWildcard().get()))
        .thenReturn(leaderQueryProjects);

    when(p2.getPeriod()).thenReturn(wrongProjectPeriod);
    when(p3.getPeriod()).thenReturn(correctProjectPeriod);

    val result = sut.query(query);

    assertEquals(1, result.size());
    assertEquals(p3, result.iterator().next());
  }

  @Test
  void shouldBasicChangeHandlerBeInvokedWhenProjectUpdate() {
    val project = testProject();
    val change =
        ProjectChange.builder()
            .projectId(project.getId())
            .projectBasicFieldsChange(
                ProjectBasicFieldsChange.builder().projectName(ProjectName.of("siemka")).build())
            .build();

    when(changeHandler.canHandleChange(change)).thenReturn(true);

    sut.update(change);

    verify(changeHandler).handleChange(change);
  }
}
