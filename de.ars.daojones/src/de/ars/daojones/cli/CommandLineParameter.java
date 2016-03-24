package de.ars.daojones.cli;

/**
 * A parameter description for the command line.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CommandLineParameter {

    private final String name;
    private final String description;

    /**
     * Creates an instance.
     * 
     * @param name
     *            the command name
     * @param description
     *            the command description
     */
    public CommandLineParameter(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the command name.
     * 
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the command description.
     * 
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the value of the parameter.
     * 
     * @param args
     *            the command line parameters
     * @return the value of the parameter
     */
    public String getValue(String... args) {
        final String prefix = getName() + "=";
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        return null;
    }
}
