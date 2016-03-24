package de.ars.daojones.internal.runtime.test.junit;

import de.ars.daojones.runtime.context.DaoJonesContext;

public interface Injector {

  Object getInjectedValue( InjectionContext ic );

  interface InjectionContext {
    DaoJonesContext getContext();

    String getApplication();
  }

}
