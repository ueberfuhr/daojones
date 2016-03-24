package de.ars.daojones.eclipse.ui.editor.connection;

import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.runtime.Dao;

/**
 * Just a dao for testing connections.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@DataSource(value="Test")
abstract class TestDao implements Dao {

	private static final long serialVersionUID = 8507030703267242535L;

}
