package de.ars.daojones.sdk.codegen.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A template used to generate text modules.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Template {

	private final String templateText;
	private final Map<String, String> variables = new HashMap<String, String>();

	private static String read(InputStream in) throws IOException {
		final InputStreamReader inReader = new InputStreamReader(in);
		try {
			final BufferedReader br = new BufferedReader(inReader);
			try {
				final StringBuffer sb = new StringBuffer();
				String line = null; 
		        while (( line = br.readLine()) != null){
		          sb.append(line);
		          sb.append(System.getProperty("line.separator"));
		        }
		        return sb.toString();
			} finally {
				br.close();
			}
		} finally {
			inReader.close();
		}
	}
	
	/**
	 * Creates a template based on a given {@link InputStream}.
	 * @param in the {@link InputStream}
	 * @throws IOException if reading from the {@link InputStream} fails
	 */
	public Template(InputStream in) throws IOException {
		this(read(in));
	}
	
	/**
	 * Creates a template based on a given template text.
	 * The text can contain variables in EL Syntax (<code>${...}</code>).
	 * @param templateText the template text
	 */
	public Template(String templateText) {
		super();
		this.templateText = templateText;
	}
	
	/**
	 * Returns the template text.
	 * @return the template text
	 */
	private String getTemplateText() {
		return this.templateText;
	}
	
	/**
	 * Sets the value of a variable.
	 * @param name the name of the variable
	 * @param value the value of the variable
	 */
	public void setVariable(String name, String value) {
		this.variables.put(name, value);
	}
	
	/**
	 * Copies all variables from another template.
	 * @param template the source template
	 */
	public void setVariables(Template template) {
		this.variables.putAll(template.variables);
	}
	
	/**
	 * Removes all variables.
	 */
	public void clearVariables() {
		this.variables.clear();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String text = getTemplateText();
		for(Map.Entry<String, String> entry : this.variables.entrySet()) {
			text = text.replaceAll("\\$\\{" + entry.getKey() + "\\}", null != entry.getValue() ? Matcher.quoteReplacement(entry.getValue()) : null);
		}
		return text;
	}

	
	
}
