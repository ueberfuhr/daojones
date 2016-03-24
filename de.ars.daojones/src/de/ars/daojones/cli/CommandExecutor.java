package de.ars.daojones.cli;

import java.io.PrintWriter;

/**
 * Executes a command.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface CommandExecutor {

    /**
     * Executes the command.
     * 
     * @param out
     *            the {@link PrintWriter} for writing output
     * @param args
     *            the arguments
     */
    public void execute(PrintWriter out, String... args);

    /**
     * Returns a description of this executor.
     * 
     * @return the executor
     */
    public String getDescription();

    /**
     * Returns the supported command line parameters.
     * 
     * @return the command line parameters
     */
    public CommandLineParameter[] getParameters();

}
