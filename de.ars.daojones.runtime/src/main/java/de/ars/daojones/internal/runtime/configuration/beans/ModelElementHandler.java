package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * This class has the responsibility to encapsulate model element type handling
 * at a central position. If model elements are added later, they need to be
 * added here to get the compiler errors where this is used.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class ModelElementHandler {

  public static class ModelElementHandlingException extends Exception {

    private static final long serialVersionUID = 1L;

    public ModelElementHandlingException( final Throwable cause ) {
      super( cause );
    }

  }

  private static final Messages logger = Messages.create( "configuration.beans.ModelElementHandler" );

  /**
   * Invoke this method to handle {@link DatabaseFieldMappedElement} instances.
   * 
   * @param model
   *          the {@link DatabaseFieldMappedElement}
   * @throws ConfigurationException
   * @throws {@link ModelElementHandlingException} if the handling occured an
   *         error that is not caused by the configuration
   * @throws IllegalArgumentException
   *           if the model is <tt>null</tt> or has a type that is not
   *           implemented yet
   */
  public void handle( final DatabaseFieldMappedElement model ) throws ConfigurationException,
          ModelElementHandlingException {
    if ( null == model ) {
      throw new IllegalArgumentException();
    } else if ( model instanceof Field ) {
      handle( ( Field ) model );
    } else if ( model instanceof MethodResult ) {
      handle( ( MethodResult ) model );
    } else if ( model instanceof MethodParameter ) {
      handle( ( MethodParameter ) model );
    } else {
      throw new IllegalArgumentException( ModelElementHandler.logger.get( "error.typeNotImplemented", model.getClass()
              .getName() ) );
    }
  }

  protected abstract void handle( final Field model ) throws ConfigurationException, ModelElementHandlingException;

  protected abstract void handle( final MethodResult model ) throws ConfigurationException,
          ModelElementHandlingException;

  protected abstract void handle( final MethodParameter model ) throws ConfigurationException,
          ModelElementHandlingException;

}
