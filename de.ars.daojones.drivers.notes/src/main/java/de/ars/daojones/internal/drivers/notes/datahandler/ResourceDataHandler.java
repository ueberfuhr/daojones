package de.ars.daojones.internal.drivers.notes.datahandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.annotations.MIMEEntityType;
import de.ars.daojones.internal.drivers.notes.NotesStream;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.FileResource;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.beans.fields.Resource;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;

public class ResourceDataHandler extends InternalAbstractDataHandler<Resource, Object> {

  private static final Messages bundle = Messages.create( "datahandler.Resource" );

  @Override
  public Class<? extends Resource> getKeyType() {
    return Resource.class;
  }

  private static void copyStream( final InputStream input, final OutputStream output ) throws IOException {
    final byte[] buffer = new byte[1024]; // Adjust if you want
    int bytesRead;
    while ( ( bytesRead = input.read( buffer ) ) != -1 ) {
      output.write( buffer, 0, bytesRead );
    }
  }

  @Override
  protected void appendToRichText( final DataHandlerContext<Document> context,
          final FieldContext<Resource> fieldContext, final Object value, final RichTextItem item )
          throws NotesException, IOException {
    final Resource resource = ( Resource ) value;
    final String resourceName = resource.getName();
    final String attachmentName = null != resourceName && resourceName.length() > 0 ? resourceName : "attachment";
    if ( resource instanceof FileResource ) {
      final File file = ( ( FileResource ) resource ).getFile();
      if ( null != file ) {
        item.embedObject( EmbeddedObject.EMBED_ATTACHMENT, "", file.getAbsolutePath(), attachmentName );
      }
    } else {
      // Write to temporary file -> file must have name of original resource
      final File tmp = File.createTempFile( "attachment", "" );
      try {
        if ( !tmp.delete() ) {
          throw new IOException( ResourceDataHandler.bundle.get( "error.file.delete", tmp.getAbsolutePath() ) );
        }
      } catch ( final SecurityException e ) {
        throw new IOException( ResourceDataHandler.bundle.get( "error.file.delete", tmp.getAbsolutePath() ), e );
      }
      try {
        if ( !tmp.mkdir() ) {
          throw new IOException( ResourceDataHandler.bundle.get( "error.file.mkdir", tmp.getAbsolutePath() ) );
        }
      } catch ( final SecurityException e ) {
        throw new IOException( ResourceDataHandler.bundle.get( "error.file.mkdir", tmp.getAbsolutePath() ), e );
      }
      try {
        final File tmpFile = new File( tmp, attachmentName );
        try {
          final FileOutputStream fos = new FileOutputStream( tmpFile );
          try {
            final InputStream in = resource.getInputStream();
            if ( null != in ) {
              try {
                ResourceDataHandler.copyStream( in, fos );
              } finally {
                in.close();
              }
            }
          } finally {
            fos.close();
          }
          item.embedObject( EmbeddedObject.EMBED_ATTACHMENT, "", tmpFile.getAbsolutePath(), tmpFile.getName() );
        } finally {
          // Delete temporary file
          ResourceDataHandler.tryToDelete( tmpFile );
        }
      } finally {
        // Delete temporary directory
        ResourceDataHandler.tryToDelete( tmp );
      }
    }
  }

  private static void tryToDelete( final File file ) {
    if ( file.exists() && !file.delete() ) {
      ResourceDataHandler.bundle.log( Level.WARNING, "error.file.delete", file.getAbsolutePath() );
    }
  }

  @Override
  protected void appendToSimpleItem( final Object value, final UpdatePolicy updatePolicy, final Item item )
          throws NotesException {
    // no attachments to simple items!
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean isRichText( final DataHandlerContext<Document> context,
          final FieldContext<Resource> fieldContext ) {
    return !isMIMEEntity( context, fieldContext ); // use rich text by default
  }

  /**
   * Decides whether to create a MIME entity or an embedded object.
   *
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @return <tt>true</tt> if the resource is stored as MIME entity
   */
  protected boolean isMIMEEntity( final DataHandlerContext<Document> context,
          final FieldContext<Resource> fieldContext ) {
    return NotesDriverConfiguration.MODEL_PROPERTY_MIME_ENTITY
            .equals( Properties.getFieldType( fieldContext.getMetadata() ) );
  }

  /**
   * Returns the MIME entity type. If no type is specified,
   * {@link MIMEEntityType#ATTACHMENT} is returned as default value.
   *
   * @param context
   *          the context
   * @param fieldContext
   *          the field context
   * @return the MIME entity type
   * @throws DataHandlerException
   *           if the configured type is not a literal of the enumeration
   */
  protected MIMEEntityType getMIMEEntityType( final DataHandlerContext<Document> context,
          final FieldContext<Resource> fieldContext ) throws DataHandlerException {
    final Property prop = Properties.getProperty( fieldContext.getMetadata(),
            NotesDriverConfiguration.MODEL_PROPERTY_MIME_ENTITY_TYPE );
    MIMEEntityType result = MIMEEntityType.ATTACHMENT;
    if ( null != prop ) {
      final String value = prop.getValue();
      if ( null != value && value.length() > 0 ) {
        try {
          result = MIMEEntityType.valueOf( value.toUpperCase() );
        } catch ( final IllegalArgumentException e ) {
          throw new DataHandlerException( this,
                  ResourceDataHandler.bundle.get( "mime.entity.type.invalid", value.toUpperCase() ) );
        }
      }
    }
    return result;
  }

  @Override
  public Resource readDocument( final DataHandlerContext<Document> context, final FieldContext<Resource> fieldContext )
          throws NotesException, DataHandlerException {
    final Document document = context.getSource();
    final String field = fieldContext.getName();
    // Try to read MIME entity
    final MIMEEntity entity = document.getMIMEEntity( field );
    if ( null != entity ) {
      return new MIMEEntityResource( entity, document );
    } else {
      // Find attachment
      final EmbeddedObject attachment = document.getAttachment( field );
      if ( null != attachment ) {
        return new EmbeddedObjectResource( attachment );
      } else {
        // Find content of Rich Text
        final Item item = document.getFirstItem( field );
        if ( item instanceof RichTextItem ) {
          final RichTextItem rt = ( RichTextItem ) item;
          // try to get embedded object
          @SuppressWarnings( "rawtypes" )
          final Vector v = rt.getEmbeddedObjects();
          if ( v != null && !v.isEmpty() ) {
            final EmbeddedObject notesObj = ( EmbeddedObject ) v.firstElement();
            if ( null != notesObj ) {
              return new EmbeddedObjectResource( notesObj );
            }
          }
        }
      }
    }
    return null;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void writeDocument( final DataHandlerContext<Document> context, final FieldContext<Resource> fieldContext,
          final Resource value ) throws NotesException, DataHandlerException {
    if ( value instanceof InternalResource ) {
      throw new DataHandlerException( this, ResourceDataHandler.bundle.get( "error.reusedresource" ) );
    }
    final Document doc = context.getSource();
    final UpdatePolicy updatePolicy = fieldContext.getUpdatePolicy();
    final String fieldName = fieldContext.getName();
    final Item item = doc.getFirstItem( fieldName );
    if ( isMIMEEntity( context, fieldContext ) ) {
      final MIMEEntityWriter mew;
      switch ( getMIMEEntityType( context, fieldContext ) ) {
      case INLINE:
        mew = new MIMEEntityInlineWriter( context, fieldContext, value );
        break;
      default:
        mew = new MIMEEntityAttachWriter( context, fieldContext, value );
        break;
      }
      try {
        // second, insert EmbeddedObject
        final Session session = context.getSession();
        synchronized ( session ) {
          final boolean isConvertMime = session.isConvertMIME();
          final boolean shouldConvertMime = mew.isSessionConvertMIME();
          if ( isConvertMime != shouldConvertMime ) {
            session.setConvertMIME( shouldConvertMime );
          }
          try {
            MIMEEntity root = doc.getMIMEEntity( fieldName );
            // delete field, if
            if ( null != root && UpdatePolicy.REPLACE == updatePolicy ) {
              doc.removeItem( fieldName );
              root = null;
            }
            try {
              if ( null == root ) {
                root = doc.createMIMEEntity( fieldName );
                mew.initialize( root );
              }
              final MIMEEntity childEntity = mew.createMIMEEntityForValue( root );
              final long len = value.getLength();
              if ( len >= 0 ) {
                childEntity.createHeader( MIMEEntityResource.CONTENT_LENGTH_HEADER )
                        .setHeaderVal( Long.toString( len ) );
              }
              final InputStream in = value.getInputStream();
              try {
                final Stream stream = session.createStream();
                try {
                  stream.setContents( in );
                  final String contentType = value.getContentType();
                  childEntity.setContentFromBytes( stream, null != contentType ? contentType : "application/unknown",
                          MIMEEntity.ENC_EXTENSION );
                } finally {
                  stream.close();
                }
              } finally {
                in.close();
              }
            } finally {
              doc.closeMIMEEntities( true );
            }
          } finally {
            if ( isConvertMime != shouldConvertMime ) {
              session.setConvertMIME( isConvertMime );
            }
          }
        }
      } catch ( final IOException e ) {
        throw new DataHandlerException( this, e );
      }
    } else {
      if ( null != item && Item.RICHTEXT == item.getType() && UpdatePolicy.REPLACE == updatePolicy ) {
        final RichTextItem rt = ( RichTextItem ) item;
        // first, remove all attachments
        final Vector<EmbeddedObject> embeddedObjects = rt.getEmbeddedObjects();
        for ( final EmbeddedObject obj : embeddedObjects ) {
          obj.remove();
          obj.recycle();
        }
        doc.removeItem( fieldName );
      }
      super.writeDocument( context, fieldContext, value );
    }
  }

  /*
   * INHERITATION HIERARCHY FOR BETTER HANDLING COMPLEXITY
   * - BEGIN -
   */

  private abstract class MIMEEntityWriter {

    private final DataHandlerContext<Document> context;
    private final FieldContext<Resource> fieldContext;
    private final Resource value;

    protected MIMEEntityWriter( final DataHandlerContext<Document> context, final FieldContext<Resource> fieldContext,
            final Resource value ) {
      super();
      this.context = context;
      this.fieldContext = fieldContext;
      this.value = value;
    }

    protected DataHandlerContext<Document> getContext() {
      return context;
    }

    protected FieldContext<Resource> getFieldContext() {
      return fieldContext;
    }

    protected Resource getValue() {
      return value;
    }

    public abstract boolean isSessionConvertMIME();

    public abstract void initialize( MIMEEntity field ) throws NotesException;

    public abstract MIMEEntity createMIMEEntityForValue( MIMEEntity field ) throws NotesException, IOException;
  }

  private class MIMEEntityAttachWriter extends MIMEEntityWriter {

    public MIMEEntityAttachWriter( final DataHandlerContext<Document> context,
            final FieldContext<Resource> fieldContext, final Resource value ) {
      super( context, fieldContext, value );
    }

    @Override
    public boolean isSessionConvertMIME() {
      return false;
    }

    @Override
    public void initialize( final MIMEEntity field ) throws NotesException {
      final Stream rootStream = getContext().getSession().createStream();
      try {
        rootStream.writeText( "This is a multipart message in MIME format." );
        field.setContentFromText( rootStream, "multipart/related", MIMEEntity.ENC_NONE );
      } finally {
        rootStream.close();
      }
    }

    @Override
    public MIMEEntity createMIMEEntityForValue( final MIMEEntity field ) throws NotesException, IOException {
      final UpdatePolicy updatePolicy = getFieldContext().getUpdatePolicy();
      final MIMEEntity childEntity;
      final MIMEEntity firstChildEntity = field.getFirstChildEntity();
      if ( updatePolicy == UpdatePolicy.INSERT && null != firstChildEntity ) {
        childEntity = field.createChildEntity( firstChildEntity );
      } else {
        childEntity = field.createChildEntity();
      }
      childEntity.createHeader( MIMEEntityResource.CONTENT_DISPOSITION_HEADER )
              .setHeaderVal( "attachment;filename=\"" + getValue().getName() + "\"" );
      return childEntity;
    }

  }

  private class MIMEEntityInlineWriter extends MIMEEntityWriter {

    public MIMEEntityInlineWriter( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
            final FieldContext<Resource> fieldContext, final Resource value ) {
      super( context, fieldContext, value );
      final UpdatePolicy updatePolicy = getFieldContext().getUpdatePolicy();
      if ( UpdatePolicy.INSERT == updatePolicy ) {
        throw new UnsupportedOperationException(
                ResourceDataHandler.bundle.get( "inline.entity.policy.invalid", updatePolicy ) );
      }
    }

    @Override
    public boolean isSessionConvertMIME() {
      return true;
    }

    @Override
    public void initialize( final MIMEEntity field ) throws NotesException {
      // nothing to do
    }

    @Override
    public MIMEEntity createMIMEEntityForValue( final MIMEEntity field ) throws NotesException {
      // nothing to do
      return field;
    }

  }

  /*
   * - BEGIN -
   * INHERITATION HIERARCHY FOR BETTER HANDLING COMPLEXITY
   */

  @Override
  public Resource convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Resource> fieldContext,
          final Object value ) throws NotesException, DataHandlerException {
    return ( Resource ) value;
  }

  @Override
  public Object convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Resource> fieldContext,
          final Resource value ) throws NotesException, DataHandlerException {
    return value;
  }

  /* *********************************
   *    I N N E R   C L A S S E S    *
   ********************************* */

  /**
   * A marker interface for the following inner types.
   */
  private interface InternalResource extends Resource {
  }

  private static class MIMEEntityResource implements InternalResource {

    private static final Logger logger = Logger.getLogger( MIMEEntityResource.class.getName() );
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private MIMEEntity entity;
    private final Document document;
    private NotesStream stream;
    private Long length;

    public MIMEEntityResource( final MIMEEntity root, final Document document ) {
      super();
      this.document = document;
      entity = MIMEEntityResource.getAttachment( root );
    }

    @Override
    public String getContentType() {
      return MIMEEntityResource.getHeaderValue( entity, MIMEEntityResource.CONTENT_TYPE_HEADER );
    }

    protected Document getDocument() throws NotesException, DataAccessException {
      return document;
    }

    private static MIMEEntity getAttachment( final MIMEEntity root ) {
      try {
        final String contentType = MIMEEntityResource.getHeaderValue( root, MIMEEntityResource.CONTENT_TYPE_HEADER );
        if ( null == contentType || contentType.startsWith( "multipart/" ) || contentType.equals( "text/html" ) ) {
          MIMEEntity child = root.getFirstChildEntity();
          MIMEEntity htmlChild = null;
          while ( null != child ) {
            final MIMEEntity result = MIMEEntityResource.getAttachment( child );
            if ( null != result ) {
              return result;
            }
            final String childContentType = MIMEEntityResource.getHeaderValue( child,
                    MIMEEntityResource.CONTENT_TYPE_HEADER );
            if ( "text/html".equals( childContentType ) ) {
              htmlChild = child;
            } else {
              return child;
            }
            child = child.getNextSibling();
          }
          return htmlChild;
        } else {
          return root;
        }
      } catch ( final NotesException e ) {
        MIMEEntityResource.logger.log( Level.WARNING, "The MIMEEntity could not be analyzed!", e );
        return null;
      }
    }

    @SuppressWarnings( "unchecked" )
    private static String getHeaderValue( final MIMEEntity entity, final String headerName ) {
      try {
        for ( final MIMEHeader header : ( Collection<MIMEHeader> ) entity.getHeaderObjects() ) {
          if ( header.getHeaderName().equals( headerName ) ) {
            return header.getHeaderVal();
          }
        }
      } catch ( final NotesException e ) {
        MIMEEntityResource.logger.log( Level.WARNING, "The headers of a MIMEEntity could not be read!", e );
      }
      return null;
    }

    private NotesStream getStream() throws NotesException, IOException, DataAccessException {
      if ( null == stream && null != entity ) {
        if ( entity.getEncoding() != MIMEEntity.ENC_NONE ) {
          entity.decodeContent();
        }
        final Stream localStream = getDocument().getParentDatabase().getParent().createStream();
        entity.getContentAsBytes( localStream );
        localStream.setPosition( 0 );
        stream = new NotesStream( localStream ) {
          @Override
          public void close() throws IOException {
            super.close();
            stream = null;
          }
        };
        stream.mark( stream.available() );
      }
      return stream;
    }

    @Override
    public InputStream getInputStream() {
      try {
        return getStream();
      } catch ( final Throwable e ) {
        MIMEEntityResource.logger.log( Level.SEVERE, "The InputStream of the MIMEEntity could not be used!", e );
      }
      return null;
    }

    @Override
    public long getLength() {
      if ( null == length ) {
        try {
          final String contentLengthHeader = MIMEEntityResource.getHeaderValue( entity,
                  MIMEEntityResource.CONTENT_LENGTH_HEADER );
          try {
            if ( null != contentLengthHeader ) {
              length = Long.valueOf( contentLengthHeader );
            }
          } catch ( final NumberFormatException e ) {
            // Invalid content length header
          }
          if ( null == length ) {
            // Callback: Open Stream
            final NotesStream stream = getStream();
            length = ( long ) stream.available();
          }
        } catch ( final Throwable e ) {
          MIMEEntityResource.logger.log( Level.SEVERE, "The length of the stream could not be read!", e );
        }
      }
      return null != length ? length : 0;
    }

    @Override
    public void destroy() {
      if ( null != entity ) {
        try {
          try {
            entity.recycle();
          } finally {
            entity = null;
          }
        } catch ( final NotesException e ) {
          MIMEEntityResource.logger.log( Level.FINER, "Could not destroy MIME entity!", e );
        }
      }
    }

    @Override
    protected void finalize() throws Throwable {
      destroy();
      super.finalize();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public String getName() throws IOException {
      try {
        final StringBuffer sb = new StringBuffer();
        sb.append( "MIME Entity" );
        for ( final MIMEHeader header : ( Iterable<MIMEHeader> ) entity.getHeaderObjects() ) {
          sb.append( "\n" ).append( header.getHeaderName() ).append( "=" ).append( header.getHeaderVal() );
        }
        return sb.toString();
      } catch ( final NotesException e ) {
        throw new IOException( e );
      }
    }

  }

  // -------------------------------------------------------------------

  private static class EmbeddedObjectResource implements InternalResource {

    private static final Logger logger = Logger.getLogger( EmbeddedObjectResource.class.getName() );
    private EmbeddedObject embeddedObject;

    public EmbeddedObjectResource( final EmbeddedObject embeddedObject ) {
      this.embeddedObject = embeddedObject;
    }

    public int getContentLength() {
      try {
        return embeddedObject.getFileSize();
      } catch ( final NotesException e ) {
        e.printStackTrace();
        return 0;
      }
    }

    @Override
    public InputStream getInputStream() {
      try {
        return embeddedObject.getInputStream();
      } catch ( final NotesException e ) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    public long getLength() {
      return getContentLength();
    }

    @Override
    public void destroy() {
      if ( null != embeddedObject ) {
        try {
          try {
            embeddedObject.recycle();
          } finally {
            embeddedObject = null;
          }
        } catch ( final NotesException e ) {
          EmbeddedObjectResource.logger.log( Level.WARNING, "Unable to recycle embedded object!", e );
        }
      }
    }

    @Override
    protected void finalize() throws Throwable {
      destroy();
      super.finalize();
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public String getName() throws IOException {
      try {
        return embeddedObject.getName();
      } catch ( final NotesException e ) {
        throw new IOException( e );
      }
    }

  }

}
