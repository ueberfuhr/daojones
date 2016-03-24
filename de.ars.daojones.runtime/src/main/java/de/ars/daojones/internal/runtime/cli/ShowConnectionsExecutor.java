package de.ars.daojones.internal.runtime.cli;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutionException;
import de.ars.daojones.runtime.cli.CommandLineParameter;
import de.ars.daojones.runtime.cli.DaoJonesContextAwareExecutor;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;

public class ShowConnectionsExecutor extends DaoJonesContextAwareExecutor {

  @Override
  public void execute( final CommandContext context ) throws CommandExecutionException {
    super.execute( context );
    final DaoJonesContextConfiguration config = context.getDaoJonesContext().getConfiguration();
    final Map<String, Collection<ConnectionModel>> connectionsByApplication = new TreeMap<String, Collection<ConnectionModel>>();
    // Sort connection models by application id
    for ( final ApplicationModel app : config.getApplicationModelManager().getModels() ) {
      for ( final ConnectionModel cm : config.getConnectionModelManager().getModels() ) {
        final String key = cm.getId().getApplicationId();
        if ( app.getId().equals( key ) ) {
          Collection<ConnectionModel> col = connectionsByApplication.get( key );
          if ( null == col ) {
            col = new TreeSet<ConnectionModel>( new Comparator<ConnectionModel>() {

              @Override
              public int compare( final ConnectionModel o1, final ConnectionModel o2 ) {
                return o1.getId().getConnectionId().compareTo( o2.getId().getConnectionId() );
              }
            } );
            connectionsByApplication.put( key, col );
          }
          col.add( cm );
        }
      }
    }
    for ( final Map.Entry<String, Collection<ConnectionModel>> entry : connectionsByApplication.entrySet() ) {
      final ApplicationModel app = config.getApplicationModelManager().getModel( entry.getKey() );
      context.print( "---" );
      context.print( null != app ? app.getName() : entry.getKey() );
      if ( null != app && null != app.getDescription() ) {
        context.print( " (" );
        context.print( app.getDescription() );
        context.print( ")" );
      }
      context.print( "---" );
      for ( final ConnectionModel con : entry.getValue() ) {
        context.println();
        context.print( "\t" );
        context.print( con.getName() );
        context.print( " (" );
        context.print( con.getId() );
        context.print( ") - " );
        context.print( con.getDescription() );
        context.println();
        context.print( "\t\t\t" );
        context.print( "database: " );
        context.print( con.getConnection().getDatabase() );
        context.println();
        context.print( "\t\t\t" );
        context.print( "type: " );
        context.print( con.getConnection().getType() );
        context.println();
        final ConnectionFactoryModel cf = config.getConnectionFactoryModelManager().getModel(
                con.getConnection().getType() );
        context.print( "\t\t\t" );
        context.print( "type: " );
        context.print( null != cf ? cf.getName() : "(unknown)" );
        context.println();
        context.print( "\t\t\t" );
        context.print( "default: " );
        context.print( con.getConnection().isDefault() );
        context.println();
        context.print( "\t\t\t" );
        context.print( "classes: " );
        context.print( con.getConnection().getAssignedBeanTypes() );
      }
    }
  }

  @Override
  public String getName() {
    return "showConnections";
  }

  @Override
  public String getDescription() {
    return "Displays all configured connections.";
  }

  @Override
  public CommandLineParameter[] getParameters() {
    return new CommandLineParameter[0];
  }

}
