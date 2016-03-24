
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatabaseFieldMappedElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatabaseFieldMappedElement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}BeanMember">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="field-mapping" type="{http://www.ars.de/daojones/2.0/beans}DatabaseFieldMapping"/>
 *         &lt;element name="field-mapping-ref" type="{http://www.ars.de/daojones/2.0/beans}DatabaseFieldMappingReference"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatabaseFieldMappedElement", propOrder = {
    "fieldMapping",
    "fieldMappingRef"
})
@XmlSeeAlso({
    Field.class
})
public abstract class DatabaseFieldMappedElement
    extends BeanMember
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "field-mapping")
    protected DatabaseFieldMapping fieldMapping;
    @XmlElement(name = "field-mapping-ref")
    protected DatabaseFieldMappingReference fieldMappingRef;

    /**
     * Gets the value of the fieldMapping property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseFieldMapping }
     *     
     */
    public DatabaseFieldMapping getFieldMapping() {
        return fieldMapping;
    }

    /**
     * Sets the value of the fieldMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseFieldMapping }
     *     
     */
    public void setFieldMapping(DatabaseFieldMapping value) {
        this.fieldMapping = value;
    }

    /**
     * Gets the value of the fieldMappingRef property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseFieldMappingReference }
     *     
     */
    public DatabaseFieldMappingReference getFieldMappingRef() {
        return fieldMappingRef;
    }

    /**
     * Sets the value of the fieldMappingRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseFieldMappingReference }
     *     
     */
    public void setFieldMappingRef(DatabaseFieldMappingReference value) {
        this.fieldMappingRef = value;
    }

}
