package de.ars.daojones.connections;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.runtime.DataAccessException;

/**
 * A factory that creates {@link ApplicationContext} instances
 * by a given id.
 * This class is a singleton.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ApplicationContextFactory {

	private static ApplicationContextFactory theInstance;
    private final Map<String, ApplicationContext> contexts = new HashMap<String, ApplicationContext>();
	
	private ApplicationContextFactory() {
		super();
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public static synchronized ApplicationContextFactory getInstance() {
		if(null == theInstance){
			theInstance = new ApplicationContextFactory();
		}
		return theInstance;
	}
    
	/**
	 * Returns an {@link ApplicationContext} instance by a given id.
	 * If no such {@link ApplicationContext} exists for the id, it is created.
	 * @param id the id
	 * @return the {@link ApplicationContext} instance
	 */
    public synchronized ApplicationContext getApplicationContext(final String id) {
        final ApplicationContext ctx = contexts.get(id);
        return null != ctx && ctx.isValid() ? ctx : createApplicationContext(id);
    }
    
	private synchronized ApplicationContext createApplicationContext(final String id) {
		final ApplicationContext result = new ApplicationContext(id) {
			@Override
			public void destroy() throws DataAccessException {
				super.destroy();
				contexts.remove(id);
			}
		};
		return result;
	}
	
	void register(ApplicationContext ctx) {
		assert null == contexts.get(ctx.getId());
		contexts.put(ctx.getId(), ctx);
	}
	
	
}
