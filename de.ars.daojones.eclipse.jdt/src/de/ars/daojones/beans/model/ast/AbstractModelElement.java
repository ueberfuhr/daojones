package de.ars.daojones.beans.model.ast;

import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * An abstract class containing a binding.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> the type of the binding
 */
abstract class AbstractModelElement<T> {

	private final T binding;

	/**
	 * Creates an instance based on a {@link ITypeBinding}.
	 * @param binding the binding
	 */
	protected AbstractModelElement(T binding) {
		super();
		this.binding = binding;
	}
	
	/**
	 * Returns the binding.
	 * @return the binding
	 */
	protected T getBinding() {
		return binding;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + binding;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((binding == null) ? 0 : binding.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractModelElement other = (AbstractModelElement) obj;
		if (binding == null) {
			if (other.binding != null)
				return false;
		} else if (!binding.equals(other.binding))
			return false;
		return true;
	}


}
