package de.ars.daojones.beans.model.ast;

import static de.ars.daojones.beans.model.ast.Utilities.toExceptionTypes;
import static de.ars.daojones.beans.model.ast.Utilities.toModifiers;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isGetter;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.Modifier;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * Implementation if {@link IPropertyMethod} using {@link IBinding}s.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ASTPropertyMethodImpl extends AbstractModelElement<IMethodBinding>
		implements IPropertyMethod {

	private final IProperty property;

	/**
	 * Creates an instance based on a {@link IMethodBinding}.
	 * 
	 * @param bean
	 *            the declaring bean
	 * @param binding
	 *            the binding
	 * @param property
	 *            the property
	 */
	public ASTPropertyMethodImpl(IBean bean, IMethodBinding binding,
			IProperty property) {
		super(binding);
		this.property = null != property ? property : new ASTPropertyImpl(bean,
				binding);
	}

	/**
	 * @see de.ars.daojones.beans.model.IPropertyMethod#getModifiers()
	 */
	@Override
	public Set<Modifier> getModifiers() {
		final Set<Modifier> result = toModifiers(getBinding().getModifiers());
		result.remove(Modifier.ABSTRACT);
		return result;
	}

	/**
	 * @see de.ars.daojones.beans.model.IPropertyMethod#getProperty()
	 */
	@Override
	public IProperty getProperty() {
		return property;
	}

	/**
	 * @see de.ars.daojones.beans.model.IPropertyMethod#getType()
	 */
	@Override
	public PropertyMethodType getType() {
		return isGetter(getBinding()) ? PropertyMethodType.GETTER
				: PropertyMethodType.SETTER;
	}

	/**
	 * @see de.ars.daojones.beans.model.IExceptionTrigger#getExceptionTypes()
	 */
	@Override
	public List<String> getExceptionTypes() {
		return toExceptionTypes(getBinding().getExceptionTypes());
	}

}
