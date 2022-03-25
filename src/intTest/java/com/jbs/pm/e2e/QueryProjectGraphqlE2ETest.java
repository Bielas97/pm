package com.jbs.pm.e2e;

import static com.jbs.pm.domain.TestDataHelper.testBasicFieldsChangeBuilder;
import static com.jbs.pm.domain.TestDataHelper.testCreateProjectRequest;
import static com.jbs.pm.domain.TestDataHelper.testCreateProjectRequestBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.AllProjectsQuery;
import com.jbs.pm.QueriedProjectsQuery;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.model.user.UserId;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class QueryProjectGraphqlE2ETest extends ProjectGraphQLE2ETest {

  @Test
  void shouldFindAllProjects() {
    final ProjectId projectId1 = projectComponent.add(testCreateProjectRequest());
    final ProjectId projectId2 = projectComponent.add(testCreateProjectRequest());

    final List<ProjectId> queriedProjects =
        pmTestClient.findAllProjects().stream()
            .map(AllProjectsQuery.FindAllProject::id)
            .map(ProjectId::valueOf)
            .collect(Collectors.toList());

    assertEquals(List.of(projectId1, projectId2), queriedProjects);
  }

  @Test
  void shouldQueryProjectByIdIfQueryContainsId() {
    final ProjectId projectId = projectComponent.add(testCreateProjectRequest());
    projectComponent.add(testCreateProjectRequest());
    final ProjectQuery query = ProjectQuery.builder().id(projectId).nameWildcard("wild").build();
    projectComponent.add(
        testCreateProjectRequestBuilder().name(ProjectName.of("wildcard", "WIL")).build());
    projectComponent.add(
        testCreateProjectRequestBuilder().name(ProjectName.of("wildcard2", "WIL")).build());

    final List<QueriedProjectsQuery.FindProject> findProjects = pmTestClient.queryProjects(query);

    assertEquals(1, findProjects.size());
    assertEquals(projectId, ProjectId.valueOf(findProjects.iterator().next().id()));
  }

  @Test
  void shouldQueryProjectByNameWildcard() {
    final ProjectQuery query = ProjectQuery.builder().nameWildcard("any").build();
    final ProjectId anyNameProject =
        projectComponent.add(
            testCreateProjectRequestBuilder().name(ProjectName.of("anyname", "ANY")).build());
    final ProjectId anyName2Project =
        projectComponent.add(
            testCreateProjectRequestBuilder().name(ProjectName.of("anyname2", "ANY")).build());

    final List<ProjectId> resultIds =
        pmTestClient.queryProjects(query).stream()
            .map(QueriedProjectsQuery.FindProject::id)
            .map(ProjectId::valueOf)
            .collect(Collectors.toList());

    assertEquals(2, resultIds.size());
    assertTrue(resultIds.contains(anyNameProject) && resultIds.contains(anyName2Project));
  }

  @Test
  void shouldQueryProjectByDatePeriodWildcard() {
    final ProjectQuery query =
        ProjectQuery.builder()
            .period(
                DatePeriod.builder()
                    .start(LocalDate.of(2100, 1, 1))
                    .end(LocalDate.of(2100, 1, 4))
                    .build())
            .build();
    final ProjectId projectCorrectPeriod =
        projectComponent.add(
            testCreateProjectRequestBuilder()
                .period(
                    DatePeriod.builder()
                        .start(LocalDate.of(2100, 1, 2))
                        .end(LocalDate.of(2100, 1, 3))
                        .build())
                .build());
    final ProjectId projectCorrectPeriod2 =
        projectComponent.add(
            testCreateProjectRequestBuilder()
                .period(
                    DatePeriod.builder()
                        .start(LocalDate.of(2100, 1, 1))
                        .end(LocalDate.of(2100, 1, 4))
                        .build())
                .build());

    final List<ProjectId> resultIds =
        pmTestClient.queryProjects(query).stream()
            .map(QueriedProjectsQuery.FindProject::id)
            .map(ProjectId::valueOf)
            .collect(Collectors.toList());

    assertEquals(2, resultIds.size());
    assertTrue(
        resultIds.contains(projectCorrectPeriod) && resultIds.contains(projectCorrectPeriod2));
  }

  @Test
  void shouldQueryProjectByLeaderWildcard() {
    final ProjectId projectId = projectComponent.add(testCreateProjectRequest());
    final ProjectChange change =
        ProjectChange.builder()
            .projectBasicFieldsChange(
                testBasicFieldsChangeBuilder()
                    .projectLeader(new ProjectLeader(UserId.valueOf("leader")))
                    .build())
            .projectId(projectId)
            .build();
    projectComponent.update(change);

    final ProjectQuery query = ProjectQuery.builder().leaderIdWildcard("lea").build();
    final List<ProjectId> resultIds =
        pmTestClient.queryProjects(query).stream()
            .map(QueriedProjectsQuery.FindProject::id)
            .map(ProjectId::valueOf)
            .collect(Collectors.toList());

    assertEquals(1, resultIds.size());
    assertEquals(projectId, resultIds.iterator().next());
  }

  @Test
  void shouldQueryProjectByNamePeriodAndLeaderWildcards() {
    final ProjectId projectId = projectComponent.add(testCreateProjectRequest());
    final ProjectChange change =
        ProjectChange.builder()
            .projectBasicFieldsChange(
                testBasicFieldsChangeBuilder()
                    .projectName(ProjectName.of("name", "NAM"))
                    .datePeriod(
                        DatePeriod.builder()
                            .start(LocalDate.of(2100, 1, 2))
                            .end(LocalDate.of(2100, 1, 3))
                            .build())
                    .projectLeader(new ProjectLeader(UserId.valueOf("leader")))
                    .build())
            .projectId(projectId)
            .build();
    projectComponent.update(change);

    final ProjectQuery query =
        ProjectQuery.builder()
            .nameWildcard("na")
            .period(
                DatePeriod.builder()
                    .start(LocalDate.of(2100, 1, 1))
                    .end(LocalDate.of(2100, 1, 4))
                    .build())
            .leaderIdWildcard("lea")
            .build();
    final List<ProjectId> resultIds =
        pmTestClient.queryProjects(query).stream()
            .map(QueriedProjectsQuery.FindProject::id)
            .map(ProjectId::valueOf)
            .collect(Collectors.toList());

    assertEquals(1, resultIds.size());
    assertEquals(projectId, resultIds.iterator().next());
  }
}
