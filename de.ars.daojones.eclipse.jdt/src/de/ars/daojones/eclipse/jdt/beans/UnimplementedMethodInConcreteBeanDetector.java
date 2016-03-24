package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotation;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.eclipse.jdt.markers.UnimplementedMethodInConcreteBeanMarker;

/**
 * A detector for finding abstract methods within a non-abstract bean class or
 * interface. This detector also searches in subclasses and subinterfaces.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class UnimplementedMethodInConcreteBeanDetector extends
        AbstractUnimplementedMethodsDetector {

    /*
     * Only validate if the bean is concrete.
     */
    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        final ITypeBinding typeBinding = node.resolveBinding();
        if (null != typeBinding
                && null == getAnnotation(typeBinding, Abstract.class)) {
            return super.visit(node);
        } else {
            return false;
        }
    }

    /**
     * @see de.ars.daojones.eclipse.jdt.beans.AbstractUnimplementedMethodsDetector#process(org.eclipse.jdt.core.dom.TypeDeclaration)
     */
    @Override
    protected void process(TypeDeclaration node) {
        final Set<IMethodBinding> externalMethods = new TreeSet<IMethodBinding>(
                new Comparator<IMethodBinding>() {
                    @Override
                    public int compare(IMethodBinding o1, IMethodBinding o2) {
                        final int classComp = o1.getDeclaringClass()
                                .getQualifiedName().compareTo(
                                        o2.getDeclaringClass()
                                                .getQualifiedName());
                        if (0 != classComp)
                            return classComp;
                        final int nameComp = o1.getName().compareTo(
                                o2.getName());
                        if (0 != nameComp)
                            return nameComp;
                        return o1.equals(o2) ? 0 : 1;
                    }
                });
        for (Map.Entry<MethodInformation, IMethodBinding> entry : getUnimplementedMethods()
                .entrySet()) {
            final IMethodBinding method = entry.getValue();
            final MethodDeclaration methodNode = (MethodDeclaration) getCompilationUnit()
                    .findDeclaringNode(method);
            if (null != methodNode) {
                // method is declared in compilation unit
                createMarker(new UnimplementedMethodInConcreteBeanMarker(
                        methodNode));
            } else {
                // method is inherited
                externalMethods.add(method);
            }
        }
        if (!externalMethods.isEmpty())
            createMarker(new UnimplementedMethodInConcreteBeanMarker(node,
                    externalMethods.toArray(new IMethodBinding[externalMethods
                            .size()])));
    }

    /**
     * @see de.ars.daojones.eclipse.jdt.beans.ProblemDetector#getMarkerIds()
     */
    @Override
    protected String[] getMarkerIds() {
        return new String[] { UnimplementedMethodInConcreteBeanMarker.ID };
    }

}
