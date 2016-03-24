package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.markers.MarkerUtilities.createMarker2;
import static de.ars.daojones.eclipse.jdt.markers.MarkerUtilities.deleteMarkers2;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.ars.daojones.eclipse.jdt.markers.AbstractMarker;

/**
 * A class identifying problems by visiting the AST and creating markers in case
 * of problems. Note that there is only one instance of each detector running
 * sequentially. You should re-initialize instance variables using
 * {@link #visit(org.eclipse.jdt.core.dom.CompilationUnit)} and
 * {@link #endVisit(org.eclipse.jdt.core.dom.CompilationUnit)}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ProblemDetector extends ASTVisitor {

    private ProblemDetectorEnvironment env;
    private CompilationUnit compilationUnit;

    void setEnvironment(ProblemDetectorEnvironment env) {
        this.env = env;
    }

    /**
     * Returns the environment for the current building process.
     * 
     * @return the environment
     */
    protected ProblemDetectorEnvironment getEnvironment() {
        return env;
    }

    /**
     * Returns the {@link CompilationUnit}.
     * 
     * @return the {@link CompilationUnit}
     */
    protected CompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }

    /**
     * Returns the ids of the problem markers.
     * 
     * @return the ids of the problem markers
     */
    protected abstract String[] getMarkerIds();

    /**
     * Creates a marker associated to the given node.
     * 
     * @param marker
     *            the marker
     */
    protected void createMarker(AbstractMarker marker) {
        createMarker2(getEnvironment().getSource(), marker);
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CompilationUnit)
     */
    @Override
    public boolean visit(CompilationUnit node) {
        this.compilationUnit = node;
        // delete all existing markers
        for (String markerId : getMarkerIds()) {
            deleteMarkers2(getEnvironment().getSource(), markerId, true,
                    IResource.DEPTH_INFINITE);
        }
        return super.visit(node);
    }

    /**
     * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.CompilationUnit)
     */
    @Override
    public void endVisit(CompilationUnit node) {
        this.compilationUnit = null;
        super.endVisit(node);
    }

}
