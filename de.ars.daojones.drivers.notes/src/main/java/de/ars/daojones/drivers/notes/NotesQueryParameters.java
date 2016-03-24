package de.ars.daojones.drivers.notes;

import de.ars.daojones.runtime.query.Query;

/**
 * Notes-specific query parameters.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @see Query#withParameters(de.ars.daojones.runtime.query.Query.Parameter...)
 */
public enum NotesQueryParameters implements Query.Parameter {

  /**
   * This query parameter will lead the query for documents to be executed with
   * <tt>FTSearch</tt>. Otherwise, documents are searched with the
   * <tt>Search</tt> method invoked on the database, which provided the full
   * functionality, but can be slow for large databases. <tt>FTSearch</tt> is
   * faster, but has some restrictions:
   * <ul>
   * <li>It cannot be used to search for date-time values. Only searching for
   * dates is allowed. DaoJones will otherwise throw an exception.</li>
   * <li>It cannot be used to search for collection comparisons. DaoJones will
   * otherwise throw an exception.</li>
   * <li>It is only able to search for case-insensitive field values. Each query
   * that searches for strings with a comparison that differs from
   * StringComparison.CONTAINS_IGNORECASE will result in an exception.</li>
   * <li>It only finds results of the current index (if available). You can
   * enable DaoJones to update the full-text index of the whole database before
   * each query to any view. This could slow down performance. (see chapter
   * "Driver Settings" in documentation for details)</li>
   * </ul>
   * 
   * @see <a
   *      href="http://www-01.ibm.com/support/knowledgecenter/SSVRGU_9.0.0/com.ibm.designer.domino.main.doc/H_FTSEARCH_METHOD_DB_JAVA.html">IBM
   *      documentation for FTSearch</a>
   */
  FT_SEARCH;

}
