package de.ars.daojones.internal.drivers.notes.datahandler;

public class StringArrayDataHandler extends AbstractArrayDataHandler<String> {

  @Override
  public Class<? extends String[]> getKeyType() {
    return String[].class;
  }

}
