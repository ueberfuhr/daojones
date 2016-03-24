package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.spi.database.ConnectionFactory;

/**
 * A model describing a connection factory. This is necessary for declaring and
 * using services.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface ConnectionFactoryModel extends FactoryModel<ConnectionFactory> {

}