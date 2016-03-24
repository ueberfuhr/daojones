package de.ars.daojones.connections.model.io;

import java.io.IOException;
import java.io.InputStream;

import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A source for reading and writing {@link IConnectionConfiguration}s
 * from inputstreams that are resolved from a {@link ClassLoader}.
 * You can specify a resource name to resolve a resource relatively when loading.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ClassLoaderConnectionConfigurationSource extends XmlInputStreamConnectionConfigurationSource implements IReadableConnectionConfigurationSource {

	private final ClassLoader classLoader;
	private final String resourceName;
	
	/**
	 * Creates an instance.
	 * @param classLoader the class loader
	 * @param resourceName the resource name (can be null, if you want to resolve resources absolutely)
	 */
	public ClassLoaderConnectionConfigurationSource(ClassLoader classLoader, String resourceName) {
		super();
		this.classLoader = classLoader;
		this.resourceName = resourceName;
	}
	/**
	 * Creates an instance.
	 * @param c the class with the class loader
	 * @param resourceName the resource name (can be null, if you want to resolve resources absolutely)
	 */
	public ClassLoaderConnectionConfigurationSource(Class<?> c, String resourceName) {
		this(c.getClassLoader(), resourceName);
	}
	
	/**
	 * @see de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource#getInputStream(java.lang.String)
	 */
	@Override
	protected InputStream getInputStream(String resource) throws IOException {
		return classLoader.getResourceAsStream((null != resourceName ? resourceName + "/" : "") + resource);
	}

}
