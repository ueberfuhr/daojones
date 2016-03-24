package de.ars.daojones.runtime.query;

/**
 * A {@link SearchCriterion} to search for empty fields in the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class IsEmptySearchCriterion extends PropertyHandlingCriterion {

  private static final long serialVersionUID = -3757293911968633558L;

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   */
  public IsEmptySearchCriterion( final String property ) {
    super( property );
  }

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   * @param resolveColumn
   *          a flag indicating whether the property is a bean property (true)
   *          or an original column name
   */
  public IsEmptySearchCriterion( final String property, boolean resolveColumn ) {
    super( property, resolveColumn );
  }

  /**
   * @see PropertyHandlingCriterion#toQuery(TemplateManager, VariableResolver,
   *      String)
   */
  @Override
  protected String toQuery( TemplateManager templateManager,
      VariableResolver resolver, String column )
      throws VariableResolvingException {
    return templateManager.getTemplate( "empty" )
        .replaceAll( "\\{0\\}", column );
  }

}
