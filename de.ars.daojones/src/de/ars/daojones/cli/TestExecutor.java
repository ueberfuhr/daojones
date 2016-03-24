package de.ars.daojones.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.model.IConnectionConfigurationBundle;
import de.ars.daojones.connections.model.XMLConfigurationBundle;

/**
 * Executor that reads a connection file and
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class TestExecutor implements CommandExecutor {

    private static final CommandLineParameter FILE = new CommandLineParameter(
            "config",
            "The name of the connection file containing the connections to test.");

    /**
     * @see de.ars.daojones.cli.CommandExecutor#execute(java.io.PrintWriter,
     *      java.lang.String[])
     */
    public void execute(PrintWriter out, String... args) {
        try {
            out.println("Executing test.");
            out.flush();
            final String fileName = FILE.getValue(args);
            if (null == fileName)
                throw new IllegalArgumentException("Parameter \""
                        + FILE.getName() + "\" not found.");
            final File file = new File(fileName);
            out.println("Reading file \"" + file.getAbsolutePath() + "\".");
            out.flush();
            final ApplicationContext ctx = ApplicationContext.getDefault();
            try {
                final IConnectionConfigurationBundle conf = new XMLConfigurationBundle() {

                  @Override
                  protected InputStream getInputStream() throws IOException {
                    return new FileInputStream(file);
                  }
                   
                };
                ctx.createConnections(conf.getConfiguration());
                out.println("Reading connection for TestDao.");
                out.flush();
                final Connection<TestDao> con = Connection.get(ctx,
                        TestDao.class);
                out.println("Executing dummy query.");
                out.flush();
                con.find();
                out.println("Successfully finished test.");
                out.flush();
            } finally {
                ctx.destroy();
            }
        } catch (Throwable t) {
            t.printStackTrace(out);
            out.flush();
        }
    }

    /**
     * @see de.ars.daojones.cli.CommandExecutor#getDescription()
     */
    public String getDescription() {
        return "Reads a connection file and tests the connection by executing a dummy query. "
                + "The connection must be declared as default connection.";
    }

    /**
     * @see de.ars.daojones.cli.CommandExecutor#getParameters()
     */
    public CommandLineParameter[] getParameters() {
        return new CommandLineParameter[] { FILE };
    }

}
