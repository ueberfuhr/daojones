package de.ars.daojones.drivers.notes.xt;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A Notes event handler is a callback object that is invoked by the Notes
 * driver when events occur to a Notes object. Such an event is saving a
 * document or reading from a view. An event handler is registered at the
 * connection that is scoped to a single type of bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface NotesEventHandler {

  /**
   * The context of a document handler callback event.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface NotesEventHandlerContext<T extends Base> {
    /**
     * Returns the document.
     * 
     * @return the document
     * @throws NotesException
     */
    T getSource() throws NotesException;

    /**
     * Returns the database.
     * 
     * @return the database
     * @throws NotesException
     */
    Database getDatabase() throws NotesException;
  }

  /**
   * This method is invoked before a document is saved. You can invoke one of
   * the exceptions to prevent the document from being saved.<br/>
   * <br/>
   * <b>Please note:</b> A document is saved too even if the bean is mapped to a
   * view.
   * 
   * @param context
   *          the context
   * @param config
   *          the configuration of the save operation
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  void beforeSave( NotesEventHandlerContext<Document> context, DocumentSaveConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException;

  /**
   * This method is invoked after a document is saved.<br/>
   * <br/>
   * <b>Please note:</b> A document is saved too even if the bean is mapped to a
   * view.
   * 
   * @param context
   *          the context
   * @param result
   *          the result of the save operation
   */
  void afterSave( NotesEventHandlerContext<Document> context, DocumentSaveResult result );

  /**
   * An object to configure the save operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  final class DocumentSaveConfiguration {

    private boolean force;
    private boolean createResponse;
    private boolean markRead;
    private boolean ignoreFailure;

    /**
     * Returns the force flag. If <tt>true</tt>, the document is saved even if
     * someone else edits and saves the document while the script is running.
     * The last version of the document that was saved wins; the earlier version
     * is discarded.<br/>
     * If <tt>false</tt>, and someone else edits the document while the script
     * is running, the {@link #isCreateResponse()} flag determines what happens.
     * 
     * @return the force flag
     */
    public boolean isForce() {
      return force;
    }

    /**
     * Sets the force flag. If <tt>true</tt>, the document is saved even if
     * someone else edits and saves the document while the script is running.
     * The last version of the document that was saved wins; the earlier version
     * is discarded.<br/>
     * If <tt>false</tt>, and someone else edits the document while the script
     * is running, the {@link #isCreateResponse()} flag determines what happens.
     * 
     * @param force
     *          the force flag
     */
    public void setForce( final boolean force ) {
      this.force = force;
    }

    /**
     * Returns the create response flag. If <tt>true</tt>, the current document
     * becomes a response to the original document (this is what the replicator
     * does when there's a replication conflict).<br/>
     * If <tt>false</tt>, the save is canceled. If the {@link #isForce()} flag
     * is <tt>true</tt>, this flag has no effect.
     * 
     * @return create response flag
     */
    public boolean isCreateResponse() {
      return createResponse;
    }

    /**
     * Sets create response flag. If <tt>true</tt>, the current document becomes
     * a response to the original document (this is what the replicator does
     * when there's a replication conflict).<br/>
     * If <tt>false</tt>, the save is canceled. If the {@link #isForce()} flag
     * is <tt>true</tt>, this flag has no effect.
     * 
     * @param createResponse
     *          create response flag
     */
    public void setCreateResponse( final boolean createResponse ) {
      this.createResponse = createResponse;
    }

    /**
     * Returns the mark read flag. If <tt>true</tt>, the document is marked as
     * read on behalf of the current user ID.<br/>
     * If <tt>false</tt>, the document is not marked as read.<br/>
     * <br/>
     * <b>Please note:</b> If the database does not track unread marks, all
     * documents are considered read, and this parameter has no effect.
     * 
     * @return the mark read flag
     */
    public boolean isMarkRead() {
      return markRead;
    }

    /**
     * Sets the mark read flag. If <tt>true</tt>, the document is marked as read
     * on behalf of the current user ID.<br/>
     * If <tt>false</tt>, the document is not marked as read.<br/>
     * <br/>
     * <b>Please note:</b> If the database does not track unread marks, all
     * documents are considered read, and this parameter has no effect.
     * 
     * @param markRead
     *          the mark read flag
     */
    public void setMarkRead( final boolean markRead ) {
      this.markRead = markRead;
    }

    /**
     * Returns the ignore failure flag. This flag only matters when the save
     * operation of the document returns <ff>false</tt>. If <tt>true</tt>, the
     * failure is ignored, and the Notes driver continues further invocations.
     * If <tt>false</tt>, a DataAccessException is thrown.
     * 
     * @return the ignore failure flag
     */
    public boolean isIgnoreFailure() {
      return ignoreFailure;
    }

    /**
     * Sets the ignore failure flag. This flag only matters when the save
     * operation of the document returns <ff>false</tt>. If <tt>true</tt>, the
     * failure is ignored, and the Notes driver continues further invocations.
     * If <tt>false</tt>, a DataAccessException is thrown.
     * 
     * @param ignoreFailure
     *          the ignore failure flag
     */
    public void setIgnoreFailure( final boolean ignoreFailure ) {
      this.ignoreFailure = ignoreFailure;
    }

  }

  /**
   * The result of a save operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface DocumentSaveResult {

    /**
     * Returns the configuration that was set before the save operation.
     * 
     * @return the configuration
     */
    DocumentSaveConfiguration getConfiguration();

    /**
     * Returns the information whether the document was saved or not.
     * 
     * @return the information whether the document was saved or not
     */
    boolean wasSaved();

  }

  /**
   * This method is invoked before a document is deleted. You can invoke one of
   * the exceptions to prevent the document from being deleted.<br/>
   * <br/>
   * <b>Please note:</b> A document is deleted too even if the bean is mapped to
   * a view.
   * 
   * @param context
   *          the context
   * @param config
   *          the configuration of the delete operation
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  void beforeDelete( NotesEventHandlerContext<Document> context, DocumentDeleteConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException;

  /**
   * This method is invoked after a document is deleted.<br/>
   * <br/>
   * <b>Please note:</b> A document is deleted too even if the bean is mapped to
   * a view.
   * 
   * @param context
   *          the context
   * @param result
   *          the result of the delete operation
   */
  void afterDelete( NotesEventHandlerContext<Document> context, DocumentDeleteResult result );

  /**
   * An object to configure the delete operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  final class DocumentDeleteConfiguration {

    private boolean soft;
    private boolean force;
    private boolean ignoreFailure;

    /**
     * Returns the soft flag.<br/>
     * If <tt>true</tt>, documents are deleted permanently from the database,
     * doing a soft deletion if soft deletions are enabled.<br/>
     * If <tt>false</tt> documents are deleted permanently from the database,
     * doing a hard deletion even if soft deletions are enabled.
     * 
     * @return the soft flag
     */
    public boolean isSoft() {
      return soft;
    }

    /**
     * Sets the soft flag.<br/>
     * If <tt>true</tt>, documents are deleted permanently from the database,
     * doing a soft deletion if soft deletions are enabled.<br/>
     * If <tt>false</tt> documents are deleted permanently from the database,
     * doing a hard deletion even if soft deletions are enabled.
     * 
     * @param soft
     *          the soft flag
     */
    public void setSoft( final boolean soft ) {
      this.soft = soft;
    }

    /**
     * Returns the force flag. If <tt>true</tt>, the document is deleted even if
     * another user modifies the document after the script opens it.<br/>
     * If <tt>false</tt>, the document is not deleted if another user modifies
     * it.
     * 
     * @return the force flag
     */
    public boolean isForce() {
      return force;
    }

    /**
     * Sets the force flag. If <tt>true</tt>, the document is deleted even if
     * another user modifies the document after the script opens it.<br/>
     * If <tt>false</tt>, the document is not deleted if another user modifies
     * it.
     * 
     * @param force
     *          the force flag
     */
    public void setForce( final boolean force ) {
      this.force = force;
    }

    /**
     * Returns the ignore failure flag. This flag only matters when the delete
     * operation of the document returns <ff>false</tt>. If <tt>true</tt>, the
     * failure is ignored, and the Notes driver continues further invocations.
     * If <tt>false</tt>, a DataAccessException is thrown.
     * 
     * @return the ignore failure flag
     */
    public boolean isIgnoreFailure() {
      return ignoreFailure;
    }

    /**
     * Sets the ignore failure flag. This flag only matters when the delete
     * operation of the document returns <ff>false</tt>. If <tt>true</tt>, the
     * failure is ignored, and the Notes driver continues further invocations.
     * If <tt>false</tt>, a DataAccessException is thrown.
     * 
     * @param ignoreFailure
     *          the ignore failure flag
     */
    public void setIgnoreFailure( final boolean ignoreFailure ) {
      this.ignoreFailure = ignoreFailure;
    }
  }

  /**
   * The result of a delete operation.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface DocumentDeleteResult {

    /**
     * Returns the configuration that was set before the delete operation.
     * 
     * @return the configuration
     */
    DocumentDeleteConfiguration getConfiguration();

    /**
     * Returns the information whether the document was deleted or not.
     * 
     * @return the information whether the document was deleted or not
     */
    boolean wasDeleted();

  }

  /**
   * This method is invoked before a full-text search is executed. You can
   * invoke one of the exceptions to prevent the full-text search.
   * 
   * @param context
   *          the context
   * @param config
   *          the configuration of the full-text search
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  void beforeFTSearch( NotesEventHandlerContext<? extends Base> context, FTSearchConfiguration config )
          throws NotesException, DataAccessException, ConfigurationException;

  /**
   * This method is invoked after a full-text search is executed.
   * 
   * @param context
   *          the context
   * @param result
   *          the result of the full-text search
   */
  void afterFTSearch( NotesEventHandlerContext<? extends Base> context, FTSearchResult result );

  /**
   * An object to configure the full-text search.<br/>
   * <br/>
   * <b>Please note:</b> This class is currently empty, but might contain
   * settings in the future.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  final class FTSearchConfiguration {
    // currently empty
  }

  /**
   * The result of a full-text search.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface FTSearchResult {
    /**
     * Returns the configuration that was set before the full-text search was
     * executed.
     * 
     * @return the configuration
     */
    FTSearchConfiguration getConfiguration();

    /**
     * Returns the count of results.
     * 
     * @return the count of results
     */
    int getCount();
  }

}
