package de.ars.daojones.drivers.notes;


import java.io.IOException;
import java.util.Properties;

import de.ars.daojones.runtime.query.TemplateManager;

import static de.ars.daojones.drivers.notes.LoggerConstants.*;

class NotesTemplateManager implements TemplateManager {

	private static NotesTemplateManager theInstance;
	private Properties templates;
	
	private NotesTemplateManager() {}
	
	public static synchronized NotesTemplateManager getInstance() {
		if(null == theInstance) theInstance = new NotesTemplateManager();
		return theInstance;
	}
	
	private static final String PROPERTIES_FILE = "/queries.properties";
	
	private synchronized Properties getTemplates() {
		if(null == templates) {
			templates = new Properties();
			try {
				templates.load(NotesTemplateManager.class.getResourceAsStream(PROPERTIES_FILE));
			} catch (IOException e) {
				getLogger().log(ERROR, "Error when loading file \"" + PROPERTIES_FILE + "\"!", e);
			}
		};
		return templates;
	}
	
	public String getTemplate(String key) {
		final String result = getTemplates().getProperty(key);
		if(null == result) throw new NullPointerException("There is no entry with key \"" + key + "\" in the file " + PROPERTIES_FILE + "!");
		return result;
	}
	
}
