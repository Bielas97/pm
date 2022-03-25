package com.jbs.pm.domain.service.project;

import com.aegon.util.components.ApiComponentProxy;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

@Slf4j
public class ProjectComponentProxy extends ApiComponentProxy<ProjectComponent> {

  private final StopWatch stopWatch = new StopWatch();

  public ProjectComponentProxy(ProjectComponent bean) {
    super(bean);
  }

  @Override
  protected void onError(Throwable throwable) {
    // TODO send errors to metrics
    System.out.println("error raised " + throwable.getMessage());
  }

  @Override
  @SuppressWarnings("PMD")
  protected void afterExecution(Object proxy, Method method, Object[] args, Object methodResult) {
    stopWatch.stop();
    log.info(
        "Execution of method {}#{} took {} miliseconds",
        method.getDeclaringClass().getSimpleName(),
        method.getName(),
        stopWatch.getTime(TimeUnit.MILLISECONDS));
    stopWatch.reset();
  }

  @Override
  protected void beforeExecution(Object o, Method method, Object[] args) {
    stopWatch.start();
  }
}
