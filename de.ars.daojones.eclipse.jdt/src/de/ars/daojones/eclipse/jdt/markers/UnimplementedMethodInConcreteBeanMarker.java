package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * A marker that indicates that there are abstract methods in the bean, so the
 * implementation is not concrete.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class UnimplementedMethodInConcreteBeanMarker extends AbstractMarker {

    /**
     * The ID of the marker used in the plugin.xml config file.
     */
    public static final String ID = "de.ars.daojones.eclipse.jdt.markers.unimplementedMethodInConcreteBean";
    private static final String MESSAGE_TYPE = "There are inherited abstract methods that must be implemented:{0}";
    private static final String MESSAGE_METHOD = "The method {0} must be implemented to provide a concrete bean class.";

    private static String replaceParam(String template, String replacement) {
        return template.replaceAll("\\{0\\}", replacement);
    }

    private static String toString(IMethodBinding node) {
        final StringBuilder sb = new StringBuilder();
        sb.append(node.getName());
        sb.append("(");
        boolean first = true;
        for (ITypeBinding param : node.getParameterTypes()) {
            if (!first)
                sb.append(", ");
            sb.append(param.getQualifiedName());
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    private static String join(IMethodBinding[] methods) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (IMethodBinding method : methods) {
            if (!first)
                sb.append(",");
            sb.append("\n    - ");
            sb.append(method.getDeclaringClass().getQualifiedName() + "#");
            sb.append(toString(method));
        }
        return sb.toString();
    }

    /**
     * Creates a marker.
     * 
     * @param node
     */
    public UnimplementedMethodInConcreteBeanMarker(MethodDeclaration node) {
        super(ID, Severity.ERROR, replaceParam(MESSAGE_METHOD, toString(node
                .resolveBinding())), node.getName());
    }

    /**
     * Creates a marker at class level. Use this if there are methods that are
     * inherited from other compilation units.
     * 
     * @param node
     *            the class node
     * @param methods
     *            the methods
     */
    public UnimplementedMethodInConcreteBeanMarker(TypeDeclaration node,
            IMethodBinding[] methods) {
        super(ID, Severity.ERROR, replaceParam(MESSAGE_TYPE, join(methods)),
                node.getName());
    }
}
