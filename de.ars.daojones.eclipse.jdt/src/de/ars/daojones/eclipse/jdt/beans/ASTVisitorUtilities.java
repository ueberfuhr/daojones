package de.ars.daojones.eclipse.jdt.beans;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataObjectContainer;

/**
 * A class containing utility methods for {@link ASTVisitor}s.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ASTVisitorUtilities {

    /**
     * Returns true if the class is imported by the given
     * {@link ImportDeclaration}.
     * 
     * @param c
     *            the class
     * @param node
     *            the {@link ImportDeclaration}
     * @return true if the class is imported by the given
     *         {@link ImportDeclaration}
     */
    public static boolean isImported(Class<?> c, ImportDeclaration node) {
        final String qName = node.getName().getFullyQualifiedName();
        return qName.equals(c.getName())
                || qName.equals(c.getPackage().getName() + ".*");
    }

    /**
     * Returns the parent with the given type or null, if no such parent exists.
     * 
     * @param <T>
     *            the parent's node type
     * @param child
     *            the child
     * @param parentNodeType
     *            the parent's node type
     * @return the parent with the given type or null, if no such parent exist
     */
    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T findAncestorWithType(ASTNode child,
            Class<T> parentNodeType) {
        if (null != child) {
            if (parentNodeType.isAssignableFrom(child.getClass()))
                return (T) child;
            ASTNode running = child;
            while ((running = running.getParent()) != null
                    && !parentNodeType.isAssignableFrom(running.getClass()))
                ;
            if (null != running)
                return (T) running;
        }
        return null;
    }

    /**
     * Returns the annotation with the given type or null, if no such annotation
     * is set.
     * 
     * @param node
     *            the annotated {@link ASTNode}
     * @param annotationType
     *            the annotation type
     * @return the annotation or null, if no such annotation exists
     */
    public static IAnnotationBinding getAnnotation(IBinding node,
            Class<? extends java.lang.annotation.Annotation> annotationType) {
        if (null == node)
            return null;
        for (IAnnotationBinding ann : node.getAnnotations()) {
            if (ann.getAnnotationType().getQualifiedName().equals(
                    annotationType.getName()))
                return ann;
        }
        ;
        return null;
    }

    /**
     * Returns the value of an annotation's attribute.
     * 
     * @param annotation
     *            the annotation
     * @return the value
     */
    public static Object getAnnotationValue(IAnnotationBinding annotation) {
        return getAnnotationValue(annotation, "value");
    }

    /**
     * Returns the literal of an enumeration object. If the object is part of
     * the enumeration, it is returned. If the object is an instance of
     * {@link IVariableBinding}, the binding is resolved. If the object is
     * another object, it's {@link #toString()} method is used to solve the
     * enumeration literal. If the object is null or it could not be found in
     * the enumeration, null is returned.
     * 
     * @param <T>
     *            the enum type
     * @param enumClass
     *            the enum class
     * @param value
     *            the object to solve
     * @return the enum literal or null, if not solvable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnumerationValue(Class<T> enumClass,
            Object value) {
        if (null != value && enumClass.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (value instanceof IVariableBinding) {
            return Enum
                    .valueOf(enumClass, ((IVariableBinding) value).getName());
        } else if (value != null) {
            return Enum.valueOf(enumClass, value.toString());
        } else {
            return null;
        }
    }

    /**
     * Returns the value of an annotation's attribute.
     * 
     * @param annotation
     *            the annotation
     * @param attributeName
     *            the name of the attribute
     * @return the value
     */
    public static Object getAnnotationValue(IAnnotationBinding annotation,
            String attributeName) {
        if (null != annotation) {
            for (IMemberValuePairBinding pair : annotation
                    .getAllMemberValuePairs()) {
                if (attributeName.equals(pair.getName()))
                    return pair.getValue();
            }
        }
        return null;
    }

    /**
     * Returns true, if the node has the given modifier.
     * 
     * @param node
     *            the node
     * @param keyword
     *            the modifier
     * @see ModifierKeyword
     * @return true, if the node has the given modifier
     */
    @SuppressWarnings("unchecked")
    public static boolean hasModifier(BodyDeclaration node,
            ModifierKeyword keyword) {
        for (IExtendedModifier modifier : (List<IExtendedModifier>) node
                .modifiers()) {
            if (modifier instanceof Modifier) {
                final Modifier mod = (Modifier) modifier;
                if (mod.getKeyword() == keyword)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if the node has the given modifier.
     * 
     * @param node
     *            the node
     * @param modifier
     * @return true, if the node has the given modifier
     * @see Modifier
     */
    public static boolean hasModifier(IBinding node, int modifier) {
        return null != node && (node.getModifiers() & modifier) > 0;
    }

    /**
     * Returns true, if the given node is a getter method.
     * 
     * @param node
     *            the node
     * @return true, if the given node is a getter method
     */
    public static boolean isGetter(IMethodBinding node) {
        if (null == node)
            return false;
        // Check return type
        final ITypeBinding returnType = node.getReturnType();
        if (null == returnType)
            return false;
        if ("void".equals(returnType.getName()))
            return false;
        // Check name
        final String name = node.getName();
        final String prefix = "boolean".equals(returnType.getName()) ? "is"
                : "get";
        if (name.length() < prefix.length() + 1 || !name.startsWith(prefix)
                || !Character.isUpperCase(name.charAt(prefix.length())))
            return false;
        // Check parameters
        final ITypeBinding[] parameters = node.getParameterTypes();
        if (null != parameters && parameters.length > 0)
            return false;
        // Check modifiers
        if (hasModifier(node, Modifier.PRIVATE))
            return false;
        return true;
    }

    /**
     * Returns true, if the given node is a setter method.
     * 
     * @param node
     *            the node
     * @return true, if the given node is a setter method
     */
    public static boolean isSetter(IMethodBinding node) {
        if (null == node)
            return false;
        // Check return type
        final ITypeBinding returnType = node.getReturnType();
        if (null == returnType)
            return false;
        if (!"void".equals(returnType.getName()))
            return false;
        // Check name
        final String name = node.getName();
        final String prefix = "set";
        if (name.length() < prefix.length() + 1 || !name.startsWith(prefix)
                || !Character.isUpperCase(name.charAt(prefix.length())))
            return false;
        // Check parameters
        final ITypeBinding[] parameters = node.getParameterTypes();
        if (null == parameters || parameters.length != 1)
            return false;
        // Check modifiers
        if (hasModifier(node, Modifier.PRIVATE))
            return false;
        return true;
    }

    /**
     * Compares two methods and returns true, if node1 and node2 are getter and
     * setter of the same property. It does not matter which method of them is
     * the getter and which one is the setter. This method does not check
     * whether the methods are declared an the same type or one of its
     * supertypes.
     * 
     * @param node1
     * @param node2
     * @return true, if node1 and node2 are getter and setter of the same
     *         property
     */
    public static boolean isSibling(IMethodBinding node1, IMethodBinding node2) {
        final boolean isGetter1 = isGetter(node1);
        final boolean isSetter1 = !isGetter1 && isSetter(node1);
        final boolean isGetter2 = isGetter(node2);
        final boolean isSetter2 = !isGetter2 && isSetter(node2);
        if (isGetter1 && isSetter2) {
            return _isSibling(node1, node2);
        } else if (isSetter1 && isGetter2) {
            return _isSibling(node2, node1);
        } else {
            return false;
        }
    }

    /**
     * Compares two methods and returns true, if node1 and node2 are getter and
     * setter of the same property. The first parameter must be a getter, the
     * second must be a setter method. This method does not check whether the
     * methods are declared an the same type or one of its supertypes.
     * 
     * @param node1
     * @param node2
     * @return true, if node1 and node2 are getter and setter of the same
     *         property
     */
    private static boolean _isSibling(IMethodBinding node1, IMethodBinding node2) {
        // Compare property type
        final ITypeBinding propertyType = node1.getReturnType();
        if (!(node2.getParameterTypes()[0].getQualifiedName()
                .equals(propertyType.getQualifiedName())))
            return false;
        // Compare property name
        final String prefix1 = Boolean.TYPE.getName().equals(
                propertyType.getName()) ? "is" : "get";
        final String prefix2 = "set";
        final String name1 = node1.getName();
        final String name2 = node2.getName();
        if (!name1.substring(prefix1.length()).equals(
                name2.substring(prefix2.length())))
            return false;
        // Done!
        return true;
    }

    /**
     * Returns true, if the given methods have the same signature, i.e. that the
     * one overwrites the second if they consist in the same type hierarchy.
     * 
     * @param m1
     *            method1
     * @param m2
     *            method2
     * @return true, if the given methods have the same signature;
     */
    public static boolean isSignatureEqual(IMethodBinding m1, IMethodBinding m2) {
        if (null == m1 || null == m2)
            return false;
        if (!m1.getName().equals(m2.getName()))
            return false;
        final ITypeBinding[] p1 = m1.getParameterTypes();
        final ITypeBinding[] p2 = m2.getParameterTypes();
        if (p1.length != p2.length)
            return false;
        for (int i = 0; i < p1.length; i++) {
            if (!p1[i].getQualifiedName().equals(p2[i].getQualifiedName()))
                return false;
        }
        return true;
    }

    /**
     * Returns the name of the property that the given method is accessor for.
     * 
     * @param binding
     *            the method
     * @return the name of the property that the given method is accessor for or
     *         null, if the method is not a getter or a setter
     */
    public static String getPropertyName(IMethodBinding binding) {
        if (!isGetter(binding) && !isSetter(binding))
            return null;
        final String name = binding.getName();
        final int prefixLength = isGetter(binding) && name.startsWith("is") ? 2
                : 3;
        return name.substring(prefixLength, prefixLength + 1).toLowerCase()
                + (name.length() > prefixLength + 1 ? name
                        .substring(prefixLength + 1) : "");
    }

    /**
     * Returns the type of the property that the given method is accessor for.
     * 
     * @param binding
     *            the method
     * @return the type of the property that the given method is accessor for or
     *         null, if the method is not a getter or a setter
     */
    public static ITypeBinding getPropertyType(IMethodBinding binding) {
        if (isGetter(binding)) {
            return binding.getReturnType();
        } else if (isSetter(binding)) {
            return binding.getParameterTypes()[0];
        }
        ;
        return null;
    }

    /**
     * Returns true, if the type is java.lang.Object.
     * 
     * @param type
     *            the type
     * @return true, if the type is java.lang.Object
     */
    public static boolean isObjectType(Type type) {
        return null == type || isObjectType(type.resolveBinding());
    }

    /**
     * Returns true, if the type is java.lang.Object.
     * 
     * @param type
     *            the type
     * @return true, if the type is java.lang.Object
     */
    public static boolean isObjectType(ITypeBinding type) {
        return null == type
                || Object.class.getName().equals(type.getQualifiedName());
    }

    /**
     * Returns true, if the type is java.lang.Object.
     * 
     * @param type
     *            the type
     * @return true, if the type is java.lang.Object
     */
    public static boolean isDaoJonesSuperType(ITypeBinding type) {
        return null != type
                && (Dao.class.getName().equals(type.getQualifiedName()) || DataObjectContainer.class
                        .getName().equals(type.getQualifiedName()));
    }

    /**
     * Returns true if the given type is a DaoJones Bean.
     * 
     * @param type
     *            the type to check for being a DaoJones Bean
     * @return true if the given type is a DaoJones Bean
     */
    public static boolean isDaoJonesBean(ITypeBinding type) {
        if (null == type || isObjectType(type))
            return false;
        if (isDaoJonesSuperType(type))
            return true;
        if ((null != getAnnotation(type, DataSource.class))
                || isDaoJonesBean(type.getSuperclass()))
            return true;
        for (ITypeBinding i : type.getInterfaces()) {
            if (isDaoJonesBean(i))
                return true;
        }
        return false;
    }

    /**
     * Returns true, if the subClass implements or extends the superClass
     * 
     * @param subClass
     * @param superClass
     *            the fully qualified name of the superClass
     * @return true, if the subClass implements or extends the superClass
     */
    public static boolean isAssignableFrom(ITypeBinding subClass,
            String superClass) {
        if (null == subClass || null == superClass)
            return false;
        if (subClass.getQualifiedName().equals(superClass))
            return true;
        if (!isObjectType(subClass)) {
            if (isAssignableFrom(subClass.getSuperclass(), superClass))
                return true;
            for (ITypeBinding i : subClass.getInterfaces()) {
                if (isAssignableFrom(i, superClass))
                    return true;
            }
        }
        return false;
    }

}
