package de.ars.daojones.drivers.notes;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.UnsupportedFieldTypeException;

/**
 * A utility class transforming Notes to Java types and back.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class TypeMapper {

	private static final Logger logger = Logger.getLogger(TypeMapper.class
			.getName());
	private static final DateFormat DATEFORMAT = new SimpleDateFormat();

	// public static <T> T notes2Java(Class<T> javaType, Object notesValue)
	// throws UnsupportedFieldTypeException {
	// if(null == notesValue) return null;
	//		
	// return null;
	// }
	//	
	// public static <T> Object java2Notes(Class<T> javaType, T javaValue)
	// throws UnsupportedFieldTypeException {
	// if(null == javaValue) return null;
	//		
	// return null;
	// }

//	public static void main(String[] args) throws UnsupportedFieldTypeException {
//		final String[] result = java2Java("Hallo", String[].class);
//		System.out.println(result);
//		final String result2 = java2Java(new String[]{"Hallo"}, String.class);
//		System.out.println(result2);
//		final int result3 = java2Java(new String[]{"53"}, Integer.class);
//		System.out.println(result3);
//	}
//
	/**
	 * Transforms one java type to another. Allowed are the wrapper types for
	 * the primitive types, String, Collections and Arrays of them. Please note
	 * that generic connections cannot be handled.
	 * 
	 * @param <T>
	 *            the type of the target value
	 * @param sourceValue
	 *            the value that should be transformed
	 * @param targetType
	 *            the type to transform into
	 * @return the transformed value
	 * @throws UnsupportedFieldTypeException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T java2Java(Object sourceValue, Class<T> targetType)
			throws UnsupportedFieldTypeException {
		if (null == sourceValue)
			return null;
		final Class<?> sourceType = sourceValue.getClass();
		if (targetType.isArray() || sourceType.isArray()
				|| Collection.class.isAssignableFrom(targetType)
				|| Collection.class.isAssignableFrom(sourceType)) {
			// if array or collection -> ...
			// transform sourceValue to Array
			final Object[] sourceArray = sourceType.isArray() ? (Object[]) sourceValue
					: (Collection.class.isAssignableFrom(sourceType) ? ((Collection) sourceValue)
							.toArray(new Object[((Collection) sourceValue)
									.size()])
							: new Object[] { sourceValue });
			final Class<?> targetElementType = targetType.isArray() ? targetType
					.getComponentType()
					: (Collection.class.isAssignableFrom(targetType) ? Object.class
							: targetType);
			final boolean isTargetSingle = !(targetType.isArray() || Collection.class
					.isAssignableFrom(targetType));
			final Collection<Object> result = new LinkedList<Object>();
			for (Object o : sourceArray) {
				final Object oo = java2Java(o, targetElementType);
				if (isTargetSingle)
					return (T) oo;
				result.add(oo);
			}
			// Iterate and transform simple value
			if (targetType.isArray()) {
				// convert to T
				if (targetElementType.isAssignableFrom(String.class))
					return (T) result.toArray(new String[result.size()]);
				if (targetElementType.isAssignableFrom(Boolean.class))
					return (T) result.toArray(new Boolean[result.size()]);
				if (targetElementType.isAssignableFrom(Date.class))
					return (T) result.toArray(new Date[result.size()]);
				// numbers
				if (targetElementType.isAssignableFrom(Integer.class))
					return (T) result.toArray(new Integer[result.size()]);
				if (targetElementType.isAssignableFrom(Float.class))
					return (T) result.toArray(new Float[result.size()]);
				if (targetElementType.isAssignableFrom(Long.class))
					return (T) result.toArray(new Long[result.size()]);
				if (targetElementType.isAssignableFrom(Short.class))
					return (T) result.toArray(new Short[result.size()]);
				if (targetElementType.isAssignableFrom(Byte.class))
					return (T) result.toArray(new Byte[result.size()]);
				if (targetElementType.isAssignableFrom(Double.class))
					return (T) result.toArray(new Double[result.size()]);
				// do not allow other number subclasses
				if (Number.class.getName().equals(targetElementType.getName()))
					return (T) result.toArray(new Number[result.size()]);
			}
			if (targetType.isAssignableFrom(TreeSet.class))
				return (T) new TreeSet<Object>(result);
			if (targetType.isAssignableFrom(HashSet.class))
				return (T) new HashSet<Object>(result);
			if (targetType.isAssignableFrom(LinkedList.class))
				return (T) new LinkedList<Object>(result);
			if (targetType.isAssignableFrom(ArrayList.class))
				return (T) new ArrayList<Object>(result);
			if (List.class.getName().equals(targetType.getName()))
				return (T) new ArrayList<Object>(result);
			if (Set.class.getName().equals(targetType.getName()))
				return (T) new HashSet<Object>(result);
			if (Collection.class.getName().equals(targetType.getName()))
				return (T) new ArrayList<Object>(result);
		} else {
			// simple type mapping
			if (targetType.isAssignableFrom(sourceValue.getClass()))
				return (T) sourceValue;
			// convert to string
			final String value$ = sourceValue instanceof Date ? DATEFORMAT
					.format((Date) sourceValue) : sourceValue.toString();
			try {
				// convert to T
				if (targetType.isAssignableFrom(String.class))
					return (T) value$;
				if (targetType.isAssignableFrom(Boolean.class))
					return (T) (Boolean) Boolean.parseBoolean(value$);
				if (targetType.isAssignableFrom(Date.class))
					return (T) DATEFORMAT.parse(value$);
				// numbers
				if (targetType.isAssignableFrom(Integer.class))
					return (T) (Integer) Integer.parseInt(value$);
				if (targetType.isAssignableFrom(Float.class))
					return (T) (Float) Float.parseFloat(value$);
				if (targetType.isAssignableFrom(Long.class))
					return (T) (Long) Long.parseLong(value$);
				if (targetType.isAssignableFrom(Short.class))
					return (T) (Short) Short.parseShort(value$);
				if (targetType.isAssignableFrom(Byte.class))
					return (T) (Byte) Byte.parseByte(value$);
				if (targetType.isAssignableFrom(Double.class))
					return (T) (Double) Double.parseDouble(value$);
				// do not allow other number subclasses
				if (Number.class.getName().equals(targetType.getName()))
					return (T) (Double) Double.parseDouble(value$);
			} catch (ParseException e) {
				logger.log(Level.WARNING, "Unable to parse the value \""
						+ sourceValue + "\" to type " + targetType
						+ "! (string value is \"" + value$ + "\")");
				return null;
			}
		}

		// TODO
		/*
		 * from to: Number/Boolean/Date/Collection +Arrays
		 */
		/*
		 * SIMPLE -> SIMPLE -> cast SIMPLE -> ARRAY -> cast + wrapping in ein
		 * Array ARRAY -> SIMPLE -> nur erstes Element herauspicken ARRAY ->
		 * ARRAY -> jedes Element casten
		 */
		throw new UnsupportedFieldTypeException(null, targetType);
	}

}
