package de.ars.daojones.runtime.configuration.context;

/**
 * Default implementation of an application model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class ApplicationModelImpl implements ApplicationModel {

  private static final long serialVersionUID = 1L;

  private final String id;
  private String name;
  private String description;

  /**
   * Constructor
   * 
   * @param id
   *          the application id
   */
  public ApplicationModelImpl( final String id ) {
    super();
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return null != name ? name : getId();
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name
   */
  public void setName( final String name ) {
    this.name = name;
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

}
