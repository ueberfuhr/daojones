package de.ars.daojones.runtime.query;

import java.util.Collection;
import java.util.Date;

/**
 * The single property search criterion builder.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface SingleFieldSearchCriterionBuilder {

  /**
   * @deprecated Do not use this method for field comparisons.
   */
  @Override
  @Deprecated
  public boolean equals( Object o );

  /**
   * @deprecated Do not use this method for field comparisons.
   */
  @Override
  @Deprecated
  public int hashCode();

  /**
   * A search criterion that matches an empty field or property.
   * 
   * @return the search criterion
   */
  SingleFieldSearchCriterionLimitedCombination isEmpty();

  /**
   * Reads the property or field with string-specific comparisons. Such
   * comparisons are always case-sensitive.
   * 
   * @return the comparator
   * @see #asCaseInsensitiveString()
   */
  StringComparator asString();

  /**
   * Reads the property or field with string-specific comparisons. This compares
   * strings without any regards to upper or lower case characters.
   * 
   * @return the comparator
   */
  StringComparator asCaseInsensitiveString();

  /**
   * Reads the property or field with number-specific comparisons.
   * 
   * @return the comparator
   */
  NumberComparator asNumber();

  /**
   * Reads the property or field with date-specific comparisons. This only
   * includes the day, not the time.
   * 
   * @return the comparator
   * @see #asDateTime()
   */
  DateComparator asDate();

  /**
   * Reads the property or field with date-specific comparisons. This compares
   * dates including the time.
   * 
   * @return the comparator
   */
  DateComparator asDateTime();

  /**
   * Reads the property or field with boolean-specific comparisons.
   * 
   * @return the comparator
   */
  BooleanComparator asBoolean();

  /**
   * Reads the property or field with collection-specific comparisons.
   * 
   * @param c
   *          the class of the elements within the collection
   * @param <E>
   *          the type of the elements within the collection
   * @return the comparator
   */
  <E> CollectionComparator<E> asCollectionOf( Class<E> c );

  /* *************************************
   *   I N N E R   I N T E R F A C E S   *
   ************************************* */

  /**
   * If you are in a single field context, you can combine a search criterion
   * with a logical combination to combine it with a completely new criterion or
   * with a criterion that is relative to this field or property.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface SingleFieldSearchCriterionBuilderOrSearchCriterionCombination extends
          SingleFieldSearchCriterionBuilder, SearchCriterionCombination {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single field. This can
   * only be combined field or property dependent using a logical OR. With a
   * logical AND, you can only combine field or property independent criterions.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface SingleFieldSearchCriterionLimitedCombination extends ExtensibleSearchCriterion {

    @Override
    public SingleFieldSearchCriterionBuilderOrSearchCriterionCombination or();

  }

  //  ------------------- U N U S E D -------------------
  //  /**
  //   * A combination of a criterion that is dependent to a single property or
  //   * field.
  //   * 
  //   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
  //   * @since 2.0
  //   */
  //  public static interface SinglePropertySearchCriterionCombination extends
  //          SinglePropertySearchCriterionLimitedCombination {
  //
  //    @Override
  //    public SinglePropertySearchCriterionBuilderOrSearchCriterionCombination and();
  //
  //  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a comparison with a logical combination to combine it with a
   * completely new criterion or with a criterion that is relative to this field
   * or property comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <Type>
   *          the type of the property or field
   */
  public static interface DatatypeSpecificComparatorOrSearchCriterionCombination<Type> extends
          DatatypeSpecificComparator<Type>, SearchCriterionCombination {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <Type>
   *          the type of the property or field
   */
  public static interface DatatypeSpecificComparatorCombination<Type> extends ExtensibleSearchCriterion {

    @Override
    public DatatypeSpecificComparatorOrSearchCriterionCombination<Type> and();

    @Override
    public DatatypeSpecificComparatorOrSearchCriterionCombination<Type> or();

  }

  /**
   * A datatype-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <Type>
   *          the type of the property or field
   */
  public static interface DatatypeSpecificComparator<Type> {

    /**
     * @deprecated Do not use this method for field comparisons.
     * @see #isEqualTo(Object)
     */
    @Override
    @Deprecated
    public boolean equals( Object o );

    /**
     * @deprecated Do not use this method for field comparisons.
     */
    @Override
    @Deprecated
    public int hashCode();

    /**
     * Creates a search criterion that selects values equal to the given
     * parameter.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    DatatypeSpecificComparatorCombination<Type> isEqualTo( Type value );

  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a string comparison with a logical combination to combine it with a
   * completely new criterion or with a criterion that is relative to this field
   * or property string comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface StringComparatorOrSearchCriterionCombination extends
          DatatypeSpecificComparatorOrSearchCriterionCombination<String>, StringComparator {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface StringComparatorCombination extends DatatypeSpecificComparatorCombination<String> {

    @Override
    public StringComparatorOrSearchCriterionCombination and();

    @Override
    public StringComparatorOrSearchCriterionCombination or();

  }

  /**
   * A string-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface StringComparator extends DatatypeSpecificComparator<String> {

    @Override
    StringComparatorCombination isEqualTo( String value );

    /**
     * The case-sensitive startsWith criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    StringComparatorCombination startsWith( String value );

    /**
     * The case-sensitive endsWith criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    StringComparatorCombination endsWith( String value );

    /**
     * The case-sensitive contains criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    StringComparatorCombination contains( String value );

    /**
     * The case-sensitive like criterion. The following placeholders are
     * possible:
     * <ul>
     * <li><code>"_"</code>: Matches a single character.
     * <li><code>"%"</code>: Matches multiple (also 0) characters.
     * </ul>
     * Use <code>"/"</code> as escape character.<br/>
     * <br/>
     * <b>Examples:</b><br/>
     * <ul>
     * <li><code>"My f_rst example."</code> matches "My first example."
     * <li><code>"My % example."</code> matches "My first example."
     * <li><code>"The /_ is a character."</code> matches "The _ is a character."
     * <li><code>"The // is a character."</code> matches "The / is a character."
     * </ul>
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    StringComparatorCombination isLike( String value );

  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a number comparison with a logical combination to combine it with a
   * completely new criterion or with a criterion that is relative to this field
   * or property number comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface NumberComparatorOrSearchCriterionCombination extends
          DatatypeSpecificComparatorOrSearchCriterionCombination<Number>, NumberComparator {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface NumberComparatorCombination extends DatatypeSpecificComparatorCombination<Number> {

    @Override
    public NumberComparatorOrSearchCriterionCombination and();

    @Override
    public NumberComparatorOrSearchCriterionCombination or();

  }

  /**
   * A number-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface NumberComparator extends DatatypeSpecificComparator<Number> {

    /**
     * The lower than criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    NumberComparatorCombination isLowerThan( Number value );

    /**
     * The lower than or equal to criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    NumberComparatorCombination isLowerThanOrEqualTo( Number value );

    /**
     * The greater than criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    NumberComparatorCombination isGreaterThan( Number value );

    /**
     * The greater than or equal to criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    NumberComparatorCombination isGreaterThanOrEqualTo( Number value );

  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a date comparison with a logical combination to combine it with a
   * completely new criterion or with a criterion that is relative to this field
   * or property date comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface DateComparatorOrSearchCriterionCombination extends
          DatatypeSpecificComparatorOrSearchCriterionCombination<Date>, DateComparator {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface DateComparatorCombination extends DatatypeSpecificComparatorCombination<Date> {

    @Override
    public DateComparatorOrSearchCriterionCombination and();

    @Override
    public DateComparatorOrSearchCriterionCombination or();

  }

  /**
   * A date-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface DateComparator extends DatatypeSpecificComparator<Date> {

    /**
     * The after criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    DateComparatorCombination isAfter( Date value );

    /**
     * The after or equal to criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    DateComparatorCombination isAfterOrEqualTo( Date value );

    /**
     * The before criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    DateComparatorCombination isBefore( Date value );

    /**
     * The before or equal to criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    DateComparatorCombination isBeforeOrEqualTo( Date value );

  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a boolean comparison with a logical combination to combine it with
   * a completely new criterion or with a criterion that is relative to this
   * field or property boolean comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface BooleanComparatorOrSearchCriterionCombination extends
          DatatypeSpecificComparatorOrSearchCriterionCombination<Boolean>, BooleanComparator {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface BooleanComparatorCombination extends DatatypeSpecificComparatorCombination<Boolean> {

    @Override
    public BooleanComparatorOrSearchCriterionCombination and();

    @Override
    public BooleanComparatorOrSearchCriterionCombination or();

  }

  /**
   * A boolean-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface BooleanComparator extends DatatypeSpecificComparator<Boolean> {

    /**
     * The true criterion.
     * 
     * @return the search criterion
     */
    BooleanComparatorCombination isTrue(); // For boolean comparison, we do not need combinations.

    /**
     * The false criterion.
     * 
     * @return the search criterion
     */
    BooleanComparatorCombination isFalse(); // For boolean comparison, we do not need combinations.

  }

  /**
   * If you are in a single property or field comparator context, you can
   * combine a collection comparison with a logical combination to combine it
   * with a completely new criterion or with a criterion that is relative to
   * this field or property collection comparison.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <E>
   *          the element type within the collection
   */
  public static interface CollectionComparatorOrSearchCriterionCombination<E> extends
          DatatypeSpecificComparatorOrSearchCriterionCombination<Collection<E>>, CollectionComparator<E> {
    // empty, combination of interfaces
  }

  /**
   * A combination of a criterion that is dependent to a single property or
   * field.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <E>
   *          the element type within the collection
   */
  public static interface CollectionComparatorCombination<E> extends
          DatatypeSpecificComparatorCombination<Collection<E>> {

    @Override
    public CollectionComparatorOrSearchCriterionCombination<E> and();

    @Override
    public CollectionComparatorOrSearchCriterionCombination<E> or();

  }

  /**
   * A collection-specific comparator.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   * @param <E>
   *          the element type within the collection
   */
  public static interface CollectionComparator<E> extends DatatypeSpecificComparator<Collection<E>> {

    /**
     * The contains all criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    CollectionComparatorCombination<E> containsAllOf( E... value );

    /**
     * The contains one criterion.
     * 
     * @param value
     *          the parameter for comparison
     * @return the search criterion
     */
    CollectionComparatorCombination<E> containsOneOf( E... value );

  }

}
