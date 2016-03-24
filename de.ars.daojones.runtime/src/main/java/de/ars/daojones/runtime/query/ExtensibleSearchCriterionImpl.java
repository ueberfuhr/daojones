package de.ars.daojones.runtime.query;

import java.io.Serializable;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

class ExtensibleSearchCriterionImpl implements ExtensibleSearchCriterion, Serializable {

  private static final long serialVersionUID = 1L;

  private final SearchCriterion original;

  public ExtensibleSearchCriterionImpl( final SearchCriterion original ) {
    super();
    this.original = original;
  }

  protected SearchCriterion getOriginal() {
    return original;
  }

  @Override
  public SearchCriterionCombination and() {
    return new SearchCriterionCombinationImpl( compile(), LogicalCombinationType.AND );
  }

  @Override
  public SearchCriterionCombination or() {
    return new SearchCriterionCombinationImpl( compile(), LogicalCombinationType.OR );
  }

  // Used by the Visitor pattern (SearchCriterionVisitor)
  public SearchCriterion compile() {
    return original;
  }

  @Override
  public int hashCode() {
    return compile().hashCode();
  }

  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    final ExtensibleSearchCriterionImpl other = ( ExtensibleSearchCriterionImpl ) obj;
    return compile().equals( other.compile() );
  }

  @Override
  public String toString() {
    return compile().toString();
  }

  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException, DataAccessException {
    return compile().matches( ctx, bean );
  }

  @Override
  public void accept( final SearchCriterionVisitor visitor ) {
    compile().accept( visitor );
  }

  @Override
  public SearchCriterion getUnwrapped() {
    return original;
  }

}