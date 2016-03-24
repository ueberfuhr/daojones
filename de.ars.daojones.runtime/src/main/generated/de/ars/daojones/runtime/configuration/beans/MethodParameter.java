
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.ars.daojones.internal.runtime.utilities.StringTrimAdapter;


/**
 * <p>Java class for MethodParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MethodParameter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}InvocableElement">
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MethodParameter")
public class MethodParameter
    extends InvocableElement
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    protected String type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
