package com.jbs.pm.adapter.output.db.documents;

import com.jbs.pm.domain.model.Tag;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectLeader;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projects")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectDocument {

  @Id private String id;

  @Nullable private String leaderId;

  private String fullName;

  private String shortName;

  private LocalDate startDate;

  private LocalDate endDate;

  private Set<String> tags;

  public static ProjectDocument from(Project project) {
    return ProjectDocument.builder()
        .id(project.getId().getInternal())
        .leaderId(project.getLeader().map(ProjectLeader::getInternalDeep).orElse(null))
        .fullName(project.getName().getFullName())
        .shortName(project.getName().getShortName())
        .startDate(project.getPeriod().getStart())
        .endDate(project.getPeriod().getEnd())
        .tags(project.getTags().stream().map(Tag::getValue).collect(Collectors.toSet()))
        .build();
  }
}
