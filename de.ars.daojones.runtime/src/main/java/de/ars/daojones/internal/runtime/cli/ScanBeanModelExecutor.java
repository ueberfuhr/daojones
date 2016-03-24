package de.ars.daojones.internal.runtime.cli;

import java.io.File;

import de.ars.daojones.internal.runtime.tools.ScanBeanModel;
import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutionException;
import de.ars.daojones.runtime.cli.CommandExecutor;
import de.ars.daojones.runtime.cli.CommandLineParameter;

public class ScanBeanModelExecutor implements CommandExecutor {

  private static final CommandLineParameter CLASSES = new CommandLineParameter( "classes",
          "The directory that contains the classes. (the working directory by default)" );
  private static final CommandLineParameter TARGET = new CommandLineParameter(
          "target",
          "The directory where the bean model file is generated. (the working directory by default, can be the same like the directory that contains the classes)" );

  @Override
  public void execute( final CommandContext context ) throws CommandExecutionException {
    try {
      final ScanBeanModel command = new ScanBeanModel();
      final String binaries = ScanBeanModelExecutor.CLASSES.getValue( context.getParameters() );
      if ( null != binaries ) {
        command.setBytecodeDirectory( new File( binaries ) );
      }
      final String target = ScanBeanModelExecutor.TARGET.getValue( context.getParameters() );
      if ( null != target ) {
        command.setTargetDirectory( new File( target ) );
      }
      command.execute();
    } catch ( final Exception e ) {
      throw new CommandExecutionException( this, context, e );
    }
  }

  @Override
  public String getName() {
    return "scan-bean-model";
  }

  @Override
  public String getDescription() {
    return "Scans the classpath for bean model annotations and writes the model into an XML file. This prevents annotation scanning during runtime.";
  }

  @Override
  public CommandLineParameter[] getParameters() {
    return new CommandLineParameter[] { ScanBeanModelExecutor.CLASSES, ScanBeanModelExecutor.TARGET };
  }

}
