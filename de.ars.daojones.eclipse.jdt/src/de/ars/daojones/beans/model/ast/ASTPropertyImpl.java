package de.ars.daojones.beans.model.ast;

import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotation;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotationValue;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getEnumerationValue;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getPropertyName;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getPropertyType;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isGetter;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isSetter;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.ColumnSelector;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.Transform;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;

/**
 * Implementation if {@link IProperty} using {@link IBinding}s.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ASTPropertyImpl extends AbstractModelElement<IMethodBinding>
        implements IProperty {

    private final IBean bean;

    /**
     * Creates an instance based on a {@link IMethodBinding}.
     * 
     * @param bean
     *            the declaring bean.
     * @param binding
     *            the binding
     */
    public ASTPropertyImpl(IBean bean, IMethodBinding binding) {
        super(binding);
        this.bean = bean;
    }

    /**
     * Returns the declaring bean.
     * 
     * @return the declaring bean
     * @see IProperty#getBean()
     */
    @Override
    public IBean getBean() {
        return bean;
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getColumn()
     */
    @SuppressWarnings("unchecked")
    @Override
    public ColumnInfo getColumn() {
        final IAnnotationBinding annotation = getAnnotation(getBinding(),
                Column.class);
        return new ColumnInfo( ( String ) getAnnotationValue( annotation ),
        ( Class<? extends ColumnSelector> ) getAnnotationValue( annotation,
            "selector" ) );
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getStrategy()
     */
    @Override
    public StrategyPolicy getStrategy() {
        final IAnnotationBinding annotation = getAnnotation(getBinding(),
                AccessStrategy.class);
        final Object value = getAnnotationValue(annotation);
        return getEnumerationValue(StrategyPolicy.class, value);
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getTransformer()
     */
    @Override
    public String getTransformer() {
        final IAnnotationBinding annotation = getAnnotation(getBinding(),
                Transform.class);
        final ITypeBinding value = (ITypeBinding) getAnnotationValue(annotation);
        return null != value ? value.getQualifiedName() : null;
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getName()
     */
    @Override
    public String getName() {
        return getPropertyName(getBinding());
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getType()
     */
    @Override
    public String getType() {
        return getPropertyType(getBinding()).getQualifiedName();
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getGetter()
     */
    @Override
    public IPropertyMethod getGetter() {
        return isGetter(getBinding()) ? new ASTPropertyMethodImpl(bean,
                getBinding(), this) : null;
    }

    /**
     * @see de.ars.daojones.beans.model.IProperty#getSetter()
     */
    @Override
    public IPropertyMethod getSetter() {
        return isSetter(getBinding()) ? new ASTPropertyMethodImpl(bean,
                getBinding(), this) : null;
    }

}
