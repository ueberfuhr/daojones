package de.ars.daojones.beans.jit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.LoaderClassPath;
import javassist.Modifier;
import de.ars.daojones.ConnectionFactory;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.impl.jit.Bean;
import de.ars.daojones.beans.model.impl.jit.Constructor;
import de.ars.daojones.beans.model.impl.jit.PropertyMethod;
import de.ars.daojones.beans.model.impl.jit.Utilities;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataObjectContainer;
import de.ars.daojones.runtime.Naming;
import de.ars.daojones.runtime.internal.OverrideAllowed;

/**
 * A class loader that creates the implementation class bytecode
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class JITBeanClassloader extends ClassLoader {

  private static final Logger logger = Logger
      .getLogger( JITBeanClassloader.class.getName() );
  private static final Level level = Level.FINER;

  private ClassPool classPool;
  private static final Set<ClassLoader> classPathClassLoaders = new HashSet<ClassLoader>();

  /**
   * Creates an instance.
   */
  public JITBeanClassloader() {
    super();
  }

  /**
   * Creates an instance.
   * 
   * @param parentLoader
   *          the parent class laoder
   */
  public JITBeanClassloader( ClassLoader parentLoader ) {
    super( parentLoader );
  }

  /**
   * Returns the class pool. The pool automatically has the class path of the
   * given class.
   * 
   * @param c
   *          a class that has a classloader that the pool should be extended
   * @return the class pool
   */
  protected ClassPool getClassPool( Class<?> c ) {
    synchronized ( this ) {
      if ( null == this.classPool ) {
        this.classPool = ClassPool.getDefault();
        this.classPool.importPackage( Dao.class.getPackage().getName() );
        this.classPool.importPackage( ConnectionFactory.class.getPackage()
            .getName() );
      }
      if ( null != c ) {
        final ClassLoader loader = c.getClassLoader();
        synchronized ( JITBeanClassloader.classPathClassLoaders ) {
          if ( loader != this
              && JITBeanClassloader.classPathClassLoaders.add( loader ) ) {
            this.classPool.appendClassPath( new LoaderClassPath( loader ) );
          }
        }
      }
      return this.classPool;
    }
  }

  /**
   * Transforms an array of class objects to an array of class names.
   * 
   * @param c
   *          the class objects
   * @return the class names
   */
  private static String[] toTypenames( CtClass[] c ) {
    final String[] result = new String[c.length];
    for ( int i = 0; i < c.length; i++ ) {
      result[i] = c[i].getName();
    }
    return result;
  }

  private static void appendMethodInvocationParameters( StringBuffer sb,
      int paramCount, String... parameterNames ) throws Throwable {
    sb.append( '(' );
    for ( int i = 0; i < paramCount; i++ ) {
      if ( i > 0 )
        sb.append( ',' );
      if ( parameterNames.length > i && null != parameterNames[i] ) {
        sb.append( parameterNames[i] );
      } else {
        sb.append( "$" );
        sb.append( Integer.toString( i + 1 ) );
      }
    }
    sb.append( ')' );
  }

  private static void appendMethodInvocation( StringBuffer sb, CtMethod method,
      String... parameterNames ) throws Throwable {
    sb.append( method.getName() );
    appendMethodInvocationParameters( sb, method.getParameterTypes().length,
        parameterNames );
  }

  private static void createPropertyMethod( CtClass clazz,
      IPropertyMethod propertyMethod ) throws CannotCompileException,
      JITGeneratorException {
    if ( null != propertyMethod ) {
      final CtMethod method = ( ( PropertyMethod ) propertyMethod )
          .getCtMethod();
      final CtMethod impl = new CtMethod( method, clazz, null );
      impl.setModifiers( Utilities.removeFlag( impl.getModifiers(),
          Modifier.ABSTRACT ) );
      final String body = JITGenerator
          .generatePropertyMethodBody( propertyMethod );
      logger.log( level, "Body for method \"" + impl + "\" :\n" + body );
      impl.setBody( body.toString() );
      clazz.addMethod( impl );
    }
  }

  private static void configure( CtClass clazz, IBean bean ) throws Throwable {
    final ClassPool pool = clazz.getClassPool();
    /*
     * try to make bean accessible
     */
    final CtClass beanClass = ( ( Bean ) bean ).getCtClass();
    try {
      /*
       * inheritation of bean class
       */
      if ( bean.isInterface() ) {
        clazz.setInterfaces( new CtClass[] { beanClass } );
      } else {
        clazz.setSuperclass( beanClass );
      }
      /*
       * modifiers
       */
      final Set<de.ars.daojones.beans.model.Modifier> clazzModifiers = bean
          .getModifiers();
      if ( !bean.isAbstract() ) {
        clazzModifiers.remove( de.ars.daojones.beans.model.Modifier.ABSTRACT );
      }
      clazzModifiers.add( de.ars.daojones.beans.model.Modifier.PUBLIC );
      clazz.setModifiers( Utilities.toModifiers( clazzModifiers ) );
      /*
       * delegate to implement dao methods
       */
      final CtClass daoType = pool.get( Dao.class.getName() );
      final CtClass daoImplType = pool
          .get( DataObjectContainer.class.getName() );
      final String delegateName = "delegate";
      final CtField delegate = new CtField( daoImplType, delegateName, clazz );
      clazz.addField( delegate, "new " + DataObjectContainer.class.getName()
          + "(this)" );
      /*
       * initializer
       */
      final CtMethod initializer = new CtMethod( CtPrimitiveType.voidType,
          "init", new CtClass[0], clazz );
      initializer.setModifiers( Modifier.PRIVATE );
      final String initializerBody = JITGenerator
          .generateInitializerBody( bean );
      logger.log( level, "Body for initializer :\n" + initializerBody );
      initializer.setBody( initializerBody );
      clazz.addMethod( initializer );
      final Collection<IProperty> properties = bean.getProperties();
      /*
       * constructors
       */
      if ( !bean.isInterface() ) {
        for ( IConstructor con : bean.getConstructors() ) {
          if ( !con.getModifiers().contains(
              de.ars.daojones.beans.model.Modifier.PRIVATE ) ) {
            final CtConstructor origin = ( ( Constructor ) con )
                .getCtConstructor();
            final CtConstructor newCon = new CtConstructor( origin, clazz, null );
            final String constructorBody = JITGenerator
                .generateConstructorBody( con );
            logger.log( level, "Body for constructor :\n" + constructorBody );
            newCon.setBody( constructorBody.toString() );
            clazz.addConstructor( newCon );
          }
        }
      }
      /*
       * properties
       */
      for ( IProperty prop : properties ) {
        createPropertyMethod( clazz, prop.getGetter() );
        createPropertyMethod( clazz, prop.getSetter() );
      }
      /*
       * implement Dao methods
       */
      for ( CtMethod m : daoType.getDeclaredMethods() ) {
        final boolean overrideAllowed = null != m
            .getAnnotation( OverrideAllowed.class );
        final boolean overwritten = overrideAllowed
            && bean.isMethodImplemented( m.getName(), toTypenames( m
                .getParameterTypes() ) );
        final boolean hasReturnType = !CtPrimitiveType.voidType.equals( m
            .getReturnType() );
        final CtMethod method = new CtMethod( m, clazz, null );
        method.setModifiers( Utilities.removeFlag( method.getModifiers(),
            Modifier.ABSTRACT ) );
        final StringBuffer body = new StringBuffer();
        body.append( '{' );
        body.append( "\n  " );
        if ( hasReturnType ) {
          body.append( "return " );
        }
        body.append( overwritten && hasReturnType ? "super." : "delegate." );
        appendMethodInvocation( body, method );
        body.append( ";\n" );
        if ( overwritten && !hasReturnType ) {
          body.append( "  super." );
          appendMethodInvocation( body, method );
          body.append( ";\n" );
        }
        body.append( '}' );
        logger.log( level, "Body for method \"" + m.getName() + "\":\n" + body );
        method.setBody( body.toString() );
        clazz.addMethod( method );
      }
    } finally {
      // beanClass.detach();
    }
  }

  /**
   * @see java.lang.ClassLoader#findClass(String)
   */
  @Override
  protected Class<?> findClass( String className )
      throws ClassNotFoundException {
    if ( Naming.isImplementationClassName( className ) ) {
      final String beanClassName = Naming.getBeanClassName( className );
      logger.log( level, "Create bytecode for the implementation of \""
          + beanClassName + "\"." );
      final Class<?> beanClass = this.loadClass( beanClassName );
      try {
        final ClassPool pool = getClassPool( beanClass );
        pool
            .appendClassPath( new LoaderClassPath( beanClass.getClassLoader() ) );
        final IBean bean = new Bean( pool.get( beanClassName ), beanClass );
        final CtClass result = pool.makeClass( className );
        try {
          configure( result, bean );
          // it is necessary to apply the parent classloader to extends
          final Class<?> c = result.toClass( beanClass.getClassLoader(),
              beanClass.getProtectionDomain() );
          return c;
        } finally {
          // result.detach();
        }
      } catch ( Throwable t ) {
        throw new ClassNotFoundException(
            "Unable to create bytecode for class \"" + className + "\"!", t );
      }
    } else {
      return super.findClass( className );
    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getName() + "\n  - parent: " + getParent();
  }

}
