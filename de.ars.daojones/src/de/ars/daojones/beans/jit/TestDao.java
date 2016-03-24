package de.ars.daojones.beans.jit;

import de.ars.daojones.FieldAccessException;
import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.runtime.Dao;

/**
 * Just a dao for testing connections.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@DataSource( value = "Test" )
interface TestDao extends Dao {

  /**
   * Returns the MyField.
   * 
   * @return the MyField
   */
  @Column( "MyColumn" )
  @AccessStrategy( StrategyPolicy.IMMEDIATELY )
  public abstract String getMyField();

  /**
   * Sets the MyField.
   * 
   * @param pMyField
   *          the MyField
   * @throws FieldAccessException 
   */
  public abstract void setMyField( String pMyField ) throws FieldAccessException;

}
