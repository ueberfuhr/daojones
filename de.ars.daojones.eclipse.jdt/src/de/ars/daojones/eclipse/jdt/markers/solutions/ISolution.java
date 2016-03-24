package de.ars.daojones.eclipse.jdt.markers.solutions;

import org.eclipse.core.runtime.CoreException;

/**
 * A solution handling a problem within
 * an {@link ISolutionExecutionEnvironment}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ISolution {
	
	/**
	 * Returns the title of the solution.
	 * @return the title of the solution
	 */
	public String getTitle();
	
	/**
	 * Returns the description of the solution.
	 * @return the description of the solution
	 */
	public String getDescription();

	/**
	 * Solves a special problem.
	 * @param env
	 * @throws CoreException
	 */
	public void solve(ISolutionExecutionEnvironment env) throws CoreException;
	
}
