package de.ars.daojones.beans.jit;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.runtime.Naming;

/**
 * A utility class providing template functionality.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class GeneratorUtility {

  private static final String CONTEXT_VARIABLE = "util";
  private final VelocityContext context;
  private final Template template;

  /**
   * Creates an instance.
   * 
   * @param context
   *          the {@link VelocityContext}
   * @param template
   *          the {@link Template}
   */
  GeneratorUtility( VelocityContext context, Template template ) {
    super();
    this.context = context;
    this.template = template;
    this.context.put( CONTEXT_VARIABLE, this );
  }

  /**
   * This method iterates over a collection of elements, puts each element into
   * the context using a specific variable name, and executes the expression.
   * The results are concatenated using separator, begin and end. In the
   * expression, u can use a variable named "counter" to get the current
   * zero-based-index within the loop.
   * 
   * @param col
   *          the collection of elements
   * @param var
   *          the variable name that is used within the expression
   * @param expr
   *          the expression to evaluate (note that you have to escape control
   *          sequences in the template)
   * @param begin
   *          the begin of the result string
   * @param separator
   *          the separator of the result string
   * @param end
   *          the end of the result string
   * @return the concatenated string
   * @throws ParseErrorException
   *           The template could not be parsed.
   * @throws MethodInvocationException
   *           A method on a context object could not be invoked.
   * @throws ResourceNotFoundException
   *           A referenced resource could not be loaded.
   * @throws IOException
   *           While rendering to the writer, an I/O problem occured.
   * @see #join(Object[], String, String, String, String, String)
   */
  public String join( Collection<Object> col, String var, String expr,
      String begin, String separator, String end ) throws ParseErrorException,
      MethodInvocationException, ResourceNotFoundException, IOException {
    try {
      if ( null == col || col.isEmpty() )
        return "";
      final StringBuilder sb = new StringBuilder();
      sb.append( begin );
      boolean first = true;
      int idx = 0;
      for ( Object o : col ) {
        if ( !first )
          sb.append( separator );
        final StringWriter out = new StringWriter();
        this.context.put( "idx", idx );
        this.context.put( "counter", idx + 1 );
        this.context.put( var, o );
        try {
          Velocity.evaluate( context, out, this.template.getName(), expr );
        } finally {
          this.context.remove( var );
          this.context.remove( "counter" );
        }
        sb.append( out.toString() );
        first = false;
        idx++;
      }
      sb.append( end );
      return sb.toString();
    } finally {
      this.context.put( CONTEXT_VARIABLE, this );
    }
  }

  /**
   * This method iterates over an array of elements, puts each element into the
   * context using a specific variable name, and executes the expression. The
   * results are concatenated using separator, begin and end. In the expression,
   * u can use a variable named "counter" to get the current zero-based-index
   * within the loop.
   * 
   * @param col
   *          the array of elements
   * @param var
   *          the variable name that is used within the expression
   * @param expr
   *          the expression to evaluate (note that you have to escape control
   *          sequences in the template)
   * @param begin
   *          the begin of the result string
   * @param separator
   *          the separator of the result string
   * @param end
   *          the end of the result string
   * @return the concatenated string
   * @throws ParseErrorException
   *           The template could not be parsed.
   * @throws MethodInvocationException
   *           A method on a context object could not be invoked.
   * @throws ResourceNotFoundException
   *           A referenced resource could not be loaded.
   * @throws IOException
   *           While rendering to the writer, an I/O problem occured.
   * @see #join(Collection, String, String, String, String, String)
   */
  public String join( Object[] col, String var, String expr, String begin,
      String separator, String end ) throws ParseErrorException,
      MethodInvocationException, ResourceNotFoundException, IOException {
    return join( Arrays.asList( col ), var, expr, begin, separator, end );
  }

  /**
   * Returns a flag indicating whether an object is empty, i.e.
   * <ul>
   * <li>if the object is null</li>
   * <li>if the object is an empty string ("")</li>
   * <li>if the object is an empty collection</li>
   * </ul>
   * 
   * @param o
   *          the object
   * @return the empty flag
   */
  @SuppressWarnings( "unchecked" )
  public boolean isEmpty( Object o ) {
    if ( null == o )
      return true;
    if ( o instanceof Collection )
      return ( ( Collection ) o ).isEmpty();
    return false;
  }

  /**
   * Transforms the first character to upper case.
   * 
   * @param text
   *          the text
   * @return the text with the first character in upper case
   */
  public String toFirstUpperCase( String text ) {
    if ( null == text )
      return text;
    if ( text.length() < 2 )
      return text.toUpperCase();
    return text.substring( 0, 1 ).toUpperCase() + text.substring( 1 );
  }

  /*
   * WRAPPING PRIMITIVE TYPES
   */

  private static final Map<String, String> objectTypes = Collections
      .unmodifiableMap( new HashMap<String, String>() {
        private static final long serialVersionUID = -4304606261819361127L;
        {
          put( Boolean.TYPE.getName(), Boolean.class.getName() );
          put( Character.TYPE.getName(), Character.class.getName() );
          put( Byte.TYPE.getName(), Byte.class.getName() );
          put( Short.TYPE.getName(), Short.class.getName() );
          put( Integer.TYPE.getName(), Integer.class.getName() );
          put( Long.TYPE.getName(), Long.class.getName() );
          put( Float.TYPE.getName(), Float.class.getName() );
          put( Double.TYPE.getName(), Double.class.getName() );
          put( Void.TYPE.getName(), Void.class.getName() );
        }
      } );

  private boolean defaultBoolean;
  private byte defaultByte;
  private short defaultShort;
  private int defaultInt;
  private long defaultLong;
  private float defaultFloat;
  private double defaultDouble;

  /**
   * Returns the default value of the primitive type. If the classname is an
   * object type, "null" is returned.
   * 
   * @param className
   *          the name of the primitive type
   * @return the default value
   */
  public String toDefaultValue( String className ) {
    if ( Boolean.TYPE.getName().equals( className ) ) {
      return "" + defaultBoolean;
    } else if ( Character.TYPE.getName().equals( className ) ) {
      return "(char)0";
    } else if ( Byte.TYPE.getName().equals( className ) ) {
      return "" + defaultByte;
    } else if ( Short.TYPE.getName().equals( className ) ) {
      return "" + defaultShort;
    } else if ( Integer.TYPE.getName().equals( className ) ) {
      return "" + defaultInt;
    } else if ( Long.TYPE.getName().equals( className ) ) {
      return "" + defaultLong;
    } else if ( Float.TYPE.getName().equals( className ) ) {
      return "" + defaultFloat;
    } else if ( Double.TYPE.getName().equals( className ) ) {
      return "" + defaultDouble;
    }
    return "null";
  }

  /**
   * Creates an object wrapping instruction that can be used for the JIT compiler.
   * @param className the name of the class of the value
   * @param value the value in a compilable form
   * @return the expression to wrap the value as an object
   */
  public String toObject(String className, String value) {
    if ( Boolean.TYPE.getName().equals( className ) ) {
      return "new " + Boolean.class.getName() + "(" + value + ")";
    } else if ( Character.TYPE.getName().equals( className ) ) {
      return "new " + Character.class.getName() + "(" + value + ")";
    } else if ( Byte.TYPE.getName().equals( className ) ) {
      return "new " + Byte.class.getName() + "(" + value + ")";
    } else if ( Short.TYPE.getName().equals( className ) ) {
      return "new " + Short.class.getName() + "(" + value + ")";
    } else if ( Integer.TYPE.getName().equals( className ) ) {
      return "new " + Integer.class.getName() + "(" + value + ")";
    } else if ( Long.TYPE.getName().equals( className ) ) {
      return "new " + Long.class.getName() + "(" + value + ")";
    } else if ( Float.TYPE.getName().equals( className ) ) {
      return "new " + Float.class.getName() + "(" + value + ")";
    } else if ( Double.TYPE.getName().equals( className ) ) {
      return "new " + Double.class.getName() + "(" + value + ")";
    } else {
      return value;
    }
  }

  /**
   * Returns the name of the object type. If the classname is already the name
   * of an object type, it is returned without modification.
   * 
   * @param className
   *          the name of the primitive type
   * @return the name of the object type
   */
  public String toObjectType( String className ) {
    final String oName = objectTypes.get( className );
    return null != oName ? oName : className;
  }

  /**
   * Returns the statement to change to object typed value to a primitive type.
   * 
   * @param className
   *          the name of the primitive type
   * @return the return statement
   */
  public String toReturnType( String className ) {
    return toReturnType( className, "result" );
  }

  /**
   * Returns the statement to change to object typed value to a primitive type.
   * 
   * @param className
   *          the name of the primitive type
   * @param value
   *          the value
   * @return the return statement
   */
  public String toReturnType( String className, String value ) {
    if ( Boolean.TYPE.getName().equals( className ) ) {
      return value + ".booleanValue()";
    } else if ( Character.TYPE.getName().equals( className ) ) {
      return value + ".charValue()";
    } else if ( Byte.TYPE.getName().equals( className ) ) {
      return value + ".byteValue()";
    } else if ( Short.TYPE.getName().equals( className ) ) {
      return value + ".shortValue()";
    } else if ( Integer.TYPE.getName().equals( className ) ) {
      return value + ".intValue()";
    } else if ( Long.TYPE.getName().equals( className ) ) {
      return value + ".longValue()";
    } else if ( Float.TYPE.getName().equals( className ) ) {
      return value + ".floatValue()";
    } else if ( Double.TYPE.getName().equals( className ) ) {
      return value + ".doubleValue()";
    } else {
      return value;
    }
  }

  /**
   * Returns the name of the non-generic type. If the classname does not have
   * any generic parameters, it is returned without modification.
   * 
   * @param className
   *          the name of the generic type
   * @return the name of the non-generic type
   */
  public String toNonGenericType( String className ) {
    int i = className.indexOf( '<' );
    return i >= 0 ? className.substring( 0, i ) : className;
  }

  /**
   * Returns the name of the logger used for the given bean.
   * 
   * @param bean
   *          the bean
   * @return the name of the logger
   */
  public String loggerName( IBean bean ) {
    final String qName = ( null != bean.getPackageName() ? bean
        .getPackageName()
        + "." : "" )
        + bean.getName();
    return Naming.getImplementationClassName( qName );
  }
  
}
