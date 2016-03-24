package de.ars.daojones.runtime.query;


/**
 * A kind of {@link Comparison} comparing numbers.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum NumberComparison implements Comparison<Number> {

  /**
   * The equals operator.
   */
  EQUALS {

    @Override
    public boolean matches( final Number left, final Number right ) {
      return null == left && null == right || null != left && null != right
              && Double.valueOf( left.doubleValue() ).equals( Double.valueOf( right.doubleValue() ) );
    }

  },
  /**
   * The lower than operator.
   */
  LOWERTHAN {

    @Override
    public boolean matches( final Number left, final Number right ) {
      return null != left && null != right && left.doubleValue() < right.doubleValue();
    }

  },
  /**
   * The greater than operator.
   */
  GREATERTHAN {

    @Override
    public boolean matches( final Number left, final Number right ) {
      return null != left && null != right && left.doubleValue() > right.doubleValue();
    }

  };

  @Override
  public Class<Number> getType() {
    return Number.class;
  }

}
