package com.jbs.pm.adapter.input.web;

import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectLeader;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public class ProjectDTO {
  public String id;
  public String fullName;
  public String shortName;
  public String startDate;
  public String endDate;
  public String leaderId;

  public static ProjectDTO fromDomain(Project project) {
    return ProjectDTO.builder()
        .id(project.getId().getInternal())
        .fullName(project.getName().getFullName())
        .shortName(project.getName().getShortName())
        .startDate(project.getPeriod().getStart().format(DateTimeFormatter.ISO_DATE))
        .endDate(project.getPeriod().getEnd().format(DateTimeFormatter.ISO_DATE))
        .leaderId(project.getLeader().map(ProjectLeader::getInternalDeep).orElse(null))
        .build();
  }

  public static ProjectDTO empty() {
    return ProjectDTO.builder().build();
  }
}
