package de.ars.daojones.beans.model.ast;

import static de.ars.daojones.beans.model.ast.Utilities.toExceptionTypes;
import static de.ars.daojones.beans.model.ast.Utilities.toModifiers;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.Modifier;

/**
 * AST implementation for {@link IConstructor}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ASTConstructorImpl extends AbstractModelElement<IMethodBinding>
    implements IConstructor {

  private final IBean bean;

  /**
   * Creates an instance.
   * 
   * @param bean
   *          the bean declaring this constructor
   * @param binding
   *          the method
   */
  public ASTConstructorImpl( IBean bean, IMethodBinding binding ) {
    super( binding );
    this.bean = bean;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getBean()
   */
  @Override
  public IBean getBean() {
    return bean;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getParameterTypes()
   */
  @Override
  public String[] getParameterTypes() {
    return Utilities.toTypeNames( getBinding().getParameterTypes() );
  }

  /**
   * @see de.ars.daojones.beans.model.IModifierContainer#getModifiers()
   */
  @Override
  public Set<Modifier> getModifiers() {
    return toModifiers( getBinding().getModifiers() );
  }

  /**
   * @see de.ars.daojones.beans.model.IExceptionTrigger#getExceptionTypes()
   */
  @Override
  public List<String> getExceptionTypes() {
    return toExceptionTypes( getBinding().getExceptionTypes() );
  }

}
