package de.ars.daojones.runtime.query;


/**
 * A {@link SearchCriterion} searching for field values in the database.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @deprecated Use {@link PropertySearchCriterion} instead.
 * @param <T> the type of the DaoJones bean
 */
@Deprecated
public class FieldSearchCriterion<T> extends PropertySearchCriterion<T> {

	private static final long serialVersionUID = -7409426072575600756L;

	/**
	 * Creates an instance.
	 * @param property
	 *            the name of the property of the bean.
	 * @param comparison the {@link Comparison}
	 * @param value the value used for comparison
	 */
	public FieldSearchCriterion(final String property, final Comparison<T> comparison, T value) {
		super(property, comparison, value);
	}
	
}
