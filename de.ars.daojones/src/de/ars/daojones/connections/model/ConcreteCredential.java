package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.*;

/**
 * Default implementation for {@link IConcreteCredential}
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcreteCredential extends Credential implements IConcreteCredential {

	private static final long serialVersionUID = -4841682728906165812L;

	@XmlAttribute(name=CONCRETECREDENTIAL_TYPE)
	private String type;

	/**
	 * Creates a new instance.
	 */
	public ConcreteCredential() {
		super();
	}
	/**
	 * @see de.ars.daojones.connections.model.IConcreteCredential#getType()
	 */
	public String getType() {
		return type;
	}
	/**
	 * Sets the type.
	 * @param type the type
	 * @see de.ars.daojones.connections.model.IConcreteCredential#getType()
	 */
	protected void setType(String type) {
		this.type = type;
	}

}
