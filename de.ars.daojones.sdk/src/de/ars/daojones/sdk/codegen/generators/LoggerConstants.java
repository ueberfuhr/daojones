package de.ars.daojones.sdk.codegen.generators;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utilitiy class containing logging helpers.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class LoggerConstants {

    /**
     * Use DEBUG to log information to the console that is usually helpful for debugging issues. 
     */
    public static Level DEBUG = Level.INFO;
    
    /**
     * Use WARNING to log error cases to the console that are not disturbing the current runtime but may result in unwanted behaviour. 
     */
    public static Level WARNING = Level.WARNING;

    /**
     * Use ERROR to log error cases to the console that disturb the current runtime. 
     */
    public static Level ERROR = Level.SEVERE;
    
    /**
     * Returns the logger for this subsystem.
     * @return the logger
     */
    public static Logger getLogger() {
        return Logger.getLogger(LoggerConstants.class.getPackage().getName());
    }
    
    /**
     * Use this method to test if a logging output should be created.
     * This is important for expensive loggings.
     * @param level the level to test
     * @return true if a logging with the given level will be written out
     */
    public static boolean isLoggable(Level level) {
        return getLogger().isLoggable(level);
    }
    
}
