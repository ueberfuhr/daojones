package de.ars.daojones.eclipse.jdt.beans;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A node finder finding elements by index
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 */
public class NodeFinder {
	
	private NodeFinder() {
		super();
	}
	
	/**
	 * Finds a node by the given position in the source.
	 * @param unit
	 * @param fStart
	 * @param fEnd
	 * @return the node or null, if no node could be found
	 */
	public static ASTNode find(final CompilationUnit unit, final int fStart, final int fEnd) {
		final ASTNode[] result = new ASTNode[1];
		unit.accept(new GenericVisitor() {
			@Override
			protected boolean visitNode(ASTNode node) {
				int nodeStart= node.getStartPosition();
				int nodeEnd= nodeStart + node.getLength();
				if (nodeEnd < fStart || fEnd < nodeStart) {
					return false;
				}
				if (nodeStart <= fStart && fEnd <= nodeEnd) {
					result[0]= node;
				}
				if (fStart <= nodeStart && nodeEnd <= fEnd) {
					if (result[0] == node) { // nodeStart == fStart && nodeEnd == fEnd
						result[0]= node;
						return true; // look further for node with same length as parent
					} else if (result[0] == null) { // no better found
						result[0]= node;
					}
					return false;
				}
				return true;
			}

		});
		return result[0];
	}
	
}
