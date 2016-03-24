package de.ars.daojones.runtime.docs.concepts;

import de.ars.daojones.runtime.beans.annotations.Field;

public class Memo {

  @Field
  private String body;
  @Field("SendTo")
  private String receiver;
  @Field("From")
  private String sender;

  public String getBody() {
    return body;
  }

  public void setBody(final String body) {
    this.body = body;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(final String receiver) {
    this.receiver = receiver;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(final String sender) {
    this.sender = sender;
  }

}
