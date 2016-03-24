package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.*;

/**
 * The default implementation for {@link ICredential} with JAXB annotations.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>
 * &lt;complexType name="Credential">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="UserPassword" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Credential extends AbstractConnectionConfigurationElement implements ICredential {

	private static final long serialVersionUID = -298776594925382136L;

	@XmlAttribute(name=CREDENTIAL_ID)
	private String id;

	/**
	 * Creates a new instance.
	 */
	public Credential() {
		super();
	}
	/**
	 * @see de.ars.daojones.connections.model.ICredential#getId()
	 */
	// TODO Java6-Migration
//	@Override
	public String getId() {
		return id;
	}
	/**
	 * Sets the id.
	 * @param id the id
	 * @see de.ars.daojones.connections.model.ICredential#getId()
	 */
	public void setId(String id) {
		this.id = id;
	}

}
