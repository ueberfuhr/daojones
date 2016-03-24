package de.ars.daojones.sdk.codegen.generators;

import java.io.OutputStream;

import de.ars.daojones.beans.model.IBean;

/**
 * A generator generates source code for DaoJones Beans.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Generator {

	/**
	 * Generates the bean code.
	 * 
	 * @param element
	 *            the model element
	 * @param out
	 * @throws GeneratorException
	 */
	public void generate(IBean element, OutputStream out)
			throws GeneratorException;

}
