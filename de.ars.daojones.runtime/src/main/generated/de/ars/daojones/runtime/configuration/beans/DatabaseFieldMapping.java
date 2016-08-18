
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.ars.daojones.internal.runtime.utilities.StringTrimAdapter;


/**
 * 
 *         The mapping to a field within the database.
 *       
 * 
 * <p>Java class for DatabaseFieldMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatabaseFieldMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="converter" type="{http://www.ars.de/daojones/2.0/beans}LocalConverter" minOccurs="0"/>
 *         &lt;sequence>
 *           &lt;element name="meta" type="{http://www.ars.de/daojones/2.0/beans}Property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="update-policy" default="replace">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="replace"/>
 *             &lt;enumeration value="insert"/>
 *             &lt;enumeration value="append"/>
 *             &lt;enumeration value="never"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatabaseFieldMapping", propOrder = {
    "converter",
    "metadata"
})
public class DatabaseFieldMapping
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected LocalConverter converter;
    @XmlElement(name = "meta")
    protected List<Property> metadata;
    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    protected String name;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "update-policy")
    protected DatabaseFieldMapping.UpdatePolicy updatePolicy;

    /**
     * Gets the value of the converter property.
     * 
     * @return
     *     possible object is
     *     {@link LocalConverter }
     *     
     */
    public LocalConverter getConverter() {
        return converter;
    }

    /**
     * Sets the value of the converter property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalConverter }
     *     
     */
    public void setConverter(LocalConverter value) {
        this.converter = value;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     * 
     * 
     */
    public List<Property> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<Property>();
        }
        return this.metadata;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the updatePolicy property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseFieldMapping.UpdatePolicy }
     *     
     */
    public DatabaseFieldMapping.UpdatePolicy getUpdatePolicy() {
        if (updatePolicy == null) {
            return DatabaseFieldMapping.UpdatePolicy.REPLACE;
        } else {
            return updatePolicy;
        }
    }

    /**
     * Sets the value of the updatePolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseFieldMapping.UpdatePolicy }
     *     
     */
    public void setUpdatePolicy(DatabaseFieldMapping.UpdatePolicy value) {
        this.updatePolicy = value;
    }


    /**
     * <p>Java class for null.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType>
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="replace"/>
     *     &lt;enumeration value="insert"/>
     *     &lt;enumeration value="append"/>
     *     &lt;enumeration value="never"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "")
    @XmlEnum
    public enum UpdatePolicy {

        @XmlEnumValue("replace")
        REPLACE("replace"),
        @XmlEnumValue("insert")
        INSERT("insert"),
        @XmlEnumValue("append")
        APPEND("append"),
        @XmlEnumValue("never")
        NEVER("never");
        private final String value;

        UpdatePolicy(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static DatabaseFieldMapping.UpdatePolicy fromValue(String v) {
            for (DatabaseFieldMapping.UpdatePolicy c: DatabaseFieldMapping.UpdatePolicy.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }

}
