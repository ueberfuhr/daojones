
package de.ars.daojones.runtime.configuration.connections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configuration element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="configuration">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="import" type="{http://www.ars.de/daojones/2.0/connections}ImportDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="credential" type="{http://www.ars.de/daojones/2.0/connections}Credential" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="connection" type="{http://www.ars.de/daojones/2.0/connections}Connection" maxOccurs="unbounded" minOccurs="0"/>
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
    "imports",
    "credentials",
    "connections"
})
@XmlRootElement(name = "configuration")
public class ConnectionConfiguration
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "import")
    protected List<ImportDeclaration> imports;
    @XmlElement(name = "credential")
    protected List<Credential> credentials;
    @XmlElement(name = "connection")
    protected List<Connection> connections;

    /**
     * Gets the value of the imports property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imports property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImports().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportDeclaration }
     * 
     * 
     */
    public List<ImportDeclaration> getImports() {
        if (imports == null) {
            imports = new ArrayList<ImportDeclaration>();
        }
        return this.imports;
    }

    /**
     * Gets the value of the credentials property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credentials property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredentials().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Credential }
     * 
     * 
     */
    public List<Credential> getCredentials() {
        if (credentials == null) {
            credentials = new ArrayList<Credential>();
        }
        return this.credentials;
    }

    /**
     * Gets the value of the connections property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connections property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnections().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Connection }
     * 
     * 
     */
    public List<Connection> getConnections() {
        if (connections == null) {
            connections = new ArrayList<Connection>();
        }
        return this.connections;
    }

}
