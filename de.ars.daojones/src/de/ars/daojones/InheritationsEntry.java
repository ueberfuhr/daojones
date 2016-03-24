package de.ars.daojones;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="entry")
class InheritationsEntry {

	@XmlElement(name="super")
	private String superClassName;
	@XmlElement(name="sub")
	private HashSet<String> subClasses;

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public Collection<String> getSubClasses() {
		synchronized(this) {
			if(null == subClasses) subClasses = new HashSet<String>();
		}
		return subClasses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((superClassName == null) ? 0 : superClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InheritationsEntry other = (InheritationsEntry) obj;
		if (superClassName == null) {
			if (other.superClassName != null)
				return false;
		} else if (!superClassName.equals(other.superClassName))
			return false;
		return true;
	}

//	public void setSubClasses(Collection<String> subClasses) {
//		this.subClasses = new HashSet<String>();
//		this.subClasses.addAll(subClasses);
//	}
	
	
	
}
