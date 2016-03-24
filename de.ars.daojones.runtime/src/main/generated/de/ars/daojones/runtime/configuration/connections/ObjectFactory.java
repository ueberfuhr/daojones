
package de.ars.daojones.runtime.configuration.connections;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.ars.daojones.runtime.configuration.connections package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.ars.daojones.runtime.configuration.connections
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConnectionConfiguration }
     * 
     */
    public ConnectionConfiguration createConnectionConfiguration() {
        return new ConnectionConfiguration();
    }

    /**
     * Create an instance of {@link CredentialReference }
     * 
     */
    public CredentialReference createCredentialReference() {
        return new CredentialReference();
    }

    /**
     * Create an instance of {@link ImportDeclaration }
     * 
     */
    public ImportDeclaration createImportDeclaration() {
        return new ImportDeclaration();
    }

    /**
     * Create an instance of {@link Credential }
     * 
     */
    public Credential createCredential() {
        return new Credential();
    }

    /**
     * Create an instance of {@link Cache }
     * 
     */
    public Cache createCache() {
        return new Cache();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Connection }
     * 
     */
    public Connection createConnection() {
        return new Connection();
    }

}
