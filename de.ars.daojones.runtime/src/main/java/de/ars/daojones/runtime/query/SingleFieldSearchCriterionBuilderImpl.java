package de.ars.daojones.runtime.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

class SingleFieldSearchCriterionBuilderImpl implements SingleFieldSearchCriterionBuilder {

  private final String field;

  public SingleFieldSearchCriterionBuilderImpl( final String field ) {
    super();
    this.field = field;
  }

  protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
    return c;
  }

  /* *********************************
   *   I M P L E M E N T A T I O N   *
   ********************************* */

  @Override
  public SingleFieldSearchCriterionLimitedCombination isEmpty() {
    final SearchCriterion isEmptyCriterion = new IsEmpty( field );
    final SearchCriterion c = handleSearchCriterion( isEmptyCriterion );
    return new SinglePropertySearchCriterionLimitedCombinationImpl( c, this );
  }

  @Override
  public StringComparator asString() {
    return new StringComparatorImpl( true, this );
  }

  @Override
  public StringComparator asCaseInsensitiveString() {
    return new StringComparatorImpl( false, this );
  }

  @Override
  public NumberComparator asNumber() {
    return new NumberComparatorImpl( this );
  }

  @Override
  public DateComparator asDate() {
    return new DateComparatorImpl( false, this );
  }

  @Override
  public DateComparator asDateTime() {
    return new DateComparatorImpl( true, this );
  }

  @Override
  public BooleanComparator asBoolean() {
    return new BooleanComparatorImpl( this );
  }

  @Override
  public <E> CollectionComparator<E> asCollectionOf( final Class<E> c ) {
    return new CollectionComparatorImpl<E>( this );
  }

  /* *******************************
   *   I N N E R   C L A S S E S   *
   ******************************* */

  private static class SinglePropertySearchCriterionLimitedCombinationImpl extends ExtensibleSearchCriterionImpl
          implements SingleFieldSearchCriterionLimitedCombination {

    private static final long serialVersionUID = 1L;

    private final SingleFieldSearchCriterionBuilderImpl context;

    public SinglePropertySearchCriterionLimitedCombinationImpl( final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original );
      this.context = context;
    }

    /**
     * @return the context
     */
    protected SingleFieldSearchCriterionBuilderImpl getContext() {
      return context;
    }

    @Override
    public SingleFieldSearchCriterionBuilderOrSearchCriterionCombination or() {
      return new SinglePropertySearchCriterionBuilderOrSearchCriterionCombinationImpl( this.compile(),
              LogicalCombinationType.OR, getContext() );
    }
  }

  private static class SinglePropertySearchCriterionBuilderOrSearchCriterionCombinationImpl extends
          SearchCriterionCombinationImpl implements SingleFieldSearchCriterionBuilderOrSearchCriterionCombination {

    private final SingleFieldSearchCriterionBuilderImpl context;

    public SinglePropertySearchCriterionBuilderOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( SearchCriterionBuilder.unwrap( c ), combination );
      this.context = context;
    }

    protected SingleFieldSearchCriterionBuilderImpl getContext() {
      return context;
    }

    @Override
    public SingleFieldSearchCriterionLimitedCombination isEmpty() {
      final SearchCriterion isEmptyCriterion = new IsEmpty( getContext().field );
      final SearchCriterion handledEmptyCriterion = getContext().handleSearchCriterion( isEmptyCriterion );
      final SearchCriterion sc = new LogicalCombination( getCriterion(), getCombination(), handledEmptyCriterion );
      return new SinglePropertySearchCriterionLimitedCombinationImpl( sc, getContext() );
    }

    @Override
    public StringComparator asString() {
      return new StringComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), true, getContext() );
    }

    @Override
    public StringComparator asCaseInsensitiveString() {
      return new StringComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), false,
              getContext() );
    }

    @Override
    public NumberComparator asNumber() {
      return new NumberComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), getContext() );
    }

    @Override
    public DateComparator asDate() {
      return new DateComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), false, getContext() );
    }

    @Override
    public DateComparator asDateTime() {
      return new DateComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), true, getContext() );
    }

    @Override
    public BooleanComparator asBoolean() {
      return new BooleanComparatorOrSearchCriterionCombinationImpl( getCriterion(), getCombination(), getContext() );
    }

    @Override
    public <E> CollectionComparator<E> asCollectionOf( final Class<E> c ) {
      return new CollectionComparatorOrSearchCriterionCombinationImpl<E>( getCriterion(), getCombination(),
              getContext() );
    }

  }

  // COMMON ABSTRACT DATATYPE INDEPENDENT CLASSES

  private static abstract class DatatypeSpecificComparatorCombinationImpl<Type> extends ExtensibleSearchCriterionImpl
          implements DatatypeSpecificComparatorCombination<Type> {

    private static final long serialVersionUID = 1L;

    private final SingleFieldSearchCriterionBuilderImpl context;

    public DatatypeSpecificComparatorCombinationImpl( final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original );
      this.context = context;
    }

    protected SingleFieldSearchCriterionBuilderImpl getContext() {
      return context;
    }

    @Override
    public abstract DatatypeSpecificComparatorOrSearchCriterionCombination<Type> and();

    @Override
    public abstract DatatypeSpecificComparatorOrSearchCriterionCombination<Type> or();

  }

  private static abstract class DatatypeSpecificComparatorImpl<Type> implements DatatypeSpecificComparator<Type> {

    private final SingleFieldSearchCriterionBuilderImpl context;

    public DatatypeSpecificComparatorImpl( final SingleFieldSearchCriterionBuilderImpl context ) {
      super();
      this.context = context;
    }

    protected SingleFieldSearchCriterionBuilderImpl getContext() {
      return context;
    }
  }

  // STRING SPECIFIC CLASSES

  private static class StringComparatorOrSearchCriterionCombinationImpl extends SearchCriterionCombinationImpl
          implements StringComparatorOrSearchCriterionCombination {

    private final StringComparatorImpl delegate;

    public StringComparatorOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final boolean caseSensitive,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( c, combination );
      delegate = new StringComparatorImpl( caseSensitive, context ) {

        @Override
        protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
          return new LogicalCombination( getCriterion(), getCombination(), super.handleSearchCriterion( c ) );
        }

      };
    }

    @Override
    public StringComparatorCombination isEqualTo( final String value ) {
      return delegate.isEqualTo( value );
    }

    @Override
    public StringComparatorCombination startsWith( final String value ) {
      return delegate.startsWith( value );
    }

    @Override
    public StringComparatorCombination endsWith( final String value ) {
      return delegate.endsWith( value );
    }

    @Override
    public StringComparatorCombination contains( final String value ) {
      return delegate.contains( value );
    }

    @Override
    public StringComparatorCombination isLike( final String value ) {
      return delegate.isLike( value );
    }

  }

  private static class StringComparatorCombinationImpl extends DatatypeSpecificComparatorCombinationImpl<String>
          implements StringComparatorCombination {

    private static final long serialVersionUID = 1L;
    private final boolean caseSensitive;

    public StringComparatorCombinationImpl( final boolean caseSensitive, final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original, context );
      this.caseSensitive = caseSensitive;
    }

    protected boolean isCaseSensitive() {
      return caseSensitive;
    }

    @Override
    public StringComparatorOrSearchCriterionCombination and() {
      return new StringComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.AND,
              isCaseSensitive(), getContext() );
    }

    @Override
    public StringComparatorOrSearchCriterionCombination or() {
      return new StringComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.OR,
              isCaseSensitive(), getContext() );
    }

  }

  private static class StringComparatorImpl extends DatatypeSpecificComparatorImpl<String> implements StringComparator {

    private final boolean caseSensitive;

    public StringComparatorImpl( final boolean caseSensitive, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( context );
      this.caseSensitive = caseSensitive;
    }

    protected boolean isCaseSensitive() {
      return caseSensitive;
    }

    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return getContext().handleSearchCriterion( c );
    }

    private StringComparatorCombination create( final String value, final Comparison<String> comparison ) {
      final SearchCriterion scComparison = new FieldComparison<String>( getContext().field, comparison, value );
      final SearchCriterion handledScComparison = handleSearchCriterion( scComparison );
      return new StringComparatorCombinationImpl( isCaseSensitive(), handledScComparison, getContext() );
    }

    @Override
    public StringComparatorCombination isEqualTo( final String value ) {
      return create( value, isCaseSensitive() ? StringComparison.EQUALS : StringComparison.EQUALS_IGNORECASE );
    }

    @Override
    public StringComparatorCombination startsWith( final String value ) {
      return create( value, isCaseSensitive() ? StringComparison.STARTSWITH : StringComparison.STARTSWITH_IGNORECASE );
    }

    @Override
    public StringComparatorCombination endsWith( final String value ) {
      return create( value, isCaseSensitive() ? StringComparison.ENDSWITH : StringComparison.ENDSWITH_IGNORECASE );
    }

    @Override
    public StringComparatorCombination contains( final String value ) {
      return create( value, isCaseSensitive() ? StringComparison.CONTAINS : StringComparison.CONTAINS_IGNORECASE );
    }

    @Override
    public StringComparatorCombination isLike( final String value ) {
      return create( value, isCaseSensitive() ? StringComparison.LIKE : StringComparison.LIKE_IGNORECASE );
    }

  }

  // NUMBER SPECIFIC CLASSES

  private static class NumberComparatorOrSearchCriterionCombinationImpl extends SearchCriterionCombinationImpl
          implements NumberComparatorOrSearchCriterionCombination {

    private final NumberComparatorImpl delegate;

    public NumberComparatorOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( c, combination );
      delegate = new NumberComparatorImpl(

      context ) {

        @Override
        protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
          return new LogicalCombination( getCriterion(), getCombination(), super.handleSearchCriterion( c ) );
        }

      };
    }

    @Override
    public NumberComparatorCombination isEqualTo( final Number value ) {
      return delegate.isEqualTo( value );
    }

    @Override
    public NumberComparatorCombination isLowerThan( final Number value ) {
      return delegate.isLowerThan( value );
    }

    @Override
    public NumberComparatorCombination isLowerThanOrEqualTo( final Number value ) {
      return delegate.isLowerThanOrEqualTo( value );
    }

    @Override
    public NumberComparatorCombination isGreaterThan( final Number value ) {
      return delegate.isGreaterThan( value );
    }

    @Override
    public NumberComparatorCombination isGreaterThanOrEqualTo( final Number value ) {
      return delegate.isGreaterThanOrEqualTo( value );
    }

  }

  private static class NumberComparatorCombinationImpl extends DatatypeSpecificComparatorCombinationImpl<Number>
          implements NumberComparatorCombination {

    private static final long serialVersionUID = 1L;

    public NumberComparatorCombinationImpl( final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original, context );
    }

    @Override
    public NumberComparatorOrSearchCriterionCombination and() {
      return new NumberComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.AND,
              getContext() );
    }

    @Override
    public NumberComparatorOrSearchCriterionCombination or() {
      return new NumberComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.OR,
              getContext() );
    }

  }

  private static class NumberComparatorImpl extends DatatypeSpecificComparatorImpl<Number> implements NumberComparator {

    public NumberComparatorImpl( final SingleFieldSearchCriterionBuilderImpl context ) {
      super( context );
    }

    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return getContext().handleSearchCriterion( c );
    }

    private NumberComparatorCombination create( final Number value, final Comparison<Number>... comparisons ) {
      final Collection<SearchCriterion> criteria = new LinkedList<SearchCriterion>();
      for ( final Comparison<Number> comparison : comparisons ) {
        final SearchCriterion scComparison = new FieldComparison<Number>( getContext().field, comparison, value );
        final SearchCriterion handledScComparison = handleSearchCriterion( scComparison );
        criteria.add( handledScComparison );
      }
      final SearchCriterion[] criteriaArrays = criteria.toArray( new SearchCriterion[criteria.size()] );
      final SearchCriterion sc = LogicalCombination.toSearchCriterion( LogicalCombinationType.OR, criteriaArrays );
      return new NumberComparatorCombinationImpl( sc, getContext() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public NumberComparatorCombination isEqualTo( final Number value ) {
      return create( value, NumberComparison.EQUALS );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public NumberComparatorCombination isLowerThan( final Number value ) {
      return create( value, NumberComparison.LOWERTHAN );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public NumberComparatorCombination isLowerThanOrEqualTo( final Number value ) {
      return create( value, NumberComparison.EQUALS, NumberComparison.LOWERTHAN );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public NumberComparatorCombination isGreaterThan( final Number value ) {
      return create( value, NumberComparison.GREATERTHAN );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public NumberComparatorCombination isGreaterThanOrEqualTo( final Number value ) {
      return create( value, NumberComparison.EQUALS, NumberComparison.GREATERTHAN );
    }

  }

  //DATE SPECIFIC CLASSES

  private static class DateComparatorOrSearchCriterionCombinationImpl extends SearchCriterionCombinationImpl implements
          DateComparatorOrSearchCriterionCombination {

    private final DateComparatorImpl delegate;

    public DateComparatorOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final boolean includeTime,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( c, combination );
      delegate = new DateComparatorImpl( includeTime, context ) {

        @Override
        protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
          return new LogicalCombination( getCriterion(), getCombination(), super.handleSearchCriterion( c ) );
        }

      };
    }

    @Override
    public DateComparatorCombination isEqualTo( final Date value ) {
      return delegate.isEqualTo( value );
    }

    @Override
    public DateComparatorCombination isAfter( final Date value ) {
      return delegate.isAfter( value );
    }

    @Override
    public DateComparatorCombination isAfterOrEqualTo( final Date value ) {
      return delegate.isAfterOrEqualTo( value );
    }

    @Override
    public DateComparatorCombination isBefore( final Date value ) {
      return delegate.isBefore( value );
    }

    @Override
    public DateComparatorCombination isBeforeOrEqualTo( final Date value ) {
      return delegate.isBeforeOrEqualTo( value );
    }

  }

  private static class DateComparatorCombinationImpl extends DatatypeSpecificComparatorCombinationImpl<Date> implements
          DateComparatorCombination {

    private static final long serialVersionUID = 1L;
    private final boolean includeTime;

    public DateComparatorCombinationImpl( final boolean includeTime, final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original, context );
      this.includeTime = includeTime;
    }

    protected boolean isIncludeTime() {
      return includeTime;
    }

    @Override
    public DateComparatorOrSearchCriterionCombination and() {
      return new DateComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.AND,
              isIncludeTime(), getContext() );
    }

    @Override
    public DateComparatorOrSearchCriterionCombination or() {
      return new DateComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.OR,
              isIncludeTime(), getContext() );
    }

  }

  private static class DateComparatorImpl extends DatatypeSpecificComparatorImpl<Date> implements DateComparator {

    private final boolean includeTime;

    public DateComparatorImpl( final boolean includeTime, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( context );
      this.includeTime = includeTime;
    }

    protected boolean isIncludeTime() {
      return includeTime;
    }

    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return getContext().handleSearchCriterion( c );
    }

    private DateComparatorCombination create( final Date value, final Comparison<Date>... comparisons ) {
      final Collection<SearchCriterion> criteria = new LinkedList<SearchCriterion>();
      for ( final Comparison<Date> comparison : comparisons ) {
        final SearchCriterion scComparison = new FieldComparison<Date>( getContext().field, comparison, value );
        final SearchCriterion handledScComparison = handleSearchCriterion( scComparison );
        criteria.add( handledScComparison );
      }
      final SearchCriterion[] criteriaArrays = criteria.toArray( new SearchCriterion[criteria.size()] );
      final SearchCriterion sc = LogicalCombination.toSearchCriterion( LogicalCombinationType.OR, criteriaArrays );
      return new DateComparatorCombinationImpl( isIncludeTime(), sc, getContext() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public DateComparatorCombination isEqualTo( final Date value ) {
      return create( value, isIncludeTime() ? DateComparison.TIME_EQUALS : DateComparison.DATE_EQUALS );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public DateComparatorCombination isAfter( final Date value ) {
      return create( value, isIncludeTime() ? DateComparison.TIME_AFTER : DateComparison.DATE_AFTER );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public DateComparatorCombination isAfterOrEqualTo( final Date value ) {
      return create( value, isIncludeTime() ? DateComparison.TIME_AFTER : DateComparison.DATE_AFTER,
              isIncludeTime() ? DateComparison.TIME_EQUALS : DateComparison.DATE_EQUALS );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public DateComparatorCombination isBefore( final Date value ) {
      return create( value, isIncludeTime() ? DateComparison.TIME_BEFORE : DateComparison.DATE_BEFORE );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public DateComparatorCombination isBeforeOrEqualTo( final Date value ) {
      return create( value, isIncludeTime() ? DateComparison.TIME_BEFORE : DateComparison.DATE_BEFORE,
              isIncludeTime() ? DateComparison.TIME_EQUALS : DateComparison.DATE_EQUALS );
    }

  }

  //BOOLEAN SPECIFIC CLASSES

  private static class BooleanComparatorOrSearchCriterionCombinationImpl extends SearchCriterionCombinationImpl
          implements BooleanComparatorOrSearchCriterionCombination {

    private final BooleanComparatorImpl delegate;

    public BooleanComparatorOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( c, combination );
      delegate = new BooleanComparatorImpl( context ) {

        @Override
        protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
          return new LogicalCombination( getCriterion(), getCombination(), super.handleSearchCriterion( c ) );
        }

      };
    }

    @Override
    public BooleanComparatorCombination isEqualTo( final Boolean value ) {
      return delegate.isEqualTo( value );
    }

    @Override
    public BooleanComparatorCombination isTrue() {
      return delegate.isTrue();
    }

    @Override
    public BooleanComparatorCombination isFalse() {
      return delegate.isFalse();
    }

  }

  private static class BooleanComparatorCombinationImpl extends DatatypeSpecificComparatorCombinationImpl<Boolean>
          implements BooleanComparatorCombination {

    private static final long serialVersionUID = 1L;

    public BooleanComparatorCombinationImpl( final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original, context );
    }

    @Override
    public BooleanComparatorOrSearchCriterionCombination and() {
      return new BooleanComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.AND,
              getContext() );
    }

    @Override
    public BooleanComparatorOrSearchCriterionCombination or() {
      return new BooleanComparatorOrSearchCriterionCombinationImpl( getOriginal(), LogicalCombinationType.OR,
              getContext() );
    }

  }

  private static class BooleanComparatorImpl extends DatatypeSpecificComparatorImpl<Boolean> implements
          BooleanComparator {

    public BooleanComparatorImpl( final SingleFieldSearchCriterionBuilderImpl context ) {
      super( context );
    }

    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return getContext().handleSearchCriterion( c );
    }

    private BooleanComparatorCombination create( final Boolean value, final Comparison<Boolean>... comparisons ) {
      final Collection<SearchCriterion> criteria = new LinkedList<SearchCriterion>();
      for ( final Comparison<Boolean> comparison : comparisons ) {
        final SearchCriterion scComparison = new FieldComparison<Boolean>( getContext().field, comparison, value );
        final SearchCriterion handledScComparison = handleSearchCriterion( scComparison );
        criteria.add( handledScComparison );
      }
      final SearchCriterion[] criteriaArrays = criteria.toArray( new SearchCriterion[criteria.size()] );
      final SearchCriterion sc = LogicalCombination.toSearchCriterion( LogicalCombinationType.OR, criteriaArrays );
      return new BooleanComparatorCombinationImpl( sc, getContext() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public BooleanComparatorCombination isEqualTo( final Boolean value ) {
      return create( value, BooleanComparison.EQUALS );
    }

    @Override
    public BooleanComparatorCombination isTrue() {
      return isEqualTo( true );
    }

    @Override
    public BooleanComparatorCombination isFalse() {
      return isEqualTo( false );
    }

  }

  //COLLECTION SPECIFIC CLASSES

  private static class CollectionComparatorOrSearchCriterionCombinationImpl<E> extends SearchCriterionCombinationImpl
          implements CollectionComparatorOrSearchCriterionCombination<E> {

    private final CollectionComparatorImpl<E> delegate;

    public CollectionComparatorOrSearchCriterionCombinationImpl( final SearchCriterion c,
            final LogicalCombinationType combination, final SingleFieldSearchCriterionBuilderImpl context ) {
      super( c, combination );
      delegate = new CollectionComparatorImpl<E>( context ) {

        @Override
        protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
          return new LogicalCombination( getCriterion(), getCombination(), super.handleSearchCriterion( c ) );
        }

      };
    }

    @Override
    public CollectionComparatorCombination<E> isEqualTo( final Collection<E> value ) {
      return delegate.isEqualTo( value );
    }

    @Override
    public CollectionComparatorCombination<E> containsAllOf( final E... value ) {
      return delegate.containsAllOf( value );
    }

    @Override
    public CollectionComparatorCombination<E> containsOneOf( final E... value ) {
      return delegate.containsOneOf( value );
    }

  }

  private static class CollectionComparatorCombinationImpl<E> extends
          DatatypeSpecificComparatorCombinationImpl<Collection<E>> implements CollectionComparatorCombination<E> {

    private static final long serialVersionUID = 1L;

    public CollectionComparatorCombinationImpl( final SearchCriterion original,
            final SingleFieldSearchCriterionBuilderImpl context ) {
      super( original, context );
    }

    @Override
    public CollectionComparatorOrSearchCriterionCombination<E> and() {
      return new CollectionComparatorOrSearchCriterionCombinationImpl<E>( getOriginal(), LogicalCombinationType.AND,
              getContext() );
    }

    @Override
    public CollectionComparatorOrSearchCriterionCombination<E> or() {
      return new CollectionComparatorOrSearchCriterionCombinationImpl<E>( getOriginal(), LogicalCombinationType.OR,
              getContext() );
    }

  }

  private static class CollectionComparatorImpl<E> extends DatatypeSpecificComparatorImpl<Collection<E>> implements
          CollectionComparator<E> {

    public CollectionComparatorImpl( final SingleFieldSearchCriterionBuilderImpl context ) {
      super( context );
    }

    protected SearchCriterion handleSearchCriterion( final SearchCriterion c ) {
      return getContext().handleSearchCriterion( c );
    }

    private CollectionComparatorCombination<E> create( final Collection<E> value,
            final Comparison<Collection<?>>... comparisons ) {
      final Collection<SearchCriterion> criteria = new LinkedList<SearchCriterion>();
      for ( final Comparison<Collection<?>> comparison : comparisons ) {
        final SearchCriterion scComparison = new FieldComparison<Collection<?>>( getContext().field, comparison, value );
        final SearchCriterion handledScComparison = handleSearchCriterion( scComparison );
        criteria.add( handledScComparison );
      }
      final SearchCriterion[] criteriaArrays = criteria.toArray( new SearchCriterion[criteria.size()] );
      final SearchCriterion sc = LogicalCombination.toSearchCriterion( LogicalCombinationType.OR, criteriaArrays );
      return new CollectionComparatorCombinationImpl<E>( sc, getContext() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public CollectionComparatorCombination<E> isEqualTo( final Collection<E> value ) {
      return create( value, CollectionComparison.EQUALS );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public CollectionComparatorCombination<E> containsAllOf( final E... value ) {
      return create( Arrays.asList( value ), CollectionComparison.CONTAINS_ALL );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public CollectionComparatorCombination<E> containsOneOf( final E... value ) {
      return create( Arrays.asList( value ), CollectionComparison.CONTAINS_ONE );
    }

  }

}
