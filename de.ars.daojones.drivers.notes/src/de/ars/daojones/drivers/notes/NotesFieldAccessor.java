package de.ars.daojones.drivers.notes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;
import de.ars.daojones.FieldAccessException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.UnsupportedFieldTypeException;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;
import de.ars.daojones.runtime.BeanCreationException;
import de.ars.daojones.runtime.BeanFactory;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.EmbeddedObject;
import de.ars.daojones.runtime.Identificator;

class NotesFieldAccessor implements FieldAccessor {

    private static final long serialVersionUID = -9106007904048564398L;
    private final NotesDocumentObject dataObject;
    private final String applicationContextId;

    /**
     * @param dataObject
     */
    NotesFieldAccessor(final NotesDocumentObject dataObject,
            final String applicationContextId) {
        super();
        this.dataObject = dataObject;
        this.applicationContextId = applicationContextId;
    }

    private NotesDocumentObject getDataObject() {
        return this.dataObject;
    }

    private String getApplicationContextId() {
        return this.applicationContextId;
    }

    private ApplicationContext getApplicationContext() {
        return ApplicationContextFactory.getInstance().getApplicationContext(
                getApplicationContextId());
    }

    @SuppressWarnings("unchecked")
    private static Collection<?> createCollection(
            Class<? extends Collection> fieldType)
            throws IllegalAccessException, InstantiationException {
        if ((Modifier.ABSTRACT & fieldType.getModifiers()) > 0) {
            if (List.class.isAssignableFrom(fieldType)) {
                fieldType = LinkedList.class;
            } else if (SortedSet.class.isAssignableFrom(fieldType)) {
                fieldType = TreeSet.class;
            } else {
                fieldType = HashSet.class;
            }
        }
        ;
        return fieldType.newInstance();
    }

    @SuppressWarnings("unchecked")
    public <U> U getFieldValue(String fieldName, Class<U> fieldType)
            throws UnsupportedFieldTypeException, FieldAccessException {
        try {
            if (null == getDataObject())
                return null;
            final Document doc = getDataObject().getDocument();
            if (null == doc)
                throw new FieldAccessException(
                        "The data object does not contain a NotesDocument!",
                        fieldName, fieldType);
            if (!doc.hasItem(fieldName))
                return null;
            if (fieldType.equals(Integer.class)
                    || fieldType.equals(Integer.TYPE)) {
                return (U) new Integer(doc.getItemValueInteger(fieldName));
            } else if (fieldType.equals(Byte.class)
                    || fieldType.equals(Byte.TYPE)) {
                return (U) new Byte((byte) doc.getItemValueInteger(fieldName));
            } else if (fieldType.equals(Short.class)
                    || fieldType.equals(Short.TYPE)) {
                return (U) new Short((short) doc.getItemValueInteger(fieldName));
            } else if (fieldType.equals(Double.class)
                    || fieldType.equals(Double.TYPE)) {
                return (U) new Double(doc.getItemValueDouble(fieldName));
            } else if (fieldType.equals(Long.class)
                    || fieldType.equals(Long.TYPE)) {
                return (U) new Long((long) doc.getItemValueDouble(fieldName));
            } else if (fieldType.equals(Float.class)
                    || fieldType.equals(Float.TYPE)) {
                return (U) new Float(doc.getItemValueDouble(fieldName));
            } else if (fieldType.equals(Boolean.class)
                    || fieldType.equals(Boolean.TYPE)) {
                return (U) Boolean.valueOf(doc.getItemValueString(fieldName));
            } else if (fieldType.equals(Character.class)
                    || fieldType.equals(Character.TYPE)) {
                final String s = doc.getItemValueString(fieldName);
                return (U) (null != s && s.length() > 0 ? new Character(s
                        .charAt(0)) : null);
            } else if (fieldType.equals(String.class)) {
                return (U) doc.getItemValueString(fieldName);
            } else if (fieldType.isAssignableFrom(Identificator.class)) {
                return (U) new NotesDocumentIdentificator(getFieldValue(
                        fieldName, String.class));
            } else if (fieldType.equals(Date.class)) {
                // empty date fields have the type "text"
                final Item item = doc.getFirstItem(fieldName);
                if (item.getType() != Item.DATETIMES)
                    return null;
                // read date field
                final Object datetime = doc
                        .getItemValueDateTimeArray(fieldName).get(0);
                if (null == datetime) {
                    return null;
                } else if (datetime instanceof lotus.domino.cso.DateTime) {
                    return (U) ((lotus.domino.cso.DateTime) datetime)
                            .toJavaDate();
                } else if (datetime instanceof lotus.domino.local.DateTime) {
                    return (U) ((lotus.domino.local.DateTime) datetime)
                            .toJavaDate();
                }
                ;
                throw new FieldAccessException(
                        "Datetime field returned wrong format: "
                                + datetime.getClass().getName() + "!",
                        fieldName, fieldType);
            } else if (fieldType.equals(Vector.class)) {
                return (U) doc.getItemValue(fieldName);
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                final Collection result = createCollection((Class<Collection>) fieldType);
                result.addAll(doc.getItemValue(fieldName));
                return (U) result;
            } else if (double[].class.isAssignableFrom(fieldType)) {
                final Vector<Double> v = doc.getItemValue(fieldName);
                final double[] result = new double[null != v ? v.size() : 0];
                if (null != v) {
                    int idx = 0;
                    for (Double d : v) {
                        result[idx] = d;
                        idx++;
                    }
                }
                return (U) result;
            } else if (String[].class.isAssignableFrom(fieldType)) {
                final List<String> result = getFieldValue(fieldName, List.class);
                if (null == result)
                    return null;
                return (U) result.toArray(new String[result.size()]);
            } else if (EmbeddedObject.class.isAssignableFrom(fieldType)) {
                final MIMEEntity entity = doc.getMIMEEntity(fieldName);
                if (null != entity)
                    return (U) new NotesEmbeddedMIMEObject(entity,
                            getDataObject());
                final lotus.domino.EmbeddedObject notesObj = getFieldValue(
                        fieldName, lotus.domino.EmbeddedObject.class);
                if (null != notesObj)
                    return (U) new NotesEmbeddedObject(notesObj);
                return null;
            } else if (lotus.domino.EmbeddedObject.class
                    .isAssignableFrom(fieldType)) {
                final Item item = doc.getFirstItem(fieldName);
                if (item instanceof RichTextItem) {
                    final RichTextItem rt = (RichTextItem) doc
                            .getFirstItem(fieldName);
                    Vector v = null;
                    if (rt != null)
                        v = rt.getEmbeddedObjects();
                    if (v != null && !v.isEmpty())
                        return (U) v.firstElement();
                    v = doc.getEmbeddedObjects();
                    if (v != null && !v.isEmpty())
                        return (U) v.firstElement();
                    v = doc.getItems();
                    for (Enumeration<?> e = v.elements(); e.hasMoreElements();) {
                        final Item eItem = (Item) e.nextElement();
                        if (eItem.getName().toUpperCase().equals("$FILE")
                                && eItem.getType() == Item.ATTACHMENT)
                            return (U) doc
                                    .getAttachment(eItem.getValueString());
                    }
                }
                return null;
            } else if (Dao.class.isAssignableFrom(fieldType)) {
                return (U) BeanFactory.createBean((Class<Dao>) fieldType,
                        getDataObject(), getApplicationContext());
            } else {
                throw new UnsupportedFieldTypeException(fieldName, fieldType);
            }
        } catch (BeanCreationException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        } catch (NotesException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        } catch (InstantiationException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        }
    }

    /**
     * @see de.ars.daojones.FieldAccessor#setFieldValue(String, Class, Object,
     *      boolean)
     */
    // TODO Java-6-Migration
    // @Override
    @SuppressWarnings("unchecked")
    public <U> void setFieldValue(String fieldName, Class<U> fieldType,
            U value, boolean commit) throws UnsupportedFieldTypeException,
            FieldAccessException {
        try {
            if (null == getDataObject())
                throw new FieldAccessException(
                        "Cannot save because of missing data object!",
                        fieldName, fieldType);
            final Document doc = getDataObject().getDocument();
            if (null == doc)
                throw new FieldAccessException(
                        "The document was not saved before!", fieldName,
                        fieldType);
            if (null == value) {
                doc.removeItem(fieldName);
            } else if (fieldType.equals(String.class)) {
                if (null != value
                        && value.toString().indexOf(
                                System.getProperty("line.separator")) >= 0) {
                    setFieldValue(fieldName, String[].class, value.toString()
                            .split(
                                    System.getProperty("line.separator")
                                            .replaceAll("\\\\", "\\\\")),
                            commit);
                } else {
                    doc.replaceItemValue(fieldName, value);
                }
            } else if (fieldType.equals(String[].class)) {
                // DEACTIVATED because of problems with local access
                // doc.replaceItemValue(fieldName, value);
                setFieldValue(fieldName, Vector.class, new Vector(Arrays
                        .asList((String[]) value)), commit);

                // doc.removeItem(fieldName);
                // final RichTextItem item = doc.createRichTextItem(fieldName);
                // boolean firstLine = true;
                // for(String line : (String[])value) {
                // if(!firstLine) item.addNewLine();
                // item.appendText(line);
                // firstLine = false;
                // }
                // doc.replaceItemValue(fieldName, item);
                // item.update();
            } else if (fieldType.equals(Boolean.class)
                    || fieldType.equals(Boolean.TYPE)) {
                doc.replaceItemValue(fieldName, value.toString());
            } else if (fieldType.isPrimitive()
                    || Number.class.isAssignableFrom(fieldType)) {
                doc.replaceItemValue(fieldName, value);
            } else if (Date.class.isAssignableFrom(fieldType)) {
                try {
                    final lotus.domino.DateTime date = getDataObject()
                            .getConnector().getSession().createDateTime(
                                    (Date) value);
                    doc.replaceItemValue(fieldName, date);
                } catch (NotesException e) {
                    throw new FieldAccessException(e, fieldName, fieldType);
                }
            } else if (EmbeddedObject.class.isAssignableFrom(fieldType)) {
                final EmbeddedObject eo = (EmbeddedObject) value;
                final Item i = doc.getFirstItem(fieldName);
                if (i instanceof RichTextItem) {
                    final RichTextItem rt = (RichTextItem) i;
                    // first, remove all attachments
                    Vector<lotus.domino.EmbeddedObject> v = null;
                    if (rt != null)
                        v = (Vector<lotus.domino.EmbeddedObject>) rt
                                .getEmbeddedObjects();
                    if (v.isEmpty())
                        v = doc.getEmbeddedObjects();
                    for (lotus.domino.EmbeddedObject obj : v) {
                        obj.remove();
                        obj.recycle();
                    }
                    ;
                }
                // Do not delete attachments, they might be part of another
                // field!!!
                // try {
                // final Vector v = doc.getItems();
                // for(Enumeration<?> e = v.elements(); e.hasMoreElements();) {
                // final Item item = (Item)e.nextElement();
                // if(item.getName().toUpperCase().equals("$FILE") &&
                // item.getType() == Item.ATTACHMENT) {
                // ((lotus.domino.EmbeddedObject)doc.getAttachment(item.getValueString())).remove();
                // }
                // }
                // } catch (Throwable t) {
                // // Just for this case ;-)
                // getLogger().log(WARNING,
                // "Unable to delete attachments from document " + doc + "!",
                // t);
                // }
                doc.removeItem(fieldName);
                // second, insert EmbeddedObject
                final Session session = doc.getParentDatabase().getParent();
                final boolean isConvertMime = session.isConvertMIME();
                session.setConvertMIME(false);
                try {
                    final MIMEEntity root = doc.createMIMEEntity(fieldName);
                    try {
                        final Stream rootStream = session.createStream();
                        try {
                            rootStream
                                    .writeText("This is a multipart message in MIME format.");
                            root.setContentFromText(rootStream,
                                    "multipart/related", MIMEEntity.ENC_NONE);
                        } finally {
                            rootStream.close();
                        }
                        final MIMEEntity childEntity = root.createChildEntity();
                        childEntity.createHeader("Content-Length")
                                .setHeaderVal(
                                        new Integer(eo.getLength()).toString());
                        final InputStream in = eo.getInputStream();
                        try {
                            final Stream stream = session.createStream();
                            try {
                                stream.setContents(in);
                                childEntity.setContentFromBytes(stream, eo
                                        .getContentType(),
                                        MIMEEntity.ENC_EXTENSION);
                            } finally {
                                stream.close();
                            }
                        } finally {
                            in.close();
                        }
                        // Call fileMIME.SetContentFromBytes(fileStream,
                        // contentType, 1731)
                    } finally {
                        doc.closeMIMEEntities(true);
                    }
                } finally {
                    if (isConvertMime)
                        session.setConvertMIME(isConvertMime);
                }
            } else if (Vector.class.isAssignableFrom(fieldType)) {
                doc.replaceItemValue(fieldName, value);
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                final Vector<String> vector = new Vector<String>();
                vector.addAll((Collection) value);
                doc.replaceItemValue(fieldName, vector);
            } else {
                doc.replaceItemValueCustomData(fieldName, value);
            }
        } catch (NotesException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        } catch (IOException e) {
            throw new FieldAccessException(e, fieldName, fieldType);
        }
    }

}
