package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotation;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotationValue;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getEnumerationValue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.annotations.Transform;
import de.ars.daojones.eclipse.jdt.markers.DoubledAnnotatedMethodsMarker;

/**
 * Detects DaoJones getter and setter within a class that are both annotated. If
 * they are annotated equally, a warning is created, otherwise an error is
 * created.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DoubledAnnotatedMethodsDetector extends
        AbstractUnimplementedMethodsDetector {

    /**
     * @see ProblemDetector#getMarkerIds()
     */
    @Override
    protected String[] getMarkerIds() {
        return new String[] { DoubledAnnotatedMethodsMarker.ID };
    }

    /**
     * @see AbstractUnimplementedMethodsDetector#process(TypeDeclaration)
     */
    @Override
    protected void process(TypeDeclaration node) {
        // compare valid getters with (in)valid setters
        // compare valid setters with (in)valid getters
        final Map<String, MethodInformation> getters = new HashMap<String, MethodInformation>();
        getters.putAll(getValidGettersByProperty());
        getters.putAll(getInvalidGettersByProperty());
        final Map<String, MethodInformation> setters = new HashMap<String, MethodInformation>();
        setters.putAll(getValidSettersByProperty());
        setters.putAll(getInvalidSettersByProperty());
        getters.keySet().retainAll(setters.keySet());
        for (Map.Entry<String, MethodInformation> entry : getters.entrySet()) {
            // retrieve method bindings
            final MethodInformation getterInfo = entry.getValue();
            final MethodInformation setterInfo = setters.get(entry.getKey());
            final IMethodBinding getter = getMethodsByInfo().get(getterInfo);
            final IMethodBinding setter = getMethodsByInfo().get(setterInfo);
            // check annotations, if different column values or access
            // strategies -> error, else warning
            final IAnnotationBinding getterColumn = getAnnotation(getter,
                    Column.class);
            final IAnnotationBinding setterColumn = getAnnotation(setter,
                    Column.class);
            final IAnnotationBinding getterStrategy = getAnnotation(getter,
                    AccessStrategy.class);
            final IAnnotationBinding setterStrategy = getAnnotation(setter,
                    AccessStrategy.class);
            final IAnnotationBinding getterTransformer = getAnnotation(getter,
                    Transform.class);
            final IAnnotationBinding setterTransformer = getAnnotation(setter,
                    Transform.class);
            final String getterColumnValue = (String) getAnnotationValue(getterColumn);
            final String setterColumnValue = (String) getAnnotationValue(setterColumn);
            final StrategyPolicy getterStrategyValue = getEnumerationValue(
                    StrategyPolicy.class, getAnnotationValue(getterStrategy));
            final StrategyPolicy setterStrategyValue = getEnumerationValue(
                    StrategyPolicy.class, getAnnotationValue(setterStrategy));
            final ITypeBinding getterTransformerValue = (ITypeBinding) getAnnotationValue(getterTransformer);
            final ITypeBinding setterTransformerValue = (ITypeBinding) getAnnotationValue(setterTransformer);
            // if both column values are set, create marker
            if (null != getterColumnValue && null != setterColumnValue) {
                final ASTNode getterColumnNode = getCompilationUnit()
                        .findDeclaringNode(getterColumn);
                if (null != getterColumnNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            getterColumnNode, !getterColumnValue
                                    .equals(setterColumnValue)));
                final ASTNode setterColumnNode = getCompilationUnit()
                        .findDeclaringNode(setterColumn);
                if (null != setterColumnNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            setterColumnNode, !setterColumnValue
                                    .equals(getterColumnValue)));
            }
            // if both strategy values are set, create marker
            if (null != getterStrategyValue && null != setterStrategyValue) {
                final ASTNode getterStrategyNode = getCompilationUnit()
                        .findDeclaringNode(getterStrategy);
                if (null != getterStrategyNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            getterStrategyNode, !getterStrategyValue
                                    .equals(setterStrategyValue)));
                final ASTNode setterStrategyNode = getCompilationUnit()
                        .findDeclaringNode(setterStrategy);
                if (null != setterStrategyNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            setterStrategyNode, !setterStrategyValue
                                    .equals(getterStrategyValue)));
            }
            // if both transformer values are set, create marker
            if (null != getterTransformerValue
                    && null != setterTransformerValue) {
                final ASTNode getterTransformerNode = getCompilationUnit()
                        .findDeclaringNode(getterTransformer);
                if (null != getterTransformerNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            getterTransformerNode, !getterTransformerValue
                                    .getQualifiedName().equals(
                                            setterTransformerValue
                                                    .getQualifiedName())));
                final ASTNode setterTransformerNode = getCompilationUnit()
                        .findDeclaringNode(setterTransformer);
                if (null != setterTransformerNode)
                    createMarker(new DoubledAnnotatedMethodsMarker(
                            setterTransformerNode, !setterTransformerValue
                                    .getQualifiedName().equals(
                                            getterTransformerValue
                                                    .getQualifiedName())));
            }
        }
    }
}
