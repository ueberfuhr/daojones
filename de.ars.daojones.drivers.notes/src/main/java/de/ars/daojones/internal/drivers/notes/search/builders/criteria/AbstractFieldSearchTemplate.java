package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.context.BeanModelHelper;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.query.AbstractFieldSearchCriterion;

public abstract class AbstractFieldSearchTemplate<T extends AbstractFieldSearchCriterion> extends AbstractTemplate<T> {

  @Override
  protected Object[] getParameters( final QueryContext<T> context ) throws QueryLanguageException {
    // Insert field name as first parameter
    final Object[] children = provideChildren() ? super.getParameters( context ) : new Object[0];
    final Object[] result = new Object[children.length + 1];
    result[0] = resolveField( context );
    System.arraycopy( children, 0, result, 1, children.length );
    return result;
  }

  protected String resolveField( final QueryContext<T> context ) throws QueryLanguageException {
    final T criterion = context.getCriterion();
    final String field = criterion.getField();
    final Bean model = context.getModel();
    try {
      return BeanModelHelper.resolveFieldName( model, field );
    } catch ( final ConfigurationException e ) {
      throw new QueryLanguageException( criterion, model, e );
    }
  }

  /**
   * Returns a flag to decide whether the children of the crition have to be
   * used as parameters or not. Default value is <tt>false</tt>.
   * 
   * @return the flag to decide whether the children of the crition have to be
   *         used as parameters or not
   */
  protected boolean provideChildren() {
    return false;
  }

}
