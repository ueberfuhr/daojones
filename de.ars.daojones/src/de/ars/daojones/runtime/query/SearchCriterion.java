package de.ars.daojones.runtime.query;

import java.io.Serializable;

/**
 * A search criterion is used to find objects based on special conditions. There
 * are two criteria for field values ({@link FieldSearchCriterion} and
 * {@link IsEmptySearchCriterion}) and further ones to combine them. You do not
 * have to implement this interface, because the driver won't support it.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface SearchCriterion extends Serializable {

	/**
	 * Returns a query string to search for entries in the database. This is
	 * used by drivers that query results with their own query language.
	 * 
	 * @param templateManager
	 *            the {@link TemplateManager}
	 * @param resolver
	 *            the {@link VariableResolver}
	 * @throws VariableResolvingException
	 *             if resolving the variables to build the query failed
	 * @return the query string
	 */
	public String toQuery(final TemplateManager templateManager,
			final VariableResolver resolver) throws VariableResolvingException;

}
