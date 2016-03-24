package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;

import de.ars.daojones.eclipse.jdt.markers.IMarker.Severity;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.*;

/**
 * A factory creating markers.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class MarkerUtilities {

	/**
	 * Creates a marker.
	 * @param marker
	 * @param resource
	 * @return the marker
	 * @throws CoreException
	 */
	public static IMarker createMarker(IResource resource, de.ars.daojones.eclipse.jdt.markers.IMarker marker) throws CoreException {
		  final IMarker result = resource.createMarker(marker.getId());
		  final Severity severity = marker.getSeverity();
		  result.setAttribute(IJavaModelMarker.ID, marker.getProblemId());
		  result.setAttribute(IMarker.SEVERITY, Severity.ERROR == severity ? IMarker.SEVERITY_ERROR : (Severity.WARNING == severity ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_INFO));
		  if(marker.getLineNumber()>0) result.setAttribute(IMarker.LINE_NUMBER, marker.getLineNumber());
		  if(marker.getCharStart()>=0) result.setAttribute(IMarker.CHAR_START, marker.getCharStart());
		  if(marker.getCharEnd()>=0) result.setAttribute(IMarker.CHAR_END, marker.getCharEnd());
		  result.setAttribute(IMarker.MESSAGE, marker.getMessage());
		  return result;
	}
	
	/**
	 * Creates a marker.
	 * Use this is case of none-error-handling environments.
	 * @param marker
	 * @param resource
	 * @return the marker or null, if creating the marker fails
	 */
	public static IMarker createMarker2(IResource resource, de.ars.daojones.eclipse.jdt.markers.IMarker marker) {
		try {
			return createMarker(resource, marker);
		} catch (CoreException e) {
			getLogger().log(ERROR, "Unable to create marker", e);
			return null;
		}
	}
	
	/**
	 * Deletes all markers with a special ID from a resource.
	 * @param res the resource
	 * @param id the ID
	 * @param includingSubtypes
	 * @param depth the depth
	 * @see IResource#DEPTH_ZERO
	 * @see IResource#DEPTH_ONE
	 * @see IResource#DEPTH_INFINITE
	 * @throws CoreException
	 */
	public static void deleteMarkers(IResource res, String id, boolean includingSubtypes, int depth) throws CoreException {
		for(IMarker marker : res.findMarkers(id, includingSubtypes, depth)) marker.delete();
	}

	/**
	 * Deletes all markers with a special ID from a resource.
	 * Use this is case of none-error-handling environments.
	 * @param res the resource
	 * @param id the ID
	 * @param includingSubtypes
	 * @param depth the depth
	 * @see IResource#DEPTH_ZERO
	 * @see IResource#DEPTH_ONE
	 * @see IResource#DEPTH_INFINITE
	 */
	public static void deleteMarkers2(IResource res, String id, boolean includingSubtypes, int depth) {
		try {
			deleteMarkers(res, id, includingSubtypes, depth);
		} catch (CoreException e) {
			getLogger().log(ERROR, "Unable to delete marker!", e);
		}
	}

}
