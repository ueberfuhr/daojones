package de.ars.daojones.internal.drivers.notes.search.builders.comparisons;

import de.ars.daojones.drivers.notes.search.ComparisonBuilder;
import de.ars.daojones.drivers.notes.search.Encoder;
import de.ars.daojones.drivers.notes.search.Encoder.EncoderContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageBuilder.QueryContext;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.context.BeanModelHelper;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.query.FieldComparison;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterionVisitor;

abstract class AbstractComparisonBuilder<X> implements ComparisonBuilder<X> {

  private static final Messages templates = Messages.create( "search.builders.templates" );

  /**
   * Returns the value of the template with the place holders replaced by the
   * parameter values. Parameters are of type search criterion. <b>Please
   * note:</b> In case of creating custom templates for custom criteria, you
   * should overwrite this method and refer to your custom resource bundle.
   * 
   * @param context
   *          the context
   * @param key
   *          the key
   * @param params
   *          the parameters
   * @return the value of the template
   */
  protected String getTemplate( final ComparisonContext<X> context, final String key, final Object... params ) {
    final String keyPrefix = context.getQueryContext().getSearchType().getValue();
    return AbstractComparisonBuilder.templates.get( keyPrefix.concat( "." ).concat( key ), params );
  }

  private String getTemplateRenderingCriteria( final ComparisonContext<X> context, final String key,
          final Object... params ) throws QueryLanguageException {
    final Object[] paramObjects = new Object[params.length];
    for ( int i = 0; i < params.length; i++ ) {
      final Object param = params[i];
      if ( param instanceof SearchCriterion ) {
        final SearchCriterion searchCriterion = ( SearchCriterion ) param;
        final StringBuilder sb = new StringBuilder();
        final QueryContext<FieldComparison<X>> qc = context.getQueryContext();
        qc.getLanguage().createQuery( sb, qc.getQuery().clone().only( searchCriterion ), qc.getModel() );
        paramObjects[i] = sb;
      } else {
        paramObjects[i] = param;
      }
    }
    return getTemplate( context, key, paramObjects );
  }

  @Override
  public void createQuery( final StringBuilder buffer, final ComparisonContext<X> context )
          throws QueryLanguageException {
    final Object[] parameters = getParameters( context );
    final String key = getKey( context );
    final String template = getTemplateRenderingCriteria( context, key, parameters );
    buffer.append( template );
  }

  /**
   * Returns the key within the templates file.
   * 
   * @param criterion
   *          the search criterion
   * @return the key within the templates file
   * @throws QueryLanguageException
   */
  protected abstract String getKey( final ComparisonContext<X> context ) throws QueryLanguageException;

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
   *          the context
   * @return the parameters
   * @throws QueryLanguageException
   */
  protected Object[] getParameters( final ComparisonContext<X> context ) throws QueryLanguageException {
    final QueryContext<FieldComparison<X>> qc = context.getQueryContext();
    final FieldComparison<X> criterion = qc.getCriterion();
    final Bean model = qc.getModel();
    try {
      final String fieldId = criterion.getField();
      final String fieldName = BeanModelHelper.resolveFieldName( model, fieldId );
      final X value = criterion.getValue();
      final Encoder<X> encoder = getEncoder( context );
      final EncoderContext<X> encoderContext = new EncoderContext<X>() {

        @Override
        public Bean getModel() {
          return model;
        }

        @Override
        public FieldComparison<X> getCriterion() {
          return criterion;
        }

        @Override
        public SearchType getSearchType() {
          return qc.getSearchType();
        }
      };
      final String value$;
      if ( null == value ) {
        value$ = encoder.encodeNull( encoderContext );
      } else {
        value$ = encoder.encodeLiteral( encoderContext, context, value );
      }
      return new Object[] { fieldName, value$ };
    } catch ( final ConfigurationException e ) {
      throw new QueryLanguageException( criterion, model, e );
    }
  }

  @SuppressWarnings( "unchecked" )
  protected Encoder<X> getEncoder( final ComparisonContext<X> context ) throws QueryLanguageException {
    return context.getEncoder( ( Class<X> ) getFieldType() );
  }

}
