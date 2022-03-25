package com.jbs.pm.domain;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.adapter.output.db.documents.ProjectDocument;
import com.jbs.pm.domain.model.project.CreateProjectRequest;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectBasicFieldsChange;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.user.UserId;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDataHelper {

  public static Project testProject() {
    return testProjectBuilder().build();
  }

  public static Project.ProjectBuilder testProjectBuilder() {
    return Project.builder()
        .id(ProjectId.valueOf("projectId"))
        .name(ProjectName.of("fullname", "shortname"))
        .leader(new ProjectLeader(UserId.valueOf("userId")))
        .period(
            DatePeriod.builder()
                .start(LocalDate.parse("2005-01-01"))
                .end(LocalDate.parse("2006-01-01"))
                .build());
  }

  public static CreateProjectRequest testCreateProjectRequest() {
    return testCreateProjectRequestBuilder().build();
  }

  public static ProjectChange testPojectChange(ProjectId projectId) {
    return testProjectChangeBuilder().projectId(projectId).build();
  }

  public static ProjectChange.ProjectChangeBuilder testProjectChangeBuilder() {
    return ProjectChange.builder()
        .projectId(ProjectId.valueOf("projectId"))
        .projectBasicFieldsChange(testBasicFieldsChange());
  }

  public static ProjectBasicFieldsChange testBasicFieldsChange() {
    return testBasicFieldsChangeBuilder().build();
  }

  public static ProjectBasicFieldsChange.ProjectBasicFieldsChangeBuilder
      testBasicFieldsChangeBuilder() {
    return ProjectBasicFieldsChange.builder()
        .projectName(ProjectName.of("change", "chan"))
        .projectLeader(new ProjectLeader(UserId.valueOf("projectLeader")))
        .datePeriod(
            DatePeriod.builder()
                .start(LocalDate.of(2021, 1, 1))
                .end(LocalDate.of(2022, 1, 1))
                .build());
  }

  public static CreateProjectRequest.CreateProjectRequestBuilder testCreateProjectRequestBuilder() {
    return CreateProjectRequest.builder()
        .name(ProjectName.of("fullName", "shortName"))
        .period(
            DatePeriod.builder()
                .start(LocalDate.of(2021, 1, 1))
                .end(LocalDate.of(2022, 1, 1))
                .build());
  }

  public static ProjectDocument testProjectDoc() {
    return testProjectDocBuilder().build();
  }

  public static ProjectDocument.ProjectDocumentBuilder testProjectDocBuilder() {
    return ProjectDocument.builder()
        .id("projectId")
        .fullName("fullName")
        .shortName("shortName")
        .leaderId("userId")
        .startDate(LocalDate.of(2021, 1, 1))
        .endDate(LocalDate.of(2021, 1, 1));
  }
}
