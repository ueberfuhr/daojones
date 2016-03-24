package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.FINE;
import static de.ars.daojones.eclipse.jdt.LoggerConstants.getLogger;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getAnnotation;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.getPropertyName;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isGetter;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isSetter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.Column;
import de.ars.daojones.eclipse.jdt.internal.util.StubUtility;
import de.ars.daojones.runtime.Dao;

/**
 * A detector handling unimplemented methods. This is a base class for all
 * detectors dealing with bean properties and unimplemented methods.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AbstractUnimplementedMethodsDetector extends
        ProblemDetector {

    /* ========================================================================
     * This detector has the following strategy:
     *  1. Collect all unimplemented methods. 
     *     For this, we use StubUtility, a helper class copied from the JDT.
     *  2. Reflect the Dao interface and remove this interface's method
     *      - those have to be implemented by the Code Generator.
     *  3. Also remove annotated getters and setters
     *      - where only one needs to be annotated.
     *  ========================================================================
     */

    /*
     * Prepare class: Analyze Dao interface and collect method information.
     */
    private static final Set<MethodInformation> DAO_METHODS = new HashSet<MethodInformation>();
    static {
        for (Method method : Dao.class.getMethods()) {
            DAO_METHODS.add(new MethodInformation(method));
        }
    }

    /*
     * First, lets declare a transfer object to identify a method in a hash map.
     * An abstraction is necessary because we get methods per AST and per reflection
     * that we have to compare.
     */
    /**
     * A transfer object that is used for handling methods in a map. We have to
     * abstract it because we have to add all non-implemented methods from
     * IMethodBinding and remove all Dao methods from the reflection API.
     * 
     * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
     */
    protected static final class MethodInformation {
        private final String name;
        private final String[] parameterTypeNames;

        /**
         * Creates an instance from AST.
         * 
         * @param method
         *            the method
         */
        private MethodInformation(IMethodBinding method) {
            super();
            this.name = method.getName();
            final List<String> result = new LinkedList<String>();
            for (ITypeBinding type : method.getParameterTypes()) {
                result.add(type.getQualifiedName());
            }
            this.parameterTypeNames = result.toArray(new String[result.size()]);

        }

        /**
         * Creates an instance from the reflection API.
         * 
         * @param method
         *            the method
         */
        public MethodInformation(Method method) {
            super();
            this.name = method.getName();
            final List<String> result = new LinkedList<String>();
            for (Class<?> c : method.getParameterTypes()) {
                result.add(c.getCanonicalName());
            }
            this.parameterTypeNames = result.toArray(new String[result.size()]);
        }

        /**
         * Returns the name of the method.
         * 
         * @return the name of the method
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns the parameter type names. If the method does not have any
         * parameter, the result is an empty array.
         * 
         * @return the parameter type names
         */
        public String[] getParameterTypeNames() {
            return this.parameterTypeNames;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((getName() == null) ? 0 : getName().hashCode());
            result = prime * result + Arrays.hashCode(getParameterTypeNames());
            return result;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof MethodInformation))
                return false;
            MethodInformation other = (MethodInformation) obj;
            if (getName() == null) {
                if (other.getName() != null)
                    return false;
            } else if (!getName().equals(other.getName()))
                return false;
            if (!Arrays.equals(getParameterTypeNames(), other
                    .getParameterTypeNames()))
                return false;
            return true;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append("(");
            boolean first = true;
            for (String paramType : getParameterTypeNames()) {
                if (!first)
                    sb.append(",");
                sb.append(paramType);
                first = false;
            }
            sb.append(")");
            return sb.toString();
        }

    }

    /*
     * Now, we define the map managing the unimplemented methods.
     */
    private final Map<MethodInformation, IMethodBinding> unimplementedMethods = new HashMap<MethodInformation, IMethodBinding>();
    private final Map<MethodInformation, IMethodBinding> methodsByInfo = new HashMap<MethodInformation, IMethodBinding>();
    /*
     * We also need some maps for managing getters and setters.
     *  - "valid" means annotated with @Column
     */
    private final Map<String, MethodInformation> validGettersByProperty = new HashMap<String, MethodInformation>();
    private final Map<String, MethodInformation> invalidGettersByProperty = new HashMap<String, MethodInformation>();
    private final Map<String, MethodInformation> validSettersByProperty = new HashMap<String, MethodInformation>();
    private final Map<String, MethodInformation> invalidSettersByProperty = new HashMap<String, MethodInformation>();

    /*
     * We provide access to them in subclasses.
     */

    /**
     * Returns the unimplemented methods. This includes all invalid
     * getters/setters that do not have a valid setter/getter.
     * 
     * @return the unimplemented methods
     */
    protected Map<MethodInformation, IMethodBinding> getUnimplementedMethods() {
        return unimplementedMethods;
    }

    /**
     * Returns the valid getters.
     * 
     * @return the valid getters
     */
    protected Map<String, MethodInformation> getValidGettersByProperty() {
        return validGettersByProperty;
    }

    /**
     * Returns the invalid getters.
     * 
     * @return the invalid getters
     */
    protected Map<String, MethodInformation> getInvalidGettersByProperty() {
        return invalidGettersByProperty;
    }

    /**
     * Returns the valid setters.
     * 
     * @return the valid setters
     */
    protected Map<String, MethodInformation> getValidSettersByProperty() {
        return validSettersByProperty;
    }

    /**
     * Returns the invalid setters.
     * 
     * @return the invalid setters
     */
    protected Map<String, MethodInformation> getInvalidSettersByProperty() {
        return invalidSettersByProperty;
    }

    /**
     * Returns all methods handled by this instance. Use this map to get the
     * method binding for an unimplemented methods and all valid getters and
     * setters.
     * 
     * @return all methods handled by this instance
     */
    public Map<MethodInformation, IMethodBinding> getMethodsByInfo() {
        return methodsByInfo;
    }

    /*
     * Let's provide a clean operation.
     */
    /**
     * Cleans all internal states.
     */
    private void clean(TypeDeclaration node) {
        this.unimplementedMethods.clear();
        this.methodsByInfo.clear();
        this.validGettersByProperty.clear();
        this.validSettersByProperty.clear();
        this.invalidGettersByProperty.clear();
        this.invalidSettersByProperty.clear();
        // markersToCreate.clear();
        // implementableMethods.clear();
    }

    /*
     * Then we search for unimplemented methods (step1) and add them to the maps.
     */
    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        // clean state
        clean(node);
        if (!super.visit(node))
            return false;
        getLogger().log(FINE,
                "Visiting type " + node.getName().getFullyQualifiedName());
        final ITypeBinding typeBinding = node.resolveBinding();
        if (null != typeBinding) {
            for (final IMethodBinding unimplementedMethod : StubUtility
                    .getUnimplementedMethods(typeBinding, true)) {
                // Create Method Information
                final MethodInformation info = new MethodInformation(
                        unimplementedMethod);
                // put to unimplemented methods
                this.unimplementedMethods.put(info, unimplementedMethod);
                this.methodsByInfo.put(info, unimplementedMethod);
                // insert into maps managing getters/setters by property name
                final boolean isColumnAnnotated = (null != getAnnotation(
                        unimplementedMethod, Column.class));
                if (isGetter(unimplementedMethod)) {
                    (isColumnAnnotated ? validGettersByProperty
                            : invalidGettersByProperty).put(
                            getPropertyName(unimplementedMethod), info);
                } else if (isSetter(unimplementedMethod)) {
                    (isColumnAnnotated ? validSettersByProperty
                            : invalidSettersByProperty).put(
                            getPropertyName(unimplementedMethod), info);
                }
            }
        }
        // we do not have to go deeper
        return false;
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TypeDeclaration)
     */
    @Override
    public void endVisit(TypeDeclaration node) {
        // remove Dao methods and properties from the maps (step2)
        this.unimplementedMethods.keySet().removeAll(DAO_METHODS);
        this.validGettersByProperty.values().removeAll(DAO_METHODS);
        this.validSettersByProperty.values().removeAll(DAO_METHODS);
        this.invalidGettersByProperty.values().removeAll(DAO_METHODS);
        this.invalidSettersByProperty.values().removeAll(DAO_METHODS);
        // remove valid property methods from unimplemented methods
        this.unimplementedMethods.keySet().removeAll(
                this.validGettersByProperty.values());
        this.unimplementedMethods.keySet().removeAll(
                this.validSettersByProperty.values());
        final Map<String, MethodInformation> invalidGettersWithValidSetters = new HashMap<String, MethodInformation>(
                this.invalidGettersByProperty);
        invalidGettersWithValidSetters.keySet().retainAll(
                validSettersByProperty.keySet());
        this.unimplementedMethods.keySet().removeAll(
                invalidGettersWithValidSetters.values());
        final Map<String, MethodInformation> invalidSettersWithValidGetters = new HashMap<String, MethodInformation>(
                this.invalidSettersByProperty);
        invalidSettersWithValidGetters.keySet().retainAll(
                validGettersByProperty.keySet());
        this.unimplementedMethods.keySet().removeAll(
                invalidSettersWithValidGetters.values());
        super.endVisit(node);
        process(node);
        clean(node);
    }

    /**
     * Processes the method information. This method is called during the
     * {@link #endVisit(TypeDeclaration)} phase.
     * 
     * @param node
     *            the type
     * @see #getInvalidGettersByProperty()
     * @see #getInvalidSettersByProperty()
     */
    // * @see #getValidGettersByProperty()
    // * @see #getValidSettersByProperty()
    protected abstract void process(TypeDeclaration node);

}
