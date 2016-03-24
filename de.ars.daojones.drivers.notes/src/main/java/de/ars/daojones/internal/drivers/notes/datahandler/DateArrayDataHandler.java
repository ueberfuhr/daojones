package de.ars.daojones.internal.drivers.notes.datahandler;

import java.util.Date;

public class DateArrayDataHandler extends AbstractArrayDataHandler<Date> {

  @Override
  public Class<? extends Date[]> getKeyType() {
    return Date[].class;
  }

}
