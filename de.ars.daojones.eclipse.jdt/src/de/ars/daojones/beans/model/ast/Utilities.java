package de.ars.daojones.beans.model.ast;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

class Utilities {

  public static Set<de.ars.daojones.beans.model.Modifier> toModifiers( int flags ) {
    final Set<de.ars.daojones.beans.model.Modifier> result = new HashSet<de.ars.daojones.beans.model.Modifier>();
    if ( ( flags & Modifier.ABSTRACT ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.ABSTRACT );
    if ( ( flags & Modifier.FINAL ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.FINAL );
    if ( ( flags & Modifier.NATIVE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.NATIVE );
    if ( ( flags & Modifier.PRIVATE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PRIVATE );
    if ( ( flags & Modifier.PROTECTED ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PROTECTED );
    if ( ( flags & Modifier.PUBLIC ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PUBLIC );
    if ( ( flags & Modifier.STATIC ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.STATIC );
    if ( ( flags & Modifier.STRICTFP ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.STRICTFP );
    if ( ( flags & Modifier.SYNCHRONIZED ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.SYNCHRONIZED );
    if ( ( flags & Modifier.TRANSIENT ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.TRANSIENT );
    if ( ( flags & Modifier.VOLATILE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.VOLATILE );
    return result;
  }

  public static List<String> toExceptionTypes( ITypeBinding[] exceptionTypes ) {
    final List<String> result = new LinkedList<String>();
    final Comparator<ITypeBinding> inheritationsComparator = new Comparator<ITypeBinding>() {
      @Override
      public int compare( ITypeBinding e1, ITypeBinding e2 ) {
        if ( e1.getQualifiedName().equals( e2.getQualifiedName() ) )
          return 0;
        return e1.isAssignmentCompatible( e2 ) ? 1 : -1;
      }
    };
    final Set<ITypeBinding> sortedExceptions = new TreeSet<ITypeBinding>(
        inheritationsComparator );
    sortedExceptions.addAll( Arrays.asList( exceptionTypes ) );
    for ( ITypeBinding e : sortedExceptions ) {
      result.add( e.getQualifiedName() );
    }
    return result;
  }

  public static String[] toTypeNames( ITypeBinding... types ) {
    final String[] result = new String[types.length];
    for ( int i = 0; i < result.length; i++ ) {
      result[i] = types[i].getQualifiedName();
    }
    return result;
  }

  public static boolean isMethodImplemented( ITypeBinding binding, String name,
      String... parameterTypes ) {
    if ( null != binding ) {
      for ( IMethodBinding method : binding.getDeclaredMethods() ) {
        if ( method.getName().equals( name )
            && Arrays.equals( parameterTypes, toTypeNames( method
                .getParameterTypes() ) ) ) {
          return true;
        }
      }
      if ( !Object.class.getName().equals( binding.getQualifiedName() ) ) {
        if ( isMethodImplemented( binding.getSuperclass(), name, parameterTypes ) ) {
          return true;
        }
        for ( ITypeBinding i : binding.getInterfaces() ) {
          if ( isMethodImplemented( i, name, parameterTypes ) ) {
            return true;
          }
        }
      }
    }
    return false;
  }

}
