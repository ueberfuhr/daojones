
package de.ars.daojones.runtime.configuration.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for beans-config element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="beans-config">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="converter" type="{http://www.ars.de/daojones/2.0/beans}GlobalConverter" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="bean" type="{http://www.ars.de/daojones/2.0/beans}Bean" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "converters",
    "beans"
})
@XmlRootElement(name = "beans-config")
public class BeanConfiguration
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "converter")
    protected List<GlobalConverter> converters;
    @XmlElement(name = "bean")
    protected List<Bean> beans;

    /**
     * Gets the value of the converters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the converters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConverters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalConverter }
     * 
     * 
     */
    public List<GlobalConverter> getConverters() {
        if (converters == null) {
            converters = new ArrayList<GlobalConverter>();
        }
        return this.converters;
    }

    /**
     * Gets the value of the beans property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the beans property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBeans().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bean }
     * 
     * 
     */
    public List<Bean> getBeans() {
        if (beans == null) {
            beans = new ArrayList<Bean>();
        }
        return this.beans;
    }

}
