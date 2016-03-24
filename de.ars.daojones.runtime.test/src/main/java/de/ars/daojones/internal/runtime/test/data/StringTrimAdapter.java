package de.ars.daojones.internal.runtime.test.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringTrimAdapter extends XmlAdapter<String, String> {

  @Override
  public String unmarshal( final String v ) {
    if ( v == null ) {
      return null;
    }
    return v.trim();
  }

  @Override
  public String marshal( final String v ) {
    if ( v == null ) {
      return null;
    }
    return v.trim();
  }

}