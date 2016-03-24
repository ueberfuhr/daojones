package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.*;

/**
 * Default implementation for {@link IInterval} using JAXB annotations.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>
 * &lt;complexType name="Interval">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="minimum" type="{http://www.ars.de/daojones/connections/}Natural" default="1" />
 *       &lt;attribute name="maximum" type="{http://www.ars.de/daojones/connections/}Natural" default="10" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Interval implements IChangeableInterval {

	private static final long serialVersionUID = -1332026770407566323L;

	@XmlAttribute(name=INTERVAL_MINIMUM)
	private int minimum;
	@XmlAttribute(name=INTERVAL_MAXIMUM)
	private int maximum;
	
	/**
	 * @return the minimum
	 * @see de.ars.daojones.connections.model.IInterval#getMinimum()
	 */
	public int getMinimum() {
		return minimum;
	}
	/**
	 * Sets the minimum.
	 * @param minimum the minimum
	 * @see de.ars.daojones.connections.model.IInterval#getMinimum()
	 */
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	/**
	 * @return the maximum
	 * @see de.ars.daojones.connections.model.IInterval#getMaximum()
	 */
	public int getMaximum() {
		return maximum;
	}
	/**
	 * Sets the maximum.
	 * @param maximum the maximum
	 * @see de.ars.daojones.connections.model.IInterval#getMaximum()
	 */
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

}
