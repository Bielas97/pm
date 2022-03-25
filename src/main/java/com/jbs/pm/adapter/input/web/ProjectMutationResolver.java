package com.jbs.pm.adapter.input.web;

import com.aegon.util.lang.DatePeriod;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.jbs.pm.domain.model.project.CreateProjectRequest;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectBasicFieldsChange;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.user.UserId;
import com.jbs.pm.domain.service.project.ProjectComponent;
import com.jbs.pm.domain.service.project.ProjectComponentGroupType;
import com.jbs.pm.infrastructure.ApiComponentProvider;
import java.time.LocalDate;
import java.util.Optional;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class ProjectMutationResolver implements GraphQLMutationResolver {

  private final ProjectComponent projectComponent;

  public ProjectMutationResolver(ApiComponentProvider apiComponentProvider) {
    this.projectComponent =
        apiComponentProvider
            .getFactories(ProjectComponentGroupType.ID)
            .getComponent(ProjectComponent.class);
  }

  public ProjectDTO createProject(CreateProjectRequestDTO requestDTO) {
    val period =
        DatePeriod.builder()
            .start(LocalDate.parse(requestDTO.startDate))
            .end(LocalDate.parse(requestDTO.endDate))
            .build();
    val projectName =
        Optional.ofNullable(requestDTO.shortName)
            .map(shortName -> ProjectName.of(requestDTO.fullName, requestDTO.shortName))
            .orElseGet(() -> ProjectName.of(requestDTO.fullName));
    val request = CreateProjectRequest.builder().name(projectName).period(period).build();
    val addedId = projectComponent.add(request);
    val domain = projectComponent.get(addedId);
    return ProjectDTO.fromDomain(domain);
  }

  public ProjectDTO updateProject(String projectId, UpdateProjectRequestDTO requestDTO) {
    val projectChange = buildProjectChange(projectId, requestDTO);
    projectComponent.update(projectChange);
    val domain = projectComponent.get(projectChange.getProjectId());
    return ProjectDTO.fromDomain(domain);
  }

  private ProjectChange buildProjectChange(String projectId, UpdateProjectRequestDTO requestDTO) {
    val id = ProjectId.valueOf(projectId);
    val currentProject = projectComponent.get(id);
    val projectChangeBuilder = ProjectChange.builder().projectId(id);
    if (isBasicFieldsChange(requestDTO)) {
      fillBasicFieldsChangeToProjectChange(requestDTO, currentProject, projectChangeBuilder);
    }

    return projectChangeBuilder.build();
  }

  private boolean isBasicFieldsChange(UpdateProjectRequestDTO requestDTO) {
    return Optional.ofNullable(requestDTO.basicFieldsUpdate).isPresent()
        && (Optional.ofNullable(requestDTO.basicFieldsUpdate.endDate).isPresent()
            || Optional.ofNullable(requestDTO.basicFieldsUpdate.startDate).isPresent()
            || Optional.ofNullable(requestDTO.basicFieldsUpdate.leaderId).isPresent()
            || Optional.ofNullable(requestDTO.basicFieldsUpdate.shortName).isPresent()
            || Optional.ofNullable(requestDTO.basicFieldsUpdate.fullName).isPresent());
  }

  private void fillBasicFieldsChangeToProjectChange(
      UpdateProjectRequestDTO requestDTO,
      Project currentProject,
      ProjectChange.ProjectChangeBuilder projectChangeBuilder) {
    val basicFieldsChangeBuilder = ProjectBasicFieldsChange.builder();
    val basicFieldsDTO = requestDTO.basicFieldsUpdate;
    Optional.ofNullable(basicFieldsDTO.leaderId)
        .map(UserId::valueOf)
        .map(ProjectLeader::new)
        .ifPresent(basicFieldsChangeBuilder::projectLeader);

    val newProjectName = buildNewProjectName(currentProject.getName(), basicFieldsDTO);
    val newPeriod = buildNewDatePeriod(currentProject.getPeriod(), basicFieldsDTO);
    val projectNameChange = currentProject.getName().equals(newProjectName) ? null : newProjectName;
    val datePeriodChange = currentProject.getPeriod().equals(newPeriod) ? null : newPeriod;

    val basicFieldsChange =
        basicFieldsChangeBuilder
            .projectName(projectNameChange)
            .datePeriod(datePeriodChange)
            .build();

    projectChangeBuilder.projectBasicFieldsChange(basicFieldsChange);
  }

  private DatePeriod buildNewDatePeriod(
      DatePeriod currentPeriod, UpdateProjectBasicFieldsDTO basicFieldsDTO) {
    val datePeriodBuilder =
        Optional.ofNullable(basicFieldsDTO.startDate)
            .map(LocalDate::parse)
            .map(start -> DatePeriod.builder().start(start))
            .orElseGet(() -> DatePeriod.builder().start(currentPeriod.getStart()));

    Optional.ofNullable(basicFieldsDTO.endDate)
        .map(LocalDate::parse)
        .ifPresentOrElse(
            datePeriodBuilder::end, () -> datePeriodBuilder.end(currentPeriod.getEnd()));
    return datePeriodBuilder.build();
  }

  private ProjectName buildNewProjectName(
      ProjectName currentName, UpdateProjectBasicFieldsDTO basicFieldsDTO) {
    val projectNameBuilder =
        Optional.ofNullable(basicFieldsDTO.fullName)
            .map(fName -> ProjectName.builder().fullName(fName))
            .orElseGet(() -> ProjectName.builder().fullName(currentName.getFullName()));

    Optional.ofNullable(basicFieldsDTO.shortName)
        .ifPresentOrElse(
            projectNameBuilder::shortName,
            () -> projectNameBuilder.shortName(currentName.getShortName()));
    return projectNameBuilder.build();
  }
}
