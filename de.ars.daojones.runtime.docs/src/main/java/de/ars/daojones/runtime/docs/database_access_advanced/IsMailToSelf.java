package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.query.AbstractSearchCriterion;

public class IsMailToSelf extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean matches(final ApplicationContext ctx, final Object bean)
          throws ConfigurationException, FieldAccessException, DataAccessException {
    final String sender = (String) getFieldValue(ctx, bean, "sender");
    final String receiver = (String) getFieldValue(ctx, bean, "receiver");
    return sender.equalsIgnoreCase(receiver);
  }

}
