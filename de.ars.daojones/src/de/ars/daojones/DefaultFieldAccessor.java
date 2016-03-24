package de.ars.daojones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;
import de.ars.daojones.runtime.BeanCreationException;
import de.ars.daojones.runtime.BeanFactory;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataObject;

/**
 * A default implementation of {@link FieldAccessor} handling transformation
 * of the primitive types and strings.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class DefaultFieldAccessor implements FieldAccessor {

	private static final long serialVersionUID = -114427679363997676L;
	private final DataObject dataObject;
	private final String applicationContextId;
    
	/**
     * @param dataObject
     */
	DefaultFieldAccessor(final DataObject dataObject, final String applicationContextId) {
        super();
        this.dataObject = dataObject;
        this.applicationContextId = applicationContextId;
    }
    
    private DataObject getDataObject() {
        return this.dataObject;
    }
    
    private String getApplicationContextId() {
    	return this.applicationContextId;
    }

    private ApplicationContext getApplicationContext() {
    	return ApplicationContextFactory.getInstance().getApplicationContext(getApplicationContextId());
    }

	private void _checkParameters(String fieldName, Class<?> fieldType) throws UnsupportedFieldTypeException, FieldAccessException {
		// TODO Java-6-Migration
//		Assert.assertFalse("The implementation of this accessor (class " + getClass().getName() + ") does not support strings!", String.class.isAssignableFrom(fieldType));
		if(String.class.isAssignableFrom(fieldType))
			throw new FieldAccessException("The implementation of this accessor (class " + getClass().getName() + ") does not support strings!", fieldName, fieldType);
	}
	
	/**
	 * @see de.ars.daojones.FieldAccessor#getFieldValue(java.lang.String, java.lang.Class)
	 */
	// TODO Java-6-Migration
	// @Override
	@SuppressWarnings("unchecked")
	public <U>U getFieldValue(String fieldName, Class<U> fieldType) throws UnsupportedFieldTypeException, FieldAccessException {
		_checkParameters(fieldName, fieldType);
		try {
			if(Dao.class.isAssignableFrom(fieldType)) {
		    	return (U)BeanFactory.createBean((Class<Dao>)fieldType, getDataObject(), getApplicationContext());
			} else if(!String.class.isAssignableFrom(fieldType)) {
				final String result = getFieldValue(fieldName, String.class);
				if(fieldType.equals(Boolean.class)) {
					return (U)Boolean.valueOf(result);
				} else if(fieldType.equals(Date.class)) {
					return (U)new SimpleDateFormat().parse(result);
				} else if(fieldType.equals(Integer.class)) {
					return (U)Integer.valueOf(result);
				} else throw new UnsupportedFieldTypeException(fieldName, fieldType);
			} else {
				throw new UnsupportedFieldTypeException(fieldName, fieldType);
			}
		} catch (BeanCreationException e) {
			throw new FieldAccessException(e, fieldName, fieldType);
		} catch (ParseException e) {
			throw new FieldAccessException(e, fieldName, fieldType);
		}
	}

	/**
	 * @see de.ars.daojones.FieldAccessor#setFieldValue(java.lang.String, java.lang.Class, java.lang.Object, boolean)
	 */
	// TODO Java-6-Migration
	// @Override
	public <U>void setFieldValue(String fieldName, Class<U> fieldType, U value, boolean commit) throws UnsupportedFieldTypeException, FieldAccessException {
		try {
			_checkParameters(fieldName, fieldType);
			setFieldValue(fieldName, String.class, value != null ? value.toString() : null, commit);
		} catch (FieldAccessException e) {
			throw new FieldAccessException(e.getCause(), fieldName, fieldType);
		}
	}

}
