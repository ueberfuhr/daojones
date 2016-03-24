package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.GlobalConverter;

/**
 * Default implementation of a global converter model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class GlobalConverterModelImpl implements GlobalConverterModel {

  private static final long serialVersionUID = 1L;

  private final Id id;
  private final GlobalConverter converter;
  private String description;

  /**
   * Constructor.
   * 
   * @param application
   *          the application
   * @param converter
   *          the converter
   */
  public GlobalConverterModelImpl( final String application, final GlobalConverter converter ) {
    super();
    this.converter = converter;
    id = new Id( application, converter.getConvertType() );
  }

  @Override
  public Id getId() {
    return id;
  }

  @Override
  public String getName() {
    return getId().getApplicationId() + ":" + getId().getTypeId();
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the description
   */
  public void setDescription( final String description ) {
    this.description = description;
  }

  @Override
  public GlobalConverter getConverter() {
    return converter;
  }

}