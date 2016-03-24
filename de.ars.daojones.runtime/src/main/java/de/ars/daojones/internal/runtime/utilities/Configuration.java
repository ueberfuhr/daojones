package de.ars.daojones.internal.runtime.utilities;

public abstract class Configuration {

  public static final String RESOLVE_FIELDS_TO_NAME_PROPERTY = "daojones.RESOLVE_FIELDS_TO_NAME";
  private static final String RESOLVE_FIELDS_TO_NAME_DEFAULT = "true";

  private Configuration() {
    super();
  }

  public static boolean isResolveFieldsToName() {
    return Boolean.valueOf( System.getProperty( Configuration.RESOLVE_FIELDS_TO_NAME_PROPERTY,
            Configuration.RESOLVE_FIELDS_TO_NAME_DEFAULT ) );
  }

}
