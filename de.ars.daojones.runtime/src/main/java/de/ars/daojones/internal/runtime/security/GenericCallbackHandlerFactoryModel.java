package de.ars.daojones.internal.runtime.security;

import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.FactoryModelImpl;
import de.ars.daojones.runtime.spi.security.CallbackHandlerFactory;

/**
 * A generic callback handler factory model. It allows to specify the callback
 * handler implementation class and further properties.<br/>
 * <br/>
 * <b>Example:</b><br/>
 * 
 * <pre>
 *   &lt;credential type="generic">
 *     &lt;property name="class" value="com.sun.security.auth.callback.TextCallbackHandler" />
 *     &lt;!-- specify further properties to get values injected into callback handler -->
 *   &lt;/credential>
 * </pre>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class GenericCallbackHandlerFactoryModel extends FactoryModelImpl<CallbackHandlerFactory> implements
        CallbackHandlerFactoryModel {

  private static final long serialVersionUID = 1L;

  /**
   * This id.
   */
  public static final String ID = "generic";

  public GenericCallbackHandlerFactoryModel() {
    super( GenericCallbackHandlerFactoryModel.ID, GenericCallbackHandlerFactory.class );
  }

}