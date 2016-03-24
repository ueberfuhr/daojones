package de.ars.daojones.internal.drivers.notes;

public class AwareNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public AwareNotFoundException() {
    super();
  }

  public AwareNotFoundException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public AwareNotFoundException( final String message ) {
    super( message );
  }

  public AwareNotFoundException( final Throwable cause ) {
    super( cause );
  }

}
