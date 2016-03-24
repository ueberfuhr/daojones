package de.ars.daojones.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.FieldAccessException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.ColumnSelector;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionReference;

/**
 * Default implementation for {@link Dao}. It is recommended that you use this
 * class as superclass for all DaoJones beans.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
@Abstract
public class DataObjectContainer implements Dao {

  private static final long serialVersionUID = 1L;
  private DataObject dataObject;
  private final TemporaryFieldAccessor tempFieldAccessor = new TemporaryFieldAccessor(
      this );
  private final Dao container;

  /**
   * Creates an instance.
   */
  public DataObjectContainer() {
    this( null );
  }

  /**
   * Creates an instance with another dao as container. This is called if this
   * object is used as a delegate object.
   * 
   * @param container
   *          the dao
   */
  public DataObjectContainer( Dao container ) {
    super();
    this.container = container;
  }

  /**
   * @see de.ars.daojones.runtime.Dao#getDataObject()
   */
  public DataObject getDataObject() {
    return dataObject;
  }

  /**
   * @see de.ars.daojones.runtime.Dao#setDataObject(de.ars.daojones.runtime.DataObject)
   */
  public void setDataObject( DataObject dataObject ) throws DataAccessException {
    this.dataObject = dataObject;
    try {
      if ( null != dataObject )
        this.tempFieldAccessor.flush( dataObject.getFieldAccessor() );
    } catch ( FieldAccessException e ) {
      throw new DataAccessException( e );
    }
  }

  private String applicationContextId;

  /**
   * @see de.ars.daojones.runtime.Dao#getApplicationContextId()
   */
  public String getApplicationContextId() {
    return applicationContextId;
  }

  /**
   * @see de.ars.daojones.runtime.Dao#setApplicationContextId(java.lang.String)
   */
  public void setApplicationContextId( String applicationContextId ) {
    this.applicationContextId = applicationContextId;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME
        * result
        + ( ( applicationContextId == null ) ? 0 : applicationContextId
            .hashCode() );
    result = PRIME * result
        + ( ( dataObject == null ) ? 0 : dataObject.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( !( obj instanceof Dao ) )
      return false;
    final Dao other = ( Dao ) obj;
    if ( applicationContextId == null ) {
      if ( other.getApplicationContextId() != null )
        return false;
    } else if ( !applicationContextId.equals( other.getApplicationContextId() ) )
      return false;
    if ( dataObject == null ) {
      if ( other.getDataObject() != null )
        return false;
    } else if ( !dataObject.equals( other.getDataObject() ) )
      return false;
    return true;
  }

  /**
   * @see de.ars.daojones.runtime.Dao#isNew()
   */
  public boolean isNew() {
    return null == dataObject;
  }

  /**
   * The type of a reference that this object holds of another {@link Dao}.
   * Concerning this type, such child elements are handled on different ways.
   * 
   * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
   */
  protected static enum ReferenceType {
    /**
     * Objects that should be marked as modified. These objects are updated
     * during the next and only the next update cycle.
     */
    MODIFIED,
    /**
     * Objects that should be marked as deleted. These objects are deleted
     * during the next and only the next update cycle.
     */
    DELETED,
    /**
     * Objects that are referenced from the object. These objects are updated in
     * every update cycle.
     */
    AGGREGATED,
    /**
     * A map with objects that are referenced from the object and only from this
     * object. These objects cannot exist without their parent, so they are
     * deleted when the parent is deleted.
     */
    COMPOSITE
  }

  private final class Reference implements Serializable {
    private static final long serialVersionUID = 4430862104094064602L;
    private final Dao dao;
    private final ConnectionReference<Dao> connection;
    private final ReferenceType type;

    public Reference( ConnectionReference<Dao> connection, Dao dao,
        ReferenceType type ) {
      super();
      this.connection = connection;
      this.dao = dao;
      this.type = type;
    }

    public Dao getDao() {
      return dao;
    }

    public ConnectionReference<Dao> getConnectionReference() {
      return connection;
    }

    public Connection<Dao> getConnection() throws DataAccessException {
      return null != getConnectionReference() ? getConnectionReference()
          .getConnection() : DataObjectContainer.this.getConnection( getDao() );
    }

    public ReferenceType getType() {
      return type;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( dao == null ) ? 0 : dao.hashCode() );
      return result;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( this == obj )
        return true;
      if ( obj == null )
        return false;
      if ( getClass() != obj.getClass() )
        return false;
      Reference other = ( Reference ) obj;
      if ( dao == null ) {
        if ( other.dao != null )
          return false;
      } else if ( !dao.equals( other.dao ) )
        return false;
      return true;
    }
  }

  /**
   * A collection with references to the object.
   */
  private final Set<Reference> references = new HashSet<Reference>();

  /**
   * Registers a reference where CRUD-operations a delegated to. Use this to
   * update childs when the parent is updated.
   * 
   * @param <T>
   *          the child type
   * @param t
   *          the child
   * @param type
   *          the kind of reference deciding which CRUD-operations are delegated
   */
  protected <T extends Dao> void registerReference( final T t,
      final ReferenceType type ) {
    registerReference( t, type, null );
  }

  /**
   * Registers a reference where CRUD-operations a delegated to. Use this to
   * update childs when the parent is updated.
   * 
   * @param <T>
   *          the child type
   * @param t
   *          the child
   * @param reference
   *          a connection reference to catch a connection for the child
   * @param type
   *          the kind of reference deciding which CRUD-operations are delegated
   */
  @SuppressWarnings( "unchecked" )
  protected <T extends Dao> void registerReference( T t,
      final ReferenceType type, ConnectionReference<T> reference ) {
    references.add( new Reference( ( ConnectionReference<Dao> ) reference, t,
        type ) );
  }

  /**
   * @see de.ars.daojones.runtime.Dao#onUpdate()
   */
  public void onUpdate() throws DataAccessException {
    for ( Iterator<Reference> it = references.iterator(); it.hasNext(); ) {
      final Reference reference = it.next();
      if ( reference.getType() == ReferenceType.DELETED ) {
        reference.getConnection().delete( reference.getDao() );
        it.remove();
      } else {
        reference.getConnection().update( reference.getDao() );
        if ( reference.getType() == ReferenceType.MODIFIED )
          it.remove();
      }
    }
  }

  /**
   * @see de.ars.daojones.runtime.Dao#onCreate()
   */
  public void onCreate() throws DataAccessException {
  }

  /**
   * @see de.ars.daojones.runtime.Dao#onDelete()
   */
  public void onDelete() throws DataAccessException {
    for ( Iterator<Reference> it = references.iterator(); it.hasNext(); ) {
      final Reference reference = it.next();
      if ( reference.getType() == ReferenceType.COMPOSITE
          || reference.getType() == ReferenceType.DELETED ) {
        reference.getConnection().delete( reference.getDao() );
        if ( reference.getType() == ReferenceType.DELETED )
          it.remove();
      } else {
        reference.getConnection().update( reference.getDao() );
        if ( reference.getType() == ReferenceType.MODIFIED )
          it.remove();
      }
    }
  }

  /**
   * Returns the application context where the object was loaded in.
   * 
   * @return the {@link ApplicationContext}
   * @see Dao#getApplicationContextId()
   */
  protected ApplicationContext getApplicationContext() {
    return ApplicationContextFactory.getInstance().getApplicationContext(
        getApplicationContextId() );
  }

  /**
   * Returns a {@link Connection} to load or store special objects within the
   * same {@link ApplicationContext}.
   * 
   * @param <T>
   *          the type of the objects
   * @param c
   *          the class of the objects
   * @return the {@link Connection} or null, if there is no connection
   * @throws DataAccessException
   *           if searching for a connection fails
   */
  protected <T extends Dao> Connection<T> getConnection( Class<T> c )
      throws DataAccessException {
    final ApplicationContext ac = getApplicationContext();
    if ( null == ac )
      throw new DataAccessException(
          "There is no application context for this object!" );
    return Connection.get( ac, c );
  }

  /**
   * Returns a {@link Connection} to load or store special objects within the
   * same {@link ApplicationContext}.
   * 
   * @param <T>
   * @param <U>
   * @param u
   *          the object to be stored by the connection
   * @return the {@link Connection}
   * @throws DataAccessException
   *           if searching for a connection fails
   */
  @SuppressWarnings( "unchecked" )
  protected <T extends Dao, U extends T> Connection<T> getConnection( U u )
      throws DataAccessException {
    return getConnection( ( Class<T> ) u.getClass().getSuperclass() );
  }

  /**
   * @see de.ars.daojones.runtime.Dao#getFieldAccessor()
   */
  public FieldAccessor getFieldAccessor() {
    return null != getDataObject() ? getDataObject().getFieldAccessor()
        : tempFieldAccessor;
  }

  /**
   * Returns the connection.
   * 
   * @return the connection
   * @throws DataAccessException
   */
  protected Connection<Dao> getConnection() throws DataAccessException {
    final Class<Dao> daoClass = getDaoClass();
    final Connection<Dao> result = Connection.get( getApplicationContext(),
        daoClass );
    if ( null == result ) {
      throw new DataAccessException( "No connection found for class "
          + daoClass.getName() + "!" );
    }
    return result;
  }

  /**
   * Returns the dao to identify a connection.
   * 
   * @return the dao
   */
  protected Dao getDao() {
    return null != this.container ? this.container : this;
  }

  /**
   * Returns the class of the dao.
   * 
   * @return the dao class
   */
  @SuppressWarnings( "unchecked" )
  protected Class<Dao> getDaoClass() {
    final Dao dao = getDao();
    final Class<?> daoClass = dao.getClass();
    final Class<?> superClass = daoClass.getSuperclass();
    if ( Dao.class.isAssignableFrom( superClass ) )
      return ( Class<Dao> ) superClass;
    final Class<?>[] interfaces = daoClass.getInterfaces();
    for ( Class<?> i : interfaces ) {
      if ( Dao.class.isAssignableFrom( i ) )
        return ( Class<Dao> ) i;
    }
    return ( Class<Dao> ) daoClass;
    // final Class c = getDao().getClass();
    // if ( null == c.getAnnotation( Inheritations.class )
    // && Dao.class.isAssignableFrom( c ) )
    // return c;
    // final Class superClass = c.getSuperclass();
    // if ( Object.class.getName().equals( superClass.getName() ) ) {
    // for ( Class i : c.getInterfaces() ) {
    // if ( Dao.class.isAssignableFrom( i ) )
    // return i;
    // }
    // } else {
    // return superClass;
    // }
    // return ( Class ) getClass().getSuperclass();
  }

  /**
   * Updates this object.
   * 
   * @see Connection#update(Dao)
   * @throws DataAccessException
   */
  public void update() throws DataAccessException {
    getConnection().update( getDao() );
  }

  /*
   * FIELD ACCESS STRATEGY IMPLEMENTATION
   */

  private final Map<String, Object> cachedFieldValues = new HashMap<String, Object>();
  private final Map<String, FieldData> immediatelyLoadedFields = new HashMap<String, FieldData>();

  private static final class FieldData {
    private final String fieldName;
    private Class<?> fieldType;
    private ColumnSelector selector;

    public FieldData( String fieldName, Class<?> fieldType,
        ColumnSelector selector ) {
      super();
      this.fieldName = fieldName;
      this.fieldType = fieldType;
      this.selector = selector;
    }

    public Class<?> getFieldType() {
      return fieldType;
    }

    public void setFieldType( Class<?> fieldType ) {
      this.fieldType = fieldType;
    }

    public ColumnSelector getSelector() {
      return selector;
    }

    public void setSelector( ColumnSelector selector ) {
      this.selector = selector;
    }

    public String getFieldName() {
      return fieldName;
    }

  }

  /**
   * @see Dao#refresh()
   */
  public void refresh() throws DataAccessException {
    synchronized ( this ) {
      try {
        if ( getDataObject() != null ) {
          getDataObject().refresh();
        }
        reloadFields();
      } catch ( FieldAccessException e ) {
        throw new DataAccessException( e );
      }
    }
  }

  /**
   * Refreshes all cached fields and reads all immediately fields.
   * 
   * @throws FieldAccessException
   */
  public final void reloadFields() throws FieldAccessException {
    this.cachedFieldValues.clear();
    if ( null != getDataObject() ) {
      for ( Map.Entry<String, FieldData> immediatelyLoadedField : immediatelyLoadedFields
          .entrySet() ) {
        final FieldData field = immediatelyLoadedField.getValue();
        final ColumnSelector selector = field.getSelector();
        getFieldValue( field.getFieldName(), field.getFieldType(), true,
            selector );
      }
    }
  }

  /**
   * Returns the field value.
   * 
   * @param <T>
   *          the field type
   * @param fieldName
   *          the field name
   * @param fieldType
   *          the field class
   * @param cached
   *          a flag indicating whether to cache the field value or not
   * @param selector
   *          the {@link ColumnSelector}
   * @return the field value
   * @throws FieldAccessException
   *           if accessing the field fails
   */
  public final <T> T getFieldValue( String fieldName, Class<T> fieldType,
      boolean cached, ColumnSelector selector ) throws FieldAccessException {
    if ( null != selector ) {
      final String base = fieldName;
      try {
        final Dao dao = getDao();
        final String[] columns = selector.getColumns( dao, base );
        T result = null;
        T firstResult = null;
        for ( int i = 0; i < columns.length; i++ ) {
          final String column = columns[i];
          result = getFieldValue( column, fieldType, cached );
          if ( 0 == i ) {
            firstResult = result;
          }
          if ( selector.isValid( dao, base, column, result ) ) {
            break;
          } else {
            // if there isn't any valid value, use the first result
            result = firstResult;
          }
        }
        return result;
      } catch ( DataAccessException e ) {
        throw new FieldAccessException( "Unable to get field value!", base,
            fieldType );
      }
    } else {
      return getFieldValue( fieldName, fieldType, cached );
    }
  }

  @SuppressWarnings( "unchecked" )
  private final <T> T getFieldValue( String fieldName, Class<T> fieldType,
      boolean cached ) throws FieldAccessException {
    synchronized ( cached ? this.cachedFieldValues : new Object() ) {
      if ( !this.cachedFieldValues.containsKey( fieldName ) ) {
        this.cachedFieldValues.put( fieldName, getFieldAccessor()
            .getFieldValue( fieldName, fieldType ) );
      }
      return ( T ) this.cachedFieldValues.get( fieldName );
    }
  }

  /**
   * Sets a field value.
   * 
   * @param <T>
   *          the field type
   * @param fieldName
   *          the field name
   * @param fieldType
   *          the field class
   * @param value
   *          the field value
   * @param cached
   *          a flag indicating whether to cache the field value or not
   * @param commit
   *          a flag indicating whether to save the field value immediately or
   *          not
   * @param selector
   *          the {@link ColumnSelector}
   * @throws FieldAccessException
   *           if accessing the field fails
   */
  public final <T> void setFieldValue( String fieldName, Class<T> fieldType,
      T value, boolean cached, boolean commit, ColumnSelector selector )
      throws FieldAccessException {
    if ( null != selector ) {
      final String base = fieldName;
      try {
        fieldName = selector.getColumns( getDao(), fieldName )[0];
      } catch ( DataAccessException e ) {
        throw new FieldAccessException( "Unable to set field value!", base,
            fieldType );
      }
    }
    setFieldValue( fieldName, fieldType, value, cached, commit );
  }

  private final <T> void setFieldValue( String fieldName, Class<T> fieldType,
      T value, boolean cached, boolean commit ) throws FieldAccessException {
    synchronized ( cached ? this.cachedFieldValues : new Object() ) {
      getFieldAccessor().setFieldValue( fieldName, fieldType, value, commit );
      if ( cached )
        this.cachedFieldValues.put( fieldName, value );
    }
  }

  /**
   * Registers a field as immediately loaded.
   * 
   * @param fieldName
   *          the field name
   * @param selector
   *          the {@link ColumnSelector}
   * @param fieldType
   *          the field type
   */
  public final void setImmediatelyField( String fieldName,
      ColumnSelector selector, Class<?> fieldType ) {
    this.immediatelyLoadedFields.put( fieldName, new FieldData( fieldName,
        fieldType, selector ) );
  }

  /**
   * Registers a field as immediately loaded.
   * 
   * @param fieldName
   *          the field name
   * @param fieldType
   *          the field type
   * @deprecated Use {@link #setImmediatelyField(String, ColumnSelector, Class)}
   *             instead.
   */
  @Deprecated
  public final void setImmediatelyField( String fieldName, Class<?> fieldType ) {
    setImmediatelyField( fieldName, null, fieldType );
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getDaoClass().getName() + " [" + getDataObject() + "]";
  }

}
