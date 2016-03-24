package de.ars.daojones.internal.runtime.utilities;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringTrimAdapter extends XmlAdapter<String, String> {
  @Override
  public String unmarshal( final String v ) throws Exception {
    if ( v == null ) {
      return null;
    }
    return v.trim();
  }

  @Override
  public String marshal( final String v ) throws Exception {
    if ( v == null ) {
      return null;
    }
    return v.trim();
  }
}