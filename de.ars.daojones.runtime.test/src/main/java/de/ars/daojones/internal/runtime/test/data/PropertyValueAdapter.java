package de.ars.daojones.internal.runtime.test.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertyValueAdapter extends XmlAdapter<String, Object> {

  private final StringTrimAdapter delegate = new StringTrimAdapter();

  @Override
  public Object unmarshal( final String v ) {
    return delegate.unmarshal( v );
  }

  @Override
  public String marshal( final Object v ) {
    return delegate.marshal( null == v ? null : String.valueOf( v ) );
  }

}