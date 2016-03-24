package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.ars.daojones.connections.model package. 
 * <p>
 * An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * </p>
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create 
     * new instances of schema derived classes for package: 
     * de.ars.daojones.connections.model
     */
    public ObjectFactory() {
    	super();
    }

    /**
     * Create an instance of {@link Credential }
     * @return the object
     */
    public Credential createCredential() {
        return new Credential();
    }

    /**
     * Create an instance of {@link Interval }
     * @return the object
     */
    public Interval createInterval() {
        return new Interval();
    }

    /**
     * Create an instance of {@link ImportDeclaration}.
     * @return the object
     */
    public ImportDeclaration createConnectionsImport() {
        return new ImportDeclaration();
    }

    /**
     * Create an instance of {@link Connection}.
     * @return the object
     */
    public Connection createConnectionsConnection() {
        return new Connection();
    }

    /**
     * Create an instance of {@link ConnectionConfiguration}.
     * @return the object
     */
    public ConnectionConfiguration createConnectionConfiguration() {
        return new ConnectionConfiguration();
    }

}
