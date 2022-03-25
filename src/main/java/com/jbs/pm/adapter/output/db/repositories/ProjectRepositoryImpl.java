package com.jbs.pm.adapter.output.db.repositories;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.adapter.output.db.documents.ProjectDocument;
import com.jbs.pm.domain.model.Tag;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.user.UserId;
import com.jbs.pm.domain.repository.ProjectRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

  private final MongoProjectProvider mongoProjectProvider;

  @Override
  public Optional<Project> findById(ProjectId projectId) {
    return mongoProjectProvider.findById(projectId.getInternal()).map(this::map);
  }

  @Override
  public List<Project> findAll() {
    return mongoProjectProvider.findAll().stream().map(this::map).toList();
  }

  @Override
  public ProjectId add(ProjectName name, DatePeriod period) {
    val doc =
        ProjectDocument.builder()
            .fullName(name.getFullName())
            .shortName(name.getShortName())
            .startDate(period.getStart())
            .endDate(period.getEnd())
            .tags(new HashSet<>())
            .build();
    val saved = mongoProjectProvider.save(doc);
    return ProjectId.valueOf(saved.getId());
  }

  @Override
  public List<Project> findByNameLike(String nameWildcard) {
    return mongoProjectProvider.findByFullNameLikeIgnoreCase(nameWildcard).stream()
        .map(this::map)
        .toList();
  }

  @Override
  public List<Project> findByLeaderIdLike(String leaderIdWildcard) {
    return mongoProjectProvider.findByLeaderIdLikeIgnoreCase(leaderIdWildcard).stream()
        .map(this::map)
        .toList();
  }

  @Override
  public void update(Project project) {
    mongoProjectProvider
        .findById(project.getId().getInternal())
        .map(
            doc -> {
              doc.setFullName(project.getName().getFullName());
              doc.setShortName(project.getName().getShortName());
              doc.setEndDate(project.getPeriod().getEnd());
              doc.setStartDate(project.getPeriod().getStart());
              project.getLeader().map(ProjectLeader::getInternalDeep).ifPresent(doc::setLeaderId);
              doc.setTags(
                  project.getTags().stream().map(Tag::getValue).collect(Collectors.toSet()));
              return doc;
            })
        .ifPresent(mongoProjectProvider::save);
  }

  @Override
  public void delete(ProjectId projectId) {
    mongoProjectProvider.deleteById(projectId.getInternal());
  }

  private Project map(ProjectDocument doc) {
    return Project.builder()
        .id(ProjectId.valueOf(doc.getId()))
        .leader(
            Optional.ofNullable(doc.getLeaderId())
                .map(leaderId -> new ProjectLeader(UserId.valueOf(leaderId)))
                .orElse(null))
        .name(ProjectName.of(doc.getFullName(), doc.getShortName()))
        .period(DatePeriod.builder().start(doc.getStartDate()).end(doc.getEndDate()).build())
        .build();
  }
}
