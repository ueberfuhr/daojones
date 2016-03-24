package de.ars.daojones.runtime.test;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TestPlugin extends Plugin {

    /**
     * The plug-in ID.
     */
    public static final String PLUGIN_ID = "de.ars.daojones.runtime.test";

    // The shared instance
    private static TestPlugin plugin;

    /**
     * The constructor
     */
    public TestPlugin() {
        super();
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static TestPlugin getDefault() {
        return plugin;
    }

}
