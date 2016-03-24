package de.ars.daojones.runtime.ri;

import java.util.Date;

import de.ars.daojones.drivers.notes.annotations.ViewColumn.DocumentMapped;
import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.annotations.Id;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

@DataSource( value = "MixedFormsByName", type = DataSourceType.VIEW )
public class MixedBeanFromViewWithDocumentMappedBean {

  public static final String NAME = "name";

  private final String name;
  private final Date lastModified;
  @Field( "?2" )
  private Date datefield = new Date();

  @DocumentMapped
  @Field
  private MixedBean mixedBean;

  public MixedBeanFromViewWithDocumentMappedBean(
          @Field( id = MixedBeanFromViewWithDocumentMappedBean.NAME, value = "?1" ) final String name,
          @Field( "?3" ) final Date lastModified ) {
    super();
    this.name = name;
    this.lastModified = lastModified;
  }

  @Id
  private Identificator id;

  public String getName() {
    return name;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public Date getDatefield() {
    return datefield;
  }

  public void setDatefield( final Date datefield ) {
    this.datefield = datefield;
  }

  public Identificator getId() {
    return id;
  }

  /**
   * Returns the document-mapped bean behind the view entry.
   * 
   * @return the document-mapped bean
   */
  public MixedBean getMixedBean() {
    return mixedBean;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "MixedBeanFromView [id=" ).append( id ).append( ", name=" ).append( name )
            .append( ", lastModified=" ).append( lastModified ).append( ", datefield=" ).append( datefield )
            .append( "]" );
    return builder.toString();
  }

}
