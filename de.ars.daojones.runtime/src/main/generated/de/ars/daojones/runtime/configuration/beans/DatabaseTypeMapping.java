
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import de.ars.daojones.internal.runtime.utilities.StringTrimAdapter;


/**
 * <p>Java class for DatabaseTypeMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatabaseTypeMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" default="table">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="table"/>
 *             &lt;enumeration value="view"/>
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
@XmlType(name = "DatabaseTypeMapping")
public class DatabaseTypeMapping
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    protected String name;
    @XmlAttribute(name = "type")
    protected DatabaseTypeMapping.DataSourceType type;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseTypeMapping.DataSourceType }
     *     
     */
    public DatabaseTypeMapping.DataSourceType getType() {
        if (type == null) {
            return DatabaseTypeMapping.DataSourceType.TABLE;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseTypeMapping.DataSourceType }
     *     
     */
    public void setType(DatabaseTypeMapping.DataSourceType value) {
        this.type = value;
    }


    /**
     * <p>Java class for null.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;simpleType>
     *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *     &lt;enumeration value="table"/>
     *     &lt;enumeration value="view"/>
     *   &lt;/restriction>
     * &lt;/simpleType>
     * </pre>
     * 
     */
    @XmlType(name = "")
    @XmlEnum
    public enum DataSourceType {

        @XmlEnumValue("table")
        TABLE("table"),
        @XmlEnumValue("view")
        VIEW("view");
        private final String value;

        DataSourceType(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static DatabaseTypeMapping.DataSourceType fromValue(String v) {
            for (DatabaseTypeMapping.DataSourceType c: DatabaseTypeMapping.DataSourceType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }

    }

}
