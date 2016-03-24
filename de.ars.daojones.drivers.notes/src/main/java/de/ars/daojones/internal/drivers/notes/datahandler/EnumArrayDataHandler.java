package de.ars.daojones.internal.drivers.notes.datahandler;

public class EnumArrayDataHandler extends AbstractArrayDataHandler<Enum<?>> {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<? extends Enum<?>[]> getKeyType() {
    return ( Class ) Enum[].class;
  }

}
