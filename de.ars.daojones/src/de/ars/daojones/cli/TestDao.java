package de.ars.daojones.cli;

import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.runtime.Dao;

/**
 * Just a dao for testing connections.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@DataSource( value = "Test" )
public interface TestDao extends Dao {

}
