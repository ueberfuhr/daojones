package de.ars.daojones.runtime.query;

/**
 * An interface for an object that manages templates by a name.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface TemplateManager {

	/**
	 * Returns the template.
	 * @param key the name of the template
	 * @return the template
	 */
	public String getTemplate(String key);

}
