package de.ars.daojones.drivers.notes;

import java.util.Collection;
import java.util.LinkedList;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

abstract class ViewHelper {

	/**
	 * Finds an entry in the view that has the given document universal id.
	 * 
	 * @param view
	 *            the view
	 * @param unid
	 *            the document universal id
	 * @return the {@link ViewEntry} or null, if not found
	 * @throws NotesException
	 */
	public static ViewEntry findEntryByUNID(View view, String unid)
			throws NotesException {
		final ViewEntryCollection col = view.getAllEntries();
		for (ViewEntry entry = col.getFirstEntry(); null != entry; entry = col
				.getNextEntry()) {
			if (unid.equals(entry.getUniversalID())) {
				return entry;
			} else {
			    //recycle methods occurs null value on reading next entry
				//entry.recycle();
			}
		}
		return null;
	}

	/**
	 * Reads the metadata from the view.
	 * 
	 * @param view
	 *            the view
	 * @return the {@link ViewColumnMetadata} objects
	 * @throws NotesException
	 */
	@SuppressWarnings("unchecked")
	public static ViewColumnMetadata[] getColumns(View view)
			throws NotesException {
		final Collection<ViewColumnMetadata> result = new LinkedList<ViewColumnMetadata>();
		for (ViewColumn col : (Collection<ViewColumn>) view.getColumns())
			result.add(new ViewColumnMetadata(col));
		return result.toArray(new ViewColumnMetadata[result.size()]);
	}

}
