package de.ars.daojones.runtime.query;


/**
 * A kind of {@link Comparison} that has a value that should be transformed
 * before creating the query.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the type of the values to be compared
 */
interface TransformingComparison<T> extends Comparison<T> {

	/**
	 * Transforms the value for comparison.
	 * 
	 * @param value
	 *            the value
	 * @return the transformed value
	 */
	T transformForComparison(T value);

}
