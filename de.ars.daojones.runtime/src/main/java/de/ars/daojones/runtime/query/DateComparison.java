package de.ars.daojones.runtime.query;

import java.util.Date;

/**
 * A type of {@link Comparison} comparing date values.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum DateComparison implements Comparison<Date> {

  /**
   * The after operator only comparing the date, not the time.
   */
  DATE_AFTER( false ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return TIME_AFTER.matches( DateComparison.noTime( left ), DateComparison.noTime( right ) );
    }

  },
  /**
   * The equals operator only comparing the date, not the time.
   */
  DATE_EQUALS( false ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return TIME_EQUALS.matches( DateComparison.noTime( left ), DateComparison.noTime( right ) );
    }

  },
  /**
   * The before operator only comparing the date, not the time.
   */
  DATE_BEFORE( false ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return TIME_BEFORE.matches( DateComparison.noTime( left ), DateComparison.noTime( right ) );
    }

  },
  /**
   * The after operator comparing both date and time.
   */
  TIME_AFTER( true ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return null != left && null != right && left.after( right );
    }

  },
  /**
   * The equals operator comparing both date and time.
   */
  TIME_EQUALS( true ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return null != left && null != right && left.equals( right );
    }

  },
  /**
   * The before operator comparing both date and time.
   */
  TIME_BEFORE( true ) {

    @Override
    public boolean matches( final Date left, final Date right ) {
      return null != left && null != right && left.before( right );
    }

  };

  private final boolean includingTime;

  private DateComparison( final boolean includingTime ) {
    this.includingTime = includingTime;
  }

  /**
   * Returns a flag indicating whether the time matters or not.
   * 
   * @return <code>true</code>, if the time matters
   */
  public boolean isIncludingTime() {
    return includingTime;
  }

  private static Date noTime( final Date date ) {
    if ( null == date ) {
      return null;
    }
    final long fullDate = date.getTime();
    final long timeOfDay = date.getTime() % ( 1000 * 60 * 60 * 24 );
    return new Date( fullDate - timeOfDay );
  }

  @Override
  public Class<Date> getType() {
    return Date.class;
  }

}
