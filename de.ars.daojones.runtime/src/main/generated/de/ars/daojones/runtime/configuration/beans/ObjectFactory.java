
package de.ars.daojones.runtime.configuration.beans;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.ars.daojones.runtime.configuration.beans package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.ars.daojones.runtime.configuration.beans
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatabaseTypeMapping }
     * 
     */
    public DatabaseTypeMapping createDatabaseTypeMapping() {
        return new DatabaseTypeMapping();
    }

    /**
     * Create an instance of {@link DatabaseFieldMapping }
     * 
     */
    public DatabaseFieldMapping createDatabaseFieldMapping() {
        return new DatabaseFieldMapping();
    }

    /**
     * Create an instance of {@link BeanConfiguration }
     * 
     */
    public BeanConfiguration createBeanConfiguration() {
        return new BeanConfiguration();
    }

    /**
     * Create an instance of {@link LocalBeanIdentificator }
     * 
     */
    public LocalBeanIdentificator createLocalBeanIdentificator() {
        return new LocalBeanIdentificator();
    }

    /**
     * Create an instance of {@link DatabaseFieldMappingReference }
     * 
     */
    public DatabaseFieldMappingReference createDatabaseFieldMappingReference() {
        return new DatabaseFieldMappingReference();
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link Method }
     * 
     */
    public Method createMethod() {
        return new Method();
    }

    /**
     * Create an instance of {@link MethodParameter }
     * 
     */
    public MethodParameter createMethodParameter() {
        return new MethodParameter();
    }

    /**
     * Create an instance of {@link IdField }
     * 
     */
    public IdField createIdField() {
        return new IdField();
    }

    /**
     * Create an instance of {@link Bean }
     * 
     */
    public Bean createBean() {
        return new Bean();
    }

    /**
     * Create an instance of {@link GlobalConverter }
     * 
     */
    public GlobalConverter createGlobalConverter() {
        return new GlobalConverter();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link LocalConverter }
     * 
     */
    public LocalConverter createLocalConverter() {
        return new LocalConverter();
    }

    /**
     * Create an instance of {@link MethodResult }
     * 
     */
    public MethodResult createMethodResult() {
        return new MethodResult();
    }

    /**
     * Create an instance of {@link Constructor }
     * 
     */
    public Constructor createConstructor() {
        return new Constructor();
    }

}
