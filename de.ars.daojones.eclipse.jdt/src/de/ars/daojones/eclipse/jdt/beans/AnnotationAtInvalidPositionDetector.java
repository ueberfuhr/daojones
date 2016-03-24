package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.findAncestorWithType;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.hasModifier;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isAssignableFrom;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isGetter;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isSetter;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.eclipse.jdt.markers.InvalidGetterSetterMarker;
import de.ars.daojones.eclipse.jdt.markers.InvalidTypeHierarchyMarker;
import de.ars.daojones.eclipse.jdt.markers.NonAbstractMethodMarker;
import de.ars.daojones.runtime.Dao;

/**
 * A detector detecting annotations at invalid positions. Invalid positions are
 * non-getter/setter methods or non-bean classes.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class AnnotationAtInvalidPositionDetector extends ProblemDetector {

    private boolean _visit(Annotation node) {
        final String name = node.resolveAnnotationBinding().getAnnotationType()
                .getQualifiedName();
        if (Column.class.getName().equals(name)
                || AccessStrategy.class.getName().equals(name)) {
            final MethodDeclaration method = findAncestorWithType(node,
                    MethodDeclaration.class);
            if (null != method) {
                final IMethodBinding methodBinding = method.resolveBinding();
                if (null != methodBinding) {
                    final boolean notAbstract = !hasModifier(methodBinding,
                            Modifier.ABSTRACT);
                    final boolean notAGetterOrSetter = !isGetter(methodBinding)
                            && !isSetter(methodBinding);
                    if (notAbstract || notAGetterOrSetter) {
                        createMarker(notAbstract ? new NonAbstractMethodMarker(
                                method) : new InvalidGetterSetterMarker(method));
                    }
                }
            }
        }
        if (DataSource.class.getName().equals(name)
                || Abstract.class.getName().equals(name)) {
            final TypeDeclaration dec = findAncestorWithType(node,
                    TypeDeclaration.class);
            if (null != dec) {
                final ITypeBinding typeBinding = dec.resolveBinding();
                if (null != typeBinding) {
                    if (!isAssignableFrom(typeBinding, Dao.class.getName())) {
                        createMarker(new InvalidTypeHierarchyMarker(dec));
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
     */
    @Override
    public boolean visit(MarkerAnnotation node) {
        return _visit(node);
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
     */
    @Override
    public boolean visit(NormalAnnotation node) {
        return _visit(node);
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
     */
    @Override
    public boolean visit(SingleMemberAnnotation node) {
        return _visit(node);
    }

    /**
     * @see de.ars.daojones.eclipse.jdt.beans.ProblemDetector#getMarkerIds()
     */
    @Override
    protected String[] getMarkerIds() {
        return new String[] { NonAbstractMethodMarker.ID,
                InvalidGetterSetterMarker.ID, InvalidTypeHierarchyMarker.ID };
    }

}
