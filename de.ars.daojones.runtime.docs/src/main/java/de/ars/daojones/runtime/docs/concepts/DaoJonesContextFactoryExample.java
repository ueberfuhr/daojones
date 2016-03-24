package de.ars.daojones.runtime.docs.concepts;

import java.io.IOException;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

public class DaoJonesContextFactoryExample {

  public void createContext() throws ConfigurationException, IOException {
    // -1-
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    try {
      final Application app = ctx.getApplication("mail");
      try {
        final Connection<Memo> con = app.getConnection(Memo.class);
        try {
          // Access database
        } finally {
          con.close();
        }
      } finally {
        app.close();
      }
    } finally {
      ctx.close();
    }
  }

  /*
  public void createContextJava7() throws ConfigurationException, DataAccessException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    try (final DaoJonesContext ctx = factory.createContext()) {
      try (final ApplicationContext app = ctx.getApplication( "mail" )) {
        try(final Connection<Memo> con = app.getConnection( Memo.class )) {
          // Access database
        }
      }
    }
  }
  */
}
