package com.jbs.pm.e2e;

import com.aegon.util.lang.DatePeriod;
import com.aegon.util.lang.SimpleId;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloMutationCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx3.Rx3Apollo;
import com.jbs.pm.AllProjectsQuery;
import com.jbs.pm.QueriedProjectsQuery;
import com.jbs.pm.UpdateBasicFieldsMutation;
import com.jbs.pm.domain.model.project.ProjectBasicFieldsChange;
import com.jbs.pm.domain.model.project.ProjectId;
import com.jbs.pm.domain.model.project.ProjectLeader;
import com.jbs.pm.domain.model.project.ProjectName;
import com.jbs.pm.domain.model.project.ProjectQuery;
import java.time.LocalDate;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class PmTestClient {

  private final ApolloClient apolloClient;

  public PmTestClient(int localServerPort) {
    final HttpUrl url = HttpUrl.parse(String.format("http://localhost:%s/api", localServerPort));
    this.apolloClient =
        ApolloClient.builder().okHttpClient(new OkHttpClient()).serverUrl(url).build();
  }

  public List<AllProjectsQuery.FindAllProject> findAllProjects() {
    final ApolloQueryCall<AllProjectsQuery.Data> query = apolloClient.query(new AllProjectsQuery());
    final Response<AllProjectsQuery.Data> dataResponse = Rx3Apollo.from(query).blockingSingle();
    return dataResponse.getData().findAllProjects();
  }

  public UpdateBasicFieldsMutation.UpdateProject updateBasicFields(
      ProjectId projectId, ProjectBasicFieldsChange change) {
    final String shortName = change.getProjectName().map(ProjectName::getShortName).orElse(null);
    final String fullName = change.getProjectName().map(ProjectName::getShortName).orElse(null);
    final UpdateBasicFieldsMutation mutation =
        UpdateBasicFieldsMutation.builder()
            .projectId(projectId.getInternal())
            .shortname(shortName)
            .fullname(fullName)
            .leaderId(change.getProjectLeader().map(ProjectLeader::getInternalDeep).orElse(null))
            .endDate(change.getDatePeriod().map(d -> d.getEnd().toString()).orElse(null))
            .startDate(change.getDatePeriod().map(d -> d.getStart().toString()).orElse(null))
            .build();
    final ApolloMutationCall<UpdateBasicFieldsMutation.Data> mutate = apolloClient.mutate(mutation);
    final Response<UpdateBasicFieldsMutation.Data> dataResponse =
        Rx3Apollo.from(mutate).blockingSingle();
    return dataResponse.getData().updateProject();
  }

  public List<QueriedProjectsQuery.FindProject> queryProjects(ProjectQuery query) {
    final QueriedProjectsQuery projectQuery =
        QueriedProjectsQuery.builder()
            .projectId(query.getId().map(SimpleId::getInternal).orElse(null))
            .endDate(
                query.getPeriod().map(DatePeriod::getEnd).map(LocalDate::toString).orElse(null))
            .startDate(
                query.getPeriod().map(DatePeriod::getStart).map(LocalDate::toString).orElse(null))
            .nameWildcard(query.getNameWildcard().orElse(null))
            .leaderIdWildcard(query.getLeaderIdWildcard().orElse(null))
            .build();

    final ApolloQueryCall<QueriedProjectsQuery.Data> queryResult = apolloClient.query(projectQuery);
    final Response<QueriedProjectsQuery.Data> dataResponse =
        Rx3Apollo.from(queryResult).blockingSingle();
    return dataResponse.getData().findProjects();
  }
}
