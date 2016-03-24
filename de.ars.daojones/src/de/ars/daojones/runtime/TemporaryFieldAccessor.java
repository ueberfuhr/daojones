package de.ars.daojones.runtime;

import java.util.ArrayList;
import java.util.List;

import de.ars.daojones.FieldAccessException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.UnsupportedFieldTypeException;

class TemporaryFieldAccessor implements FieldAccessor {

	private static final long serialVersionUID = -6862939283420732444L;
	private final DataObjectContainer parent;
	private List<FieldUpdateRequest<?>> requests = new ArrayList<FieldUpdateRequest<?>>();
	private boolean commitLastChange = false;
	
	public TemporaryFieldAccessor(DataObjectContainer parent) {
		super();
		this.parent = parent;
	}

	/**
	 * @see de.ars.daojones.FieldAccessor#getFieldValue(java.lang.String, java.lang.Class)
	 */
	// TODO Java-6-Migration
	// @Override
	@SuppressWarnings("unchecked")
	public <U> U getFieldValue(String fieldName, Class<U> fieldType)throws UnsupportedFieldTypeException, FieldAccessException {
		for(int i=requests.size()-1; i>=0; i--) {
			final FieldUpdateRequest<?> req = requests.get(i);
			final Field<?> field = req.getField();
			if(field.getName().equals(fieldName)) {
				if(fieldType.isAssignableFrom(field.getType())) {
					return (U) req.getValue();
				} else {
					// TODO support transformations
					throw new UnsupportedFieldTypeException(fieldName, fieldType);
				}
			}
		}
		return null;
	}

	/**
	 * @see de.ars.daojones.FieldAccessor#setFieldValue(String, Class, Object, boolean)
	 */
	// TODO Java-6-Migration
	// @Override
	public <U> void setFieldValue(String fieldName, Class<U> fieldType, U value, boolean commit) throws UnsupportedFieldTypeException, FieldAccessException {
		final Field<U> field = new Field<U>(fieldName, fieldType);
		final FieldUpdateRequest<U> req = new FieldUpdateRequest<U>(field, value);
		requests.add(req);
		commitLastChange = commit;
		if(commit) {
			try {
				parent.update();
			} catch (DataAccessException e) {
				throw new FieldAccessException(e, fieldName, fieldType);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	void flush(FieldAccessor accessor) throws UnsupportedFieldTypeException, FieldAccessException {
		// do not optimize!!! could occur in inconsistent states
		for(int i=0; i<requests.size(); i++) {
			final FieldUpdateRequest<Object> req = (FieldUpdateRequest<Object>) requests.get(i);
			accessor.setFieldValue(req.getField().getName(), req.getField().getType(), req.getValue(), (i == requests.size()-1) && commitLastChange);
		}
		requests.clear();
		commitLastChange = false;
	}

}
