package com.jbs.pm.domain.service.project;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.domain.model.project.CreateProjectRequest;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectQuery;
import com.jbs.pm.domain.repository.ProjectRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@RequiredArgsConstructor
@Slf4j
public class ProjectComponentImpl implements ProjectComponent {

  private final ProjectRepository repository;

  private final Collection<ProjectChangeHandler> changeHandlers;

  @Override
  public Project get(ProjectId id) {
    return repository.findById(id).orElseThrow(() -> ProjectNotFoundException.error(id));
  }

  @Override
  public Collection<Project> query(ProjectQuery query) {
    if (query.isEmpty()) {
      return repository.findAll();
    }
    return query
        .getId()
        .flatMap(repository::findById)
        .map(Collections::singleton)
        .orElseGet(
            () -> {
              Set<Project> result = new HashSet<>();
              query
                  .getNameWildcard()
                  .ifPresent(
                      nameWildcard -> {
                        val nameQueryProjects = repository.findByNameLike(nameWildcard);
                        addOrRetainCollections(result, nameQueryProjects);
                      });
              query
                  .getLeaderIdWildcard()
                  .ifPresent(
                      leaderIdWildcard -> {
                        val leaderIdQueryProjects = repository.findByLeaderIdLike(leaderIdWildcard);
                        addOrRetainCollections(result, leaderIdQueryProjects);
                      });

              return query.getPeriod().map(period -> withinPeriod(result, period)).orElse(result);
            });
  }

  @Override
  public ProjectId add(CreateProjectRequest request) {
    return repository.add(request.getName(), request.getPeriod());
  }

  @Override
  public void update(ProjectChange change) {
    changeHandlers.stream()
        .filter(handler -> handler.canHandleChange(change))
        .findFirst()
        .ifPresentOrElse(
            handler -> handler.handleChange(change),
            () -> log.warn("Project change {} is not supported", change));
  }

  @Override
  public void delete(ProjectId projectId) {
    repository.delete(projectId);
  }

  private void addOrRetainCollections(Collection<Project> target, Collection<Project> from) {
    if (target.isEmpty()) {
      target.addAll(from);
    } else {
      target.retainAll(from);
    }
  }

  private boolean filterWithinPeriod(Project project, DatePeriod period) {
    if (period == null) {
      return true;
    }
    val greaterOrEqualThanStart =
        period.getStart().isBefore(project.getPeriod().getStart())
            || period.getStart().equals(project.getPeriod().getStart());
    val lesserOrEqualThanEnd =
        period.getEnd().isAfter(project.getPeriod().getEnd())
            || period.getEnd().equals(project.getPeriod().getEnd());

    return greaterOrEqualThanStart && lesserOrEqualThanEnd;
  }

  private Set<Project> withinPeriod(Set<Project> result, DatePeriod period) {
    return (result.isEmpty() ? repository.findAll() : result)
        .stream()
            .filter(project -> filterWithinPeriod(project, period))
            .collect(Collectors.toSet());
  }
}
