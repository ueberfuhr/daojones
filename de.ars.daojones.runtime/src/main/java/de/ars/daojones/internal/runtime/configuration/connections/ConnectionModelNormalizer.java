package de.ars.daojones.internal.runtime.configuration.connections;

import de.ars.daojones.internal.runtime.configuration.ModelNormalizer;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;

/**
 * A connection model normalizer reads the bean model and commonly replaces null
 * values by default values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface ConnectionModelNormalizer extends
        ModelNormalizer<ConnectionModel.Id, ConnectionModel, ConnectionModelManager> {

}
