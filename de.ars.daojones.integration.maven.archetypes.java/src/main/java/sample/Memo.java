package sample;

import de.ars.daojones.runtime.beans.annotations.DataSource;
import de.ars.daojones.runtime.beans.annotations.Field;

@DataSource( "Memo" )
public class Memo {

  public static final String SUBJECT = "subject";

  @Field( id = Memo.SUBJECT )
  private String subject;

  @Field
  private String body;

  public String getSubject() {
    return subject;
  }

  public void setSubject( final String subject ) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody( final String body ) {
    this.body = body;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "Memo [subject=" ).append( subject ).append( ", body=" ).append( body ).append( "]" );
    return builder.toString();
  }

}
