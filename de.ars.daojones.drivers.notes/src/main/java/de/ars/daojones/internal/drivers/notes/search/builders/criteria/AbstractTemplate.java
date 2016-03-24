package de.ars.daojones.internal.drivers.notes.search.builders.criteria;

import java.util.LinkedList;
import java.util.List;

import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterionVisitor;

abstract class AbstractTemplate<T extends SearchCriterion> implements QueryLanguageBuilder<T> {

  private static final Messages templates = Messages.create( "search.builders.templates" );

  /**
   * Returns the value of the template with the place holders replaced by the
   * parameter values. Parameters are of type search criterion. <b>Please
   * note:</b> In case of creating custom templates for custom criteria, you
   * should overwrite this method and refer to your custom resource bundle.
   * 
   * @param key
   *          the key
   * @param params
   *          the parameters
   * @return the value of the template
   */
  protected String getTemplate( final String key, final Object... params ) {
    return AbstractTemplate.templates.get( key, params );
  }

  private String getTemplate( final String key, final QueryContext<T> context, final Object... params )
          throws QueryLanguageException {
    final Object[] paramObjects = new Object[params.length];
    for ( int i = 0; i < params.length; i++ ) {
      final Object param = params[i];
      if ( param instanceof SearchCriterion ) {
        final SearchCriterion searchCriterion = ( SearchCriterion ) param;
        final StringBuilder sb = new StringBuilder();
        context.getLanguage().createQuery( sb, context.getQuery().clone().only( searchCriterion ), context.getModel() );
        paramObjects[i] = sb;
      } else {
        paramObjects[i] = param;
      }
    }
    return getTemplate( key, paramObjects );
  }

  @Override
  public void createQuery( final StringBuilder buffer, final QueryContext<T> context ) throws QueryLanguageException {
    final Object[] params = getParameters( context );
    final Object[] parameters = null != params ? params : new SearchCriterion[0];
    final String key = getKey( context );
    final String template = getTemplate( key, context, parameters );
    buffer.append( template );
  }

  /**
   * Returns the key within the templates file.
   * 
   * @param context
   *          the query context
   * @return the key within the templates file
   * @throws QueryLanguageException
   */
  protected abstract String getKey( final QueryContext<T> context ) throws QueryLanguageException;

  @Override
  public SearchType getSearchType() {
    return null;
  }

  /**
   * Returns the parameters. If there isn't any parameter, <tt>null</tt> or an
   * empty array can be returned. The implementation of this method uses a
   * {@link SearchCriterionVisitor} to find the children of the criterion and
   * return them as parameters. If this behaviour is not applicable to the
   * template, this method must be overwritten.
   * 
   * @param context
   *          the query context
   * @return the parameters
   * @throws QueryLanguageException
   */
  protected Object[] getParameters( final QueryContext<T> context ) throws QueryLanguageException {
    final ChildrenCollector collector = new ChildrenCollector( context.getCriterion() );
    return collector.getChildren();
  }

  // This class is not thread-safe
  private static class ChildrenCollector extends SearchCriterionVisitor {

    private final SearchCriterion criterion;
    private final List<SearchCriterion> children = new LinkedList<SearchCriterion>();

    public ChildrenCollector( final SearchCriterion criterion ) {
      super();
      this.criterion = criterion;
    }

    public SearchCriterion[] getChildren() {
      children.clear();
      criterion.accept( this );
      return children.toArray( new SearchCriterion[children.size()] );
    }

    @Override
    public boolean preVisit( final SearchCriterion criterion ) {
      if ( this.criterion == criterion ) {
        return true;
      } else {
        children.add( criterion );
        return false;
      }
    }

  }

}
