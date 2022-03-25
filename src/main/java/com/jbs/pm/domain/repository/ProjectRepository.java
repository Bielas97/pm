package com.jbs.pm.domain.repository;

import com.aegon.util.lang.DatePeriod;
import com.jbs.pm.domain.model.project.Project;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectName;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

  Optional<Project> findById(ProjectId projectId);

  List<Project> findAll();

  ProjectId add(ProjectName name, DatePeriod period);

  List<Project> findByNameLike(String nameWildcard);

  List<Project> findByLeaderIdLike(String leaderIdWildcard);

  void update(Project project);

  void delete(ProjectId projectId);
}
