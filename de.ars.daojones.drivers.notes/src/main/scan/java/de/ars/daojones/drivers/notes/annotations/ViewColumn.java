package de.ars.daojones.drivers.notes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A composite type that provides types that are used to describe view columns.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public final class ViewColumn {

  private ViewColumn() {
  }

  /**
   * Indicates a bean that is mapped to the document behind a view entry. This
   * allows to easily search a view and get both the view column and the
   * document values.<br/>
   * <br/>
   * <b>Example:</b><br/>
   * 
   * <pre>
   * {@literal @}DataSource(type=DataSourceType.VIEW)
   * <b>public class</b> ViewBean { // used to search within a view
   * 
   *   // view column value
   *   {@literal @}Field
   *   <b>private</b> String title;
   *   // document behind the view entry
   *   
   *   {@literal @}DocumentMapped
   *   <b>private</b> FormBean formBean
   *   
   * }
   * </pre>
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   */
  @Retention( RetentionPolicy.RUNTIME )
  @Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
  public static @interface DocumentMapped {

  }

}
