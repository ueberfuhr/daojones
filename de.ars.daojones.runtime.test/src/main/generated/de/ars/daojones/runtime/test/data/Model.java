
package de.ars.daojones.runtime.test.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Model complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Model">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datasource" type="{http://www.ars.de/daojones/2.0/testmodel}DataSource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Model", propOrder = {
    "dataSources"
})
public class Model
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "datasource")
    protected List<DataSource> dataSources;

    /**
     * Gets the value of the dataSources property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataSources property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataSources().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataSource }
     * 
     * 
     */
    public List<DataSource> getDataSources() {
        if (dataSources == null) {
            dataSources = new ArrayList<DataSource>();
        }
        return this.dataSources;
    }

}
