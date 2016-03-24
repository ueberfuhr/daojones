package de.ars.daojones.beans.model.ast;

import org.eclipse.jdt.core.dom.ITypeBinding;

import de.ars.daojones.beans.model.ITypeParameter;

/**
 * Default implementation for {@link ITypeParameter}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ASTTypeParameter extends AbstractModelElement<ITypeBinding> implements ITypeParameter {

	/**
	 * Creates an instance.
	 * @param binding the binding
	 */
	public ASTTypeParameter(ITypeBinding binding) {
		super(binding);
	}

	/**
	 * @see ITypeParameter#getName()
	 */
	@Override
	public String getName() {
		return getBinding().getName();
	}

	/**
	 * @see ITypeParameter#getSupertype()
	 */
	@Override
	public String getSupertype() {
		final ITypeBinding binding = getBinding().getSuperclass();
		if(null != binding && !Object.class.getName().equals(binding.getQualifiedName())) return binding.getQualifiedName();
		for(ITypeBinding interfaceBinding : getBinding().getInterfaces()) {
			return interfaceBinding.getQualifiedName();
		}
		return null;
	}

}
