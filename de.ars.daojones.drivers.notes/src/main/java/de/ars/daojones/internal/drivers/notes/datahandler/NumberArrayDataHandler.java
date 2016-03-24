package de.ars.daojones.internal.drivers.notes.datahandler;

public class NumberArrayDataHandler extends AbstractArrayDataHandler<Number> {

  @Override
  public Class<? extends Number[]> getKeyType() {
    return Number[].class;
  }

}
