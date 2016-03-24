package de.ars.daojones;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * A manager class containing information about inheritations from class to
 * class. This is used by the DaoJones runtime to optionally search connections
 * for subclasses.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class Inheritations {

	/**
	 * The simple file name of the inheritations file that should be used by
	 * default.
	 * 
	 * @deprecated This may occur in conflicts when using multiple modules
	 *             loaded by one classloader.
	 */
	@Deprecated
	public static final String DEFAULT_INHERITATIONS_FILE = "daojones.inheritations";
	private Map<String, InheritationsEntry> subclasses = new HashMap<String, InheritationsEntry>();

	/**
	 * Removes all class entries.
	 */
	public void clear() {
		subclasses.clear();
	}

	/**
	 * Returns a collection of all classes that can be registered as sub- or
	 * superclasses.
	 * 
	 * @return the names of the classes
	 */
	public Collection<String> getRegisteredClasses() {
		final Collection<String> registeredClasses = new HashSet<String>();
		registeredClasses.addAll(subclasses.keySet());
		for (InheritationsEntry values : subclasses.values())
			registeredClasses.addAll(toNonGenericClass(values.getSubClasses()));
		return Collections.unmodifiableCollection(registeredClasses);
	}

	private static String toNonGenericClass(String name) {
		if (null == name)
			return null;
		final int idx = name.indexOf('<');
		return idx < 0 ? name : name.substring(0, idx);
	}

	private static Collection<String> toNonGenericClass(Collection<String> names) {
		final Collection<String> result = new LinkedList<String>();
		for (String name : names)
			result.add(toNonGenericClass(name));
		return result;
	}

	/**
	 * Loads a file from an {@link InputStream}.
	 * 
	 * @param in
	 *            the {@link InputStream}
	 * @throws IOException
	 *             if reading inheritations from the {@link InputStream} fails
	 */
	// @SuppressWarnings("unchecked")
	public void load(InputStream in) throws IOException {
		final XmlSerializableInheritations inh = JAXBUtilities.read(in,
				XmlSerializableInheritations.class);
		this.subclasses.clear();
		this.subclasses.putAll(inh.getMap());
		// ObjectInputStream ois = null;
		// try {
		// try {
		// ois = new ObjectInputStream(in);
		// subclasses = (Map<String, Collection<String>>)ois.readObject();
		// } finally {
		// if(ois != null) ois.close();
		// }
		// } catch (ClassNotFoundException e) {
		// // TODO Java6-Migration
		// //throw new IOException(e);
		// throw new IOException(e.getMessage());
		// }
	}

	/**
	 * Saves the inheritations configuration to an {@link OutputStream}.
	 * 
	 * @param out
	 *            the {@link OutputStream}
	 * @throws IOException
	 *             if saving inheritations to the {@link OutputStream} fails
	 */
	public void save(OutputStream out) throws IOException {
		JAXBUtilities.write(new XmlSerializableInheritations(this.subclasses),
				out);
		// final ObjectOutputStream oos = new ObjectOutputStream(out);
		// try {
		// oos.writeObject(subclasses);
		// } finally {
		// oos.close();
		// }
	}

	/**
	 * Registers an inheritation from a superclass to a subclass.
	 * 
	 * @param superClass
	 *            the name of the superclass
	 * @param subclass
	 *            the name of the subclass
	 */
	public void registerInheritation(String superClass, String subclass) {
		String key = toNonGenericClass(null == superClass ? Object.class.getName() : superClass);
		String value = toNonGenericClass(subclass);
		boolean found = false;
		for (Map.Entry<String, InheritationsEntry> entry : subclasses
				.entrySet()) {
			if (key.equals(toNonGenericClass(entry.getKey()))) {
				entry.getValue().getSubClasses().add(value);
				found = true;
			} else {
				entry.getValue().getSubClasses().remove(value);
			}
		}
		// if not found yet
		if (!found) {
			final InheritationsEntry values = new InheritationsEntry();
			values.setSuperClassName(key);
			values.getSubClasses().add(value);
			subclasses.put(key, values);
		}
	}

	/**
	 * Removes all declarations from a special class. This includes all
	 * subclasses of the class.
	 * 
	 * @param className
	 *            the name of the class
	 */
	public void removeClass(String className) {
		if (null == className)
			return;
		className = toNonGenericClass(className);
		final Collection<String> subclassesToRemove = new HashSet<String>();
		for (Iterator<Map.Entry<String, InheritationsEntry>> it = subclasses
				.entrySet().iterator(); it.hasNext();) {
			final Map.Entry<String, InheritationsEntry> entry = it.next();
			if (className.equals(toNonGenericClass(entry.getKey()))) {
				it.remove();
			} else {
				entry.getValue().getSubClasses().remove(className);
			}
		}
		for (String toRemove : subclassesToRemove)
			removeClass(toRemove);
	}

	/**
	 * Returns all subclasses of the given class.
	 * 
	 * @param className
	 *            the name of the superclass
	 * @return all subclasses of the given class
	 */
	public Collection<String> getSubclasses(String className) {
		final InheritationsEntry entry = subclasses.get(className);
		return null != entry ? toNonGenericClass(entry.getSubClasses()) : null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, InheritationsEntry> entry : subclasses
				.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":\n");
			for (String s : entry.getValue().getSubClasses()) {
				sb.append("\t- ");
				sb.append(s);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
