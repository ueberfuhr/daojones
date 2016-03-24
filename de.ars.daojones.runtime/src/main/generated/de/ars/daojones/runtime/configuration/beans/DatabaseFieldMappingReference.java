
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatabaseFieldMappingReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatabaseFieldMappingReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="mapping" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatabaseFieldMappingReference")
public class DatabaseFieldMappingReference
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "mapping", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object mapping;

    /**
     * Gets the value of the mapping property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getMapping() {
        return mapping;
    }

    /**
     * Sets the value of the mapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setMapping(Object value) {
        this.mapping = value;
    }

}
