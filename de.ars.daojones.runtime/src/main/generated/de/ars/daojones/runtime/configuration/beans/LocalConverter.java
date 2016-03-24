
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalConverter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocalConverter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}ConverterProvidingElement">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalConverter")
public class LocalConverter
    extends ConverterProvidingElement
    implements Serializable
{

    private final static long serialVersionUID = 1L;

}
