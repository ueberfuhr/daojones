package de.ars.daojones.runtime.docs.database_access_advanced;

import java.io.IOException;
import java.io.InputStream;

import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.fields.ByteArrayResource;
import de.ars.daojones.runtime.beans.fields.Resource;
import de.ars.daojones.runtime.beans.fields.StringResource;

@SuppressWarnings("unused")
public class Memo {

  @Field(value = "sender", converter = PersonConverter.class)
  private Person sender;

  public void processAttachment(@Field("attachment1") final Resource attachment)
          throws IOException {
    final long size = attachment.getLength();
    final InputStream in = attachment.getInputStream();
    // Process InputStream
  }

  @Field("attachment1")
  public Resource createAttachment1() {
    return new StringResource("This is my sample text.", "SampleText.txt");
  }

  @Field("attachment2")
  public Resource createAttachment2() {
    return new ByteArrayResource(new byte[] { 19, 50, 1, 0, 8 }, "SampleImage.jpg",
            "image/jpg");
  }
}
