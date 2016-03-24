package de.ars.daojones.beans.jit;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IModelElement;
import de.ars.daojones.beans.model.IPropertyMethod;

/**
 * A helper class providing methods for generating the body of some bean
 * members.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class JITGenerator {

  static {
    final String CLASSPATH_LOADER_ID = "classpath";
    Velocity.setProperty( Velocity.RESOURCE_LOADER, CLASSPATH_LOADER_ID );
    Velocity.setProperty( CLASSPATH_LOADER_ID + "." + Velocity.RESOURCE_LOADER
        + ".class", ClasspathResourceLoader.class.getName() );
  }

  private JITGenerator() {
    super();
  }

  private static String generateBody( IModelElement element,
      String elementName, String templateName ) throws JITGeneratorException {
    final StringWriter out = new StringWriter();
    try {
      final VelocityContext ctx = new VelocityContext();
      ctx.put( elementName, element );
      // possibility to insert dollar sign
      ctx.put( "esc", new EscapeTool() );
      final Template template = getTemplate( templateName );
      new GeneratorUtility( ctx, template );
      template.merge( ctx, out );
    } catch ( Exception e ) {
      throw new JITGeneratorException( e );
    }
    return out.toString();
  }

  private static Template getTemplate( String name ) throws Exception {
    final Template result = Velocity.getTemplate( "de/ars/daojones/beans/jit/"
        + name + ".vm" );
    return result;
  }

  /**
   * Generates the body of a constructor.
   * 
   * @param constructor
   *          the {@link IConstructor}
   * @return the body of a constructor
   * @throws JITGeneratorException
   */
  public static String generateConstructorBody( IConstructor constructor )
      throws JITGeneratorException {
    return generateBody( constructor, "constructor", "constructor" );
  }

  /**
   * Generates the body of the instance initializer.
   * 
   * @param bean
   *          the bean
   * @return the body of the instance initializer
   * @throws JITGeneratorException
   */
  public static String generateInitializerBody( IBean bean )
      throws JITGeneratorException {
    return generateBody( bean, "bean", "initializer" );
  }

  public static String generatePropertyMethodBody(IPropertyMethod method) throws JITGeneratorException {
    return generateBody( method, "method", "propertymethod" );
  }
  
}
