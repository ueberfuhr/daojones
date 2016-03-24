package de.ars.daojones.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class providing information about runtime properties.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class Settings {

    private static final Logger logger = Logger.getLogger(Settings.class
            .getName());
    private static final String CONFIG_FILE = "runtime.properties";
    private static Settings theInstance;
    private Properties props;

    private Settings() {
        super();
    }

    /**
     * Returns the instance.
     * 
     * @return the instance
     */
    public static synchronized Settings getInstance() {
        if (null == theInstance) {
            theInstance = new Settings();
        }
        return theInstance;
    }

    private Properties getProperties() {
        synchronized (this) {
            if (null == this.props) {
                this.props = new Properties();
                try {
                    final InputStream in = Settings.class
                            .getResourceAsStream(CONFIG_FILE);
                    try {
                        this.props.load(in);
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    logger
                            .log(
                                    Level.WARNING,
                                    "Unable to read configuration for the DaoJones runtime!",
                                    e);
                }
            }
        }
        return this.props;
    }

    /**
     * Returns the value of a given setting.
     * 
     * @param name
     *            the name of the value
     * @return the value or null, if not existing
     */
    public String get(String name) {
        return getProperties().getProperty(name);
    }

}
