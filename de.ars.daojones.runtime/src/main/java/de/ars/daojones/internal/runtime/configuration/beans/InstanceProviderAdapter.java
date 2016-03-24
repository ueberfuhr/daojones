package de.ars.daojones.internal.runtime.configuration.beans;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.ars.daojones.runtime.configuration.beans.InstanceProvidingElement;

public class InstanceProviderAdapter extends XmlAdapter<String, InstanceProvidingElement.InstanceProvider<?>> {
  @Override
  public InstanceProvidingElement.InstanceProvider<?> unmarshal( final String className ) throws Exception {
    return null != className ? new InstanceProvidingElement.InstanceByClassNameProvider( className ) : null;
  }

  @Override
  public String marshal( final InstanceProvidingElement.InstanceProvider<?> provider ) throws Exception {
    return null != provider ? provider.getInstanceClassName() : null;
  }
}