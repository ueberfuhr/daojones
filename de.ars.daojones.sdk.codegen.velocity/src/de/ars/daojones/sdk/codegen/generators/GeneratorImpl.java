package de.ars.daojones.sdk.codegen.generators;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import de.ars.daojones.beans.model.IBean;

/**
 * Implementation of {@link Generator} using Apache Velocity.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class GeneratorImpl implements Generator {

	static {
		final String CLASSPATH_LOADER_ID = "classpath";
		Velocity.setProperty(Velocity.RESOURCE_LOADER, CLASSPATH_LOADER_ID);
		Velocity.setProperty(CLASSPATH_LOADER_ID + "."
				+ Velocity.RESOURCE_LOADER + ".class",
				ClasspathResourceLoader.class.getName());
	}

	/**
	 * @see de.ars.daojones.sdk.codegen.generators.Generator#generate(de.ars.daojones.beans.model.IBean,
	 *      java.io.OutputStream)
	 */
	@Override
	public void generate(IBean element, OutputStream out)
			throws GeneratorException {
		try {
			final VelocityContext ctx = new VelocityContext();
			ctx.put("bean", element);
			// possibility to insert dollar sign
			ctx.put("esc", new EscapeTool());
			final Template template = Velocity.getTemplate("bean.vm");
			new GeneratorUtility(ctx, template);
			final PrintWriter pw = new PrintWriter(out);
			template.merge(ctx, pw);
			pw.flush();
		} catch (Exception e) {
			throw new GeneratorException(e);
		}
	}

}
