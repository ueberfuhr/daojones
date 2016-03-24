package de.ars.daojones.runtime.ri;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.ViewIdentificator;
import de.ars.daojones.drivers.notes.annotations.Authors;
import de.ars.daojones.drivers.notes.annotations.MIMEEntity;
import de.ars.daojones.drivers.notes.annotations.Readers;
import de.ars.daojones.drivers.notes.annotations.RichText;
import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.annotations.FieldRef;
import de.ars.daojones.runtime.beans.annotations.Id;
import de.ars.daojones.runtime.beans.fields.FileResource;
import de.ars.daojones.runtime.beans.fields.Resource;
import de.ars.daojones.runtime.beans.fields.URLResource;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;

@DataSource( "MixedForm" )
public class MixedBean {

  @Id
  private Identificator id;

  private final String name;

  public static final String NAME = "name";

  public MixedBean( @Field( id = MixedBean.NAME, value = "name" ) final String name ) {
    super();
    this.name = name;
  }

  public Identificator getId() {
    return id;
  }

  @Field( "stringfield" )
  private String stringField;

  @Field( "datefield" )
  private Date dateField = new Date();

  @Field( "namesfield" )
  private Principal[] names;

  @Field( "intarrayfield" )
  private int[] intArray = new int[] { 1, 2, 3, 4 };

  @Readers
  @Field( "readersfield" )
  private Principal[] readers;

  @Authors
  @Field( "authorsfield" )
  private Principal[] authors;

  public static final String GENERATED = "generated";

  @Field( MixedBean.GENERATED )
  private boolean generated = true;

  @FieldRef( MixedBean.NAME )
  public String getName() {
    return name;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField( final String stringField ) {
    this.stringField = stringField;
  }

  public Date getDatefield() {
    return dateField;
  }

  public void setDatefield( final Date datefield ) {
    dateField = datefield;
  }

  public Principal[] getNames() {
    return names;
  }

  public void setNames( final Principal[] names ) {
    this.names = names;
  }

  public Principal[] getReaders() {
    return readers;
  }

  public void setReaders( final Principal[] readers ) {
    this.readers = readers;
  }

  public Principal[] getAuthors() {
    return authors;
  }

  public void setAuthors( final Principal[] authors ) {
    this.authors = authors;
  }

  @Field( id = "logo", value = "logo" )
  public Resource createLogo() throws IOException {
    try {
      final URL resource = MixedBean.class.getResource( "/logo.gif" );
      final File file = new File( resource.toURI() );
      return new FileResource( file, "image/gif" );
    } catch ( final URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  @Field( id = "logo2", value = "logo2" )
  public Resource createLogo2() throws IOException {
    try {
      final URL resource = MixedBean.class.getResource( "/logo.gif" );
      final File file = new File( resource.toURI() );
      return new URLResource( file.toURI().toURL(), file.getName(), "image/gif", file.length() );
    } catch ( final URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  @MIMEEntity
  @Field( id = "logo3", value = "logo3", updatePolicy = UpdatePolicy.INSERT )
  public Resource createLogo3() throws IOException {
    try {
      final URL resource = MixedBean.class.getResource( "/logo.gif" );
      final File file = new File( resource.toURI() );
      return new FileResource( file, "image/gif" );
    } catch ( final URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  @MIMEEntity
  @Field( value = "logo3", updatePolicy = UpdatePolicy.APPEND )
  public Resource createLogo4() throws IOException {
    try {
      final URL resource = MixedBean.class.getResource( "/Example.pdf" );
      final File file = new File( resource.toURI() );
      return new FileResource( file, "application/pdf" );
    } catch ( final URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  public void processLogo( @FieldRef( "logo" ) final Resource resource ) throws IOException {
    // Do anything with the logo
  }

  /**
   * Returns the generated flag.
   * 
   * @return the generated flag
   */
  public boolean isGenerated() {
    return generated;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "MixedBean [name=" ).append( name ).append( ", stringField=" ).append( stringField )
            .append( ", names=" ).append( Arrays.toString( names ) ).append( ", readers=" )
            .append( Arrays.toString( readers ) ).append( ", authors=" ).append( Arrays.toString( authors ) )
            .append( ", generated=" ).append( generated ).append( "]" );
    return builder.toString();
  }

  @Field( value = "comments", id = "comment1", updatePolicy = UpdatePolicy.INSERT )
  public String getComment1() {
    return new StringBuffer().append( System.getProperty( "user.name" ) ).append( " (" ).append( new Date() )
            .append( ")" ).toString();
  }

  @Field( value = "comments", id = "comment2", updatePolicy = UpdatePolicy.APPEND )
  public String getComment2() {
    return "Executed Reference Implementation - works fine!";
  }

  @RichText
  @Field( value = "commentsRT", id = "comment1RT", updatePolicy = UpdatePolicy.INSERT )
  public String getComment1RT() {
    return new StringBuffer().append( "{\\b " ).append( getComment1() ).append( "}\n" ).append( getComment2() )
            .toString();
  }

  @RichText
  @Field( value = "commentsRT", id = "comment2RT", updatePolicy = UpdatePolicy.APPEND )
  public Identificator getComment2RT() {
    return new ViewIdentificator( "MixedForms", new DatabaseIdentificator( "C1257C0100400C32", "" ) );
  }

}
