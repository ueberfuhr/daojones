package de.ars.daojones.internal.drivers.notes.datahandler;

public class BooleanArrayDataHandler extends AbstractArrayDataHandler<Boolean> {

  @Override
  public Class<? extends Boolean[]> getKeyType() {
    return Boolean[].class;
  }

}
