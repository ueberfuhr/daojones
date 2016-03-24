package de.ars.daojones.runtime.docs;

import de.ars.daojones.runtime.beans.annotations.Field;

public class Memo {

  // ----------------------------------------------------------------------

  @Field
  private String body;

  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body
   *          the body to set
   */
  public void setBody(final String body) {
    this.body = body;
  }

  // ----------------------------------------------------------------------

  @Field("sendTo")
  // eigene Konverter im ApplicationContext f�r alle Type gemeinsam
  // eigene Selektoren per Annotation? (analog JSR 303 Validatoren)
  private String receiver;

  /**
   * @return the receiver
   */
  public String getReceiver() {
    return receiver;
  }

  /**
   * @param receiver
   *          the receiver to set
   */
  public void setReceiver(final String receiver) {
    this.receiver = receiver;
  }

  // ----------------------------------------------------------------------

  // @Inject f�r ConnectionProvider (wird nicht serialisiert, sondern danach injiziert)

  private String sender;

  /**
   * @return the sender
   */
  public String getSender() {
    return sender;
  }

  /**
   * @param sender
   *          the sender to set
   */
  public void setSender(@Field("sentFrom") final String sender) {
    this.sender = sender;
  }

  /**
   * @param sender
   *          the sender to set
   */
  public void setSender(@Field("sentFrom") final String sender, final boolean x) {
    this.sender = sender;
  }

  // ----------------------------------------------------------------------

}
