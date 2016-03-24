package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.beans.fields.DefaultConverter;
import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.context.BeanModelHelper;

/**
 * This annotation binds a Java element to a field within the database. You can
 * annotate the following elements of a class:<br/>
 * <br/>
 * <ul>
 * <li>Fields to get field values synchronized while reading from and writing
 * into the database. Annotated fields must not be <tt>static</tt> or
 * <tt>final</tt>.</li>
 * <li>Constructor or method parameters to get field values injected while
 * reading the bean from the database. Only one constructor can be annotated.</li>
 * <li>Methods to get the invocation result written to the database. Annotated
 * methods must not be <tt>static</tt> or <tt>abstract</tt> and must not have
 * parameters annotated with this annotation.</li>
 * </ul>
 * <br/>
 * <b>Please note:</b><br/>
 * In comparison to JPA, properties are not automatically persisted until they
 * are marked as transient. This decision was made to support inheritance of
 * classes and interfaces without any unwanted injection of inherited property
 * values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Field {

  /**
   * Returns the name of the field within the database that is assigned to the
   * annotated element. If not specified, the name of the field within the
   * database is
   * <ul>
   * <li>(for fields) the name of the field,</li>
   * <li>(for constructor or method parameters) the name of the constructor (the
   * simple class name) or method and the parameter index <i>(which is not
   * unique in case of overloaded methods)</i>,</li>
   * <li>(for method results) the name of the method and the suffix &quot;
   * <tt>-result</tt>&quot; <i>(which is not unique in case of overloaded
   * methods)</i></li>
   * </ul>
   * 
   * @return the name of the field within the database
   */
  public String value() default "";

  /**
   * Returns the id of the field. This can be used for referencing to the field
   * e.g. by a query parameter or the {@link FieldRef} annotation. If not
   * specified, this field binding can only be referenced by using the field's
   * name. See
   * {@link BeanModelHelper#resolveFieldName(de.ars.daojones.runtime.configuration.beans.Bean, String)}
   * to get more details about field name resolving.
   * 
   * @return the id of the field
   * @see FieldRef
   * @see BeanModelHelper#resolveFieldName(de.ars.daojones.runtime.configuration.beans.Bean,
   *      String)
   */
  public String id() default "";

  /**
   * Specifies a converter implementation. This converts the database value to
   * the Java value and vice-versa.
   * 
   * @return the converter implementation
   */
  public Class<? extends Converter> converter() default DefaultConverter.class;

  /**
   * The kind of field update that has to be done when storing a bean.
   * <ul>
   * <li><i>REPLACE (default): </i>The field has to be fully replaced by the
   * value that is given by the bean.</li>
   * <li><i>INSERT: </i>The value, that is given by the bean, is inserted into
   * the field at the top position.</li>
   * <li><i>APPEND: </i>The value, that is given by the bean, is inserted into
   * the field at the bottom position.</li>
   * </ul>
   * <b>Please note:</b> To avoid inserting or appending field values multiple
   * times, the framework does not inject bean member values with a policy of
   * <i>INSERT</i> or <i>APPEND</i>. After storing a bean, fields with this
   * policy are cleaned, if possible.
   * 
   * @return the update policy
   */
  public UpdatePolicy updatePolicy() default UpdatePolicy.REPLACE;

}
