package de.ars.daojones.internal.runtime.security;

import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.FactoryModelImpl;
import de.ars.daojones.runtime.spi.security.CallbackHandlerFactory;

/**
 * A static callback handler factory model. It allows to specify a user name and
 * password in the connection configuration directly.<br/>
 * <br/>
 * <b>Example:</b><br/>
 * 
 * <pre>
 *   &lt;credential type="static">
 *     &lt;property name="username" value="john.doe" />
 *     &lt;property name="password" value="password123" />
 *   &lt;/credential>
 * </pre>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class StaticCallbackHandlerFactoryModel extends FactoryModelImpl<CallbackHandlerFactory> implements
        CallbackHandlerFactoryModel {

  private static final long serialVersionUID = 1L;

  /**
   * This id.
   */
  public static final String ID = "static";

  public StaticCallbackHandlerFactoryModel() {
    super( StaticCallbackHandlerFactoryModel.ID, StaticCallbackHandlerFactory.class );
  }

}