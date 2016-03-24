package de.ars.daojones.runtime.docs.events;

import de.ars.daojones.runtime.connections.events.ConnectionEvent;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.context.DaoJonesContext;

public class ConnectionEventListenerExample {

  public void registerEventListener(final DaoJonesContext ctx) {
    ctx.addConnectionEventListener(new ConnectionEventListener() {

      @Override
      public void handle(final ConnectionEvent event) {
        switch (event.getType()) {
        case CONNECTION_CREATED:
          break;
        case CONNECTION_CLOSED:
          break;
        case CONNECTION_MODEL_REGISTERED:
          break;
        case CONNECTION_MODEL_DEREGISTERED:
          break;
        }

      }
    });
  }
}
