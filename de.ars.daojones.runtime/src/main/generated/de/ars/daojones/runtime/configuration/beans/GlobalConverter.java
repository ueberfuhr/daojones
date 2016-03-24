
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.ars.daojones.internal.runtime.utilities.StringTrimAdapter;


/**
 * <p>Java class for GlobalConverter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GlobalConverter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}ConverterProvidingElement">
 *       &lt;attribute name="convertType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalConverter")
public class GlobalConverter
    extends ConverterProvidingElement
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "convertType", required = true)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    protected String convertType;

    /**
     * Gets the value of the convertType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConvertType() {
        return convertType;
    }

    /**
     * Sets the value of the convertType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConvertType(String value) {
        this.convertType = value;
    }

}
