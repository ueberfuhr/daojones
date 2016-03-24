package de.ars.daojones.runtime.docs.concepts.connectionmodel;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel.Id;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;
import de.ars.daojones.runtime.docs.concepts.Memo;

@SuppressWarnings("unused")
public class CodeSnippets {

  public void connectionModelAccess() throws ConfigurationException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    // -1-
    final ConnectionModelManager cmm = ctx.getConfiguration().getConnectionModelManager();
    cmm.getModels();
  }

  public void connectionModelAccessSingle() throws ConfigurationException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    final ConnectionModelManager cmm = ctx.getConfiguration().getConnectionModelManager();
    // -1-
    final String app = "mail";
    final String connectionId = "memo";
    final Id id = new Id(app, connectionId);
    final ConnectionModel model = cmm.getModel(id);
  }
}
