package de.ars.daojones.runtime.query;


/**
 * A kind of {@link Comparison} comparing boolean values.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum BooleanComparison implements Comparison<Boolean> {

  /**
   * The equals operator.
   */
  EQUALS {
    @Override
    public boolean matches( final Boolean left, final Boolean right ) {
      return null == left && null == right || null != left && left.equals( right );
    }
  };

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

}
