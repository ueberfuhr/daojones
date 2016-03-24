package de.ars.daojones;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="dependencies")
class XmlSerializableInheritations {

	@XmlElement(name="entry")
	private final Collection<InheritationsEntry> entries;

	public XmlSerializableInheritations(Collection<InheritationsEntry> entries) {
		super();
		this.entries = entries;
	}
	
	public XmlSerializableInheritations() {
		this(new HashSet<InheritationsEntry>());
	}
	
	public XmlSerializableInheritations(Map<String, InheritationsEntry> entries) {
		this(entries.values());
	}
	
	public Map<String, InheritationsEntry> getMap() {
		final Map<String, InheritationsEntry> result = new HashMap<String, InheritationsEntry>();
		if(null != entries) for(InheritationsEntry e : entries) result.put(e.getSuperClassName(), e);
		return result;
	}
	
}
