package de.ars.daojones.runtime.query;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A criterion that negotiates another one.<br/>
 * <br/>
 * <b>Please note:</b> This criterion does not provide any public constructor.
 * Use {@link #not(SearchCriterion)} instead. This method has the effect that
 * doubled negotiation is not possible, such criteria are automatically
 * unwrapped.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 1.0
 */
public class Negation extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  private final SearchCriterion internal;

  /**
   * Creates an instance.
   * 
   * @param internal
   *          the negotiated criterion
   * @throws NullPointerException
   *           if internal is <tt>null</tt>
   */
  private Negation( final SearchCriterion internal ) {
    super();
    if ( null == internal ) {
      throw new NullPointerException();
    }
    this.internal = internal;
  }

  /**
   * Negotiates another search criterion.
   * 
   * @param criterion
   *          the criterion
   * @return the negotiated criterion
   * @throws NullPointerException
   *           if criterion is <tt>null</tt>
   */
  public static SearchCriterion not( final SearchCriterion criterion ) {
    final SearchCriterion sc = AbstractSearchCriterion.unwrap( criterion );
    if ( sc instanceof Negation ) {
      return ( ( Negation ) sc ).internal;
    } else {
      return new Negation( sc );
    }
  }

  /**
   * Returns the internal search criterion.
   * 
   * @return the internal search criterion
   */
  public SearchCriterion getInternal() {
    return internal;
  }

  @Override
  public int hashCode() {
    // NOT(NOT(A)) == A
    return -internal.hashCode();
  }

  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return internal instanceof Negation && ( ( Negation ) internal ).internal.equals( obj );
    }
    final Negation other = ( Negation ) obj;
    if ( internal == null ) {
      if ( other.internal != null ) {
        return false;
      }
    } else if ( !internal.equals( other.internal ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( " NOT [" ).append( internal.toString() ).append( "] " );
    return builder.toString();
  }

  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException, DataAccessException {
    return !getInternal().matches( ctx, bean );
  }

  @Override
  protected void acceptChildren( final SearchCriterionVisitor visitor ) {
    super.acceptChildren( visitor );
    getInternal().accept( visitor );
  }

}
