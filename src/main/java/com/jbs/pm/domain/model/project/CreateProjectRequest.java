package com.jbs.pm.domain.model.project;

import com.aegon.util.lang.DatePeriod;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateProjectRequest {

  ProjectName name;

  DatePeriod period;
}
