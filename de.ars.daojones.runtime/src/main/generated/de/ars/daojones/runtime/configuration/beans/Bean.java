
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         A mapping of a Java Bean type to a set of data
 *         within the
 *         database
 *       
 * 
 * <p>Java class for Bean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Bean">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ars.de/daojones/2.0/beans}TypeMappedElement">
 *       &lt;sequence>
 *         &lt;element name="type-mapping" type="{http://www.ars.de/daojones/2.0/beans}DatabaseTypeMapping" minOccurs="0"/>
 *         &lt;element name="identificator" type="{http://www.ars.de/daojones/2.0/beans}LocalBeanIdentificator" minOccurs="0"/>
 *         &lt;element name="id-field" type="{http://www.ars.de/daojones/2.0/beans}IdField" minOccurs="0"/>
 *         &lt;element name="constructor" type="{http://www.ars.de/daojones/2.0/beans}Constructor" minOccurs="0"/>
 *         &lt;element name="field" type="{http://www.ars.de/daojones/2.0/beans}Field" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="method" type="{http://www.ars.de/daojones/2.0/beans}Method" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bean", propOrder = {
    "typeMapping",
    "identificator",
    "idField",
    "constructor",
    "fields",
    "methods"
})
public class Bean
    extends TypeMappedElement
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "type-mapping")
    protected DatabaseTypeMapping typeMapping;
    protected LocalBeanIdentificator identificator;
    @XmlElement(name = "id-field")
    protected IdField idField;
    protected Constructor constructor;
    @XmlElement(name = "field")
    protected List<Field> fields;
    @XmlElement(name = "method")
    protected List<Method> methods;

    /**
     * Gets the value of the typeMapping property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseTypeMapping }
     *     
     */
    public DatabaseTypeMapping getTypeMapping() {
        return typeMapping;
    }

    /**
     * Sets the value of the typeMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseTypeMapping }
     *     
     */
    public void setTypeMapping(DatabaseTypeMapping value) {
        this.typeMapping = value;
    }

    /**
     * Gets the value of the identificator property.
     * 
     * @return
     *     possible object is
     *     {@link LocalBeanIdentificator }
     *     
     */
    public LocalBeanIdentificator getIdentificator() {
        return identificator;
    }

    /**
     * Sets the value of the identificator property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalBeanIdentificator }
     *     
     */
    public void setIdentificator(LocalBeanIdentificator value) {
        this.identificator = value;
    }

    /**
     * Gets the value of the idField property.
     * 
     * @return
     *     possible object is
     *     {@link IdField }
     *     
     */
    public IdField getIdField() {
        return idField;
    }

    /**
     * Sets the value of the idField property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdField }
     *     
     */
    public void setIdField(IdField value) {
        this.idField = value;
    }

    /**
     * Gets the value of the constructor property.
     * 
     * @return
     *     possible object is
     *     {@link Constructor }
     *     
     */
    public Constructor getConstructor() {
        return constructor;
    }

    /**
     * Sets the value of the constructor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Constructor }
     *     
     */
    public void setConstructor(Constructor value) {
        this.constructor = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * 
     * 
     */
    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        return this.fields;
    }

    /**
     * Gets the value of the methods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the methods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMethods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Method }
     * 
     * 
     */
    public List<Method> getMethods() {
        if (methods == null) {
            methods = new ArrayList<Method>();
        }
        return this.methods;
    }

}
