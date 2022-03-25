package com.jbs.pm.infrastructure;

import com.aegon.util.components.ApiComponent;
import com.aegon.util.components.ApiComponentFactory;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class ApiComponentProvider extends com.aegon.util.components.ApiComponentProvider {

  public ApiComponentProvider(Collection<ApiComponentFactory<? extends ApiComponent>> factories) {
    super(factories);
  }
}
