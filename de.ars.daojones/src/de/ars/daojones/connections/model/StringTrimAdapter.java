package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An adapter that trims strings.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class StringTrimAdapter extends XmlAdapter<String, String> {

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(String s) throws Exception {
		return null == s ? null : s.trim();
	}

	/**
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public String unmarshal(String s) throws Exception {
		return null == s ? null : s.trim();
	}

}
