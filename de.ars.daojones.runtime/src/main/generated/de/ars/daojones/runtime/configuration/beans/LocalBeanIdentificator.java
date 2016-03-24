
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalBeanIdentificator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocalBeanIdentificator">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}BeanIdentificatorProvidingElement">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalBeanIdentificator")
public class LocalBeanIdentificator
    extends BeanIdentificatorProvidingElement
    implements Serializable
{

    private final static long serialVersionUID = 1L;

}
