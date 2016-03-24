package de.ars.daojones.cli;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class providing a main method for testing purposes.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CommandLineTool {

    private static final Set<String> HELP_COMMANDS = new HashSet<String>(Arrays
            .asList("?", "/?", "help", "-help", "-h"));

    private static final Map<String, CommandExecutor> EXECUTORS = new HashMap<String, CommandExecutor>();

    static {
        addExecutor("test", new TestExecutor());
    }

    /**
     * Registers a {@link CommandExecutor} to the CLI.
     * 
     * @param command
     *            the value of the first parameter identifying the command
     * @param executor
     *            the {@link CommandExecutor}
     */
    public static void addExecutor(String command, CommandExecutor executor) {
        EXECUTORS.put(command, executor);
    }

    private static void printIntro() {
        System.out.println();
        System.out.println("DaoJones Command Line Tool");
        System.out.println("==========================");
        System.out.println();
    }

    private static void checkHelpCommand(String[] args) {
        if (args.length < 1 || args.length > 0
                && HELP_COMMANDS.contains(args[0])) {
            System.out
                    .println("Call this application with the following options:");
            System.out.println("<METHOD> [PARAMETERNAME=PARAMETERVALUE]");
            System.out.println();
            System.out.println("Currently supported methods and parameters:");
            System.out.println("-------------------------------------------");
            for (Map.Entry<String, CommandExecutor> entry : EXECUTORS
                    .entrySet()) {
                System.out.print(entry.getKey());
                System.out.print(":\t");
                System.out.print(entry.getValue().getDescription());
                for (CommandLineParameter param : entry.getValue()
                        .getParameters()) {
                    System.out.print("\n  ");
                    System.out.print(param.getName());
                    System.out.print("=<VALUE>:\t");
                    System.out.print(param.getDescription());
                }
            }
            System.out.println();
            System.out.println();
        }
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        printIntro();
        checkHelpCommand(args);
        if (args.length > 0) {
            final String command = args[0];
            System.out.println("Command is " + command);
            if (null != command) {
                final CommandExecutor executor = EXECUTORS.get(command);
                System.out.println("Executor is " + executor);
                if (null != executor) {
                    executor.execute(new PrintWriter(System.out), args);
                }
            }
        }
    }

}
