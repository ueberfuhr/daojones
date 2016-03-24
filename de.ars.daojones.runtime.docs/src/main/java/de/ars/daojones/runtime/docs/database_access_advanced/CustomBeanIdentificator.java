package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.identification.BeanIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

public class CustomBeanIdentificator implements BeanIdentificator {
  @Override
  public Identificator getIdentificator(final BeanModel model, final Object bean)
          throws DataAccessException, ConfigurationException {
    final Identificator identificator = null;
    // .. fetch identificator
    return identificator;
  }

  @Override
  public void setIdentificator(final BeanModel model, final Object bean,
          final Identificator identificator) throws DataAccessException,
          ConfigurationException {
    // ... store identificator
  }
}
