package de.ars.daojones.runtime.query;

import java.text.MessageFormat;

/**
 * A {@link SearchCriterion} negotiating another {@link SearchCriterion}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NegativeSearchCriterion implements SearchCriterion {

  private static final long serialVersionUID = 234043826896592444L;
  private final SearchCriterion c1;

  /**
   * Creates an instance.
   * 
   * @param c1
   *          the {@link SearchCriterion}
   */
  public NegativeSearchCriterion( final SearchCriterion c1 ) {
    super();
    this.c1 = c1;
  }

  /**
   * @see de.ars.daojones.runtime.query.SearchCriterion#toQuery(de.ars.daojones.runtime.query.TemplateManager,
   *      de.ars.daojones.runtime.query.VariableResolver)
   */
  public String toQuery( TemplateManager templateManager,
      final VariableResolver resolver ) throws VariableResolvingException {
    return new MessageFormat( templateManager.getTemplate( "logical.not" ) )
        .format( new Object[] { c1.toQuery( templateManager, resolver ) } );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ( ( c1 == null ) ? 0 : c1.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    final NegativeSearchCriterion other = ( NegativeSearchCriterion ) obj;
    if ( c1 == null ) {
      if ( other.c1 != null )
        return false;
    } else if ( !c1.equals( other.c1 ) )
      return false;
    return true;
  }

}
