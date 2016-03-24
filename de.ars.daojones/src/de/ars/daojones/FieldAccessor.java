package de.ars.daojones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.EmbeddedObject;
import de.ars.daojones.runtime.Identificator;

/**
 * A field accessor is an object that is responsible for
 * reading and updating properties of a DaoJones bean.
 * A field accessor must support the following types of properties:
 * <ul>
 *   <li>All primitive types and their wrappers.</li>
 *   <li>The interfaces of the Collections API and their default implementations ({@link Vector}, {@link LinkedList}, {@link ArrayList}, {@link HashSet}, {@link TreeSet})</li>
 *   <li>{@link String} and {@link String}-Arrays</li>
 *   <li>{@link Date}</li>
 *   <li>{@link Identificator}</li>
 *   <li>{@link EmbeddedObject}</li>
 * </ul>
 * Special driver field accessors can provide further types, but make the usage driver-dependent.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface FieldAccessor extends Serializable {

	/**
	 * Reads a field from the database.
	 * @param <U> the field type
	 * @param fieldName the field name
	 * @param fieldType the field class
	 * @return the value of the field
	 * @throws UnsupportedFieldTypeException if the field type is not a supported type
	 * @throws FieldAccessException if accessing the database failed
	 */
	public <U>U getFieldValue(String fieldName, Class<U> fieldType) throws UnsupportedFieldTypeException, FieldAccessException;
	/**
	 * Updates a field in the database.
	 * @param <U> the field type
	 * @param fieldName the field name
	 * @param fieldType the field class
	 * @param value the value of the field
	 * @param commit a flag indicating whether to commit the change immediately - do not set this to true when calling 
	 *    this method within the {@link Dao#onUpdate()} or {@link Dao#onUpdate()} method!
	 * @throws UnsupportedFieldTypeException if the field type is not a supported type
	 * @throws FieldAccessException if accessing the database failed
	 */
	public <U>void setFieldValue(String fieldName, Class<U> fieldType, U value, boolean commit) throws UnsupportedFieldTypeException, FieldAccessException; 
	
}
