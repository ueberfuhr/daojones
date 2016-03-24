package de.ars.daojones.runtime.query;


/**
 * A simple interface for a comparison operator. This is used by
 * {@link PropertySearchCriterion}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *            the type of the values that should be compared
 */
interface Comparison<T> {

	/**
	 * Returns a string that is part of the query language of the target system.
	 * 
	 * @param templateManager
	 *            the {@link TemplateManager}
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            the value for comparison
	 * @return the part of the query language
	 */
	String toQuery(final TemplateManager templateManager,
			final String fieldName, final T value);

}
