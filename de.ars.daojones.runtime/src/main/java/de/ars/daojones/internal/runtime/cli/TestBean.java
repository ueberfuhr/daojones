package de.ars.daojones.internal.runtime.cli;

import de.ars.daojones.runtime.beans.annotations.DataSource;

/**
 * Just a dao for testing connections.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
@DataSource( value = "Test" )
public interface TestBean {

}
