package de.ars.daojones.internal.drivers.notes.datahandler;

import java.io.IOException;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.drivers.notes.types.Address;
import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.drivers.notes.types.User;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class PrincipalDataHandler extends InternalAbstractDataHandler<Principal, String> {

  private static final Messages bundle = Messages.create( "datahandler.Principal" );

  @Override
  public Class<? extends Principal> getKeyType() {
    return Principal.class;
  }

  private static String trimToNull( final String value ) {
    return null != value && value.trim().length() == 0 ? null : value;
  }

  @Override
  public Principal convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Principal> fieldContext,
          final String value ) throws NotesException, DataHandlerException {
    if ( null == value ) {
      return null;
    } else {
      final Class<? extends Principal> type = fieldContext.getType();
      final String name$ = value;
      final lotus.domino.Name name = context.getSession().createName( name$ );
      final String address$ = PrincipalDataHandler.trimToNull( name.getAddr821() );
      final Principal result;
      if ( null != address$ ) {
        if ( type.isAssignableFrom( Address.class ) ) {
          final Address address = new Address( address$ );
          // all properties return empty strings, if unset
          address.setPhrase( PrincipalDataHandler.trimToNull( name.getAddr822Phrase() ) );
          address.setComments( PrincipalDataHandler.trimToNull( name.getAddr822Comment1() ),
                  PrincipalDataHandler.trimToNull( name.getAddr822Comment2() ),
                  PrincipalDataHandler.trimToNull( name.getAddr822Comment3() ) );
          result = address;
        } else {
          throw new DataHandlerException( this, PrincipalDataHandler.bundle.get( "error.invalidtype", Address.class,
                  type ) );
        }
      } else {
        if ( type.isAssignableFrom( User.class ) ) {
          final User user = new User( name.getCommon() );
          if ( name.isHierarchical() ) {
            user.setOrganizationalUnits( PrincipalDataHandler.trimToNull( name.getCountry() ),
                    PrincipalDataHandler.trimToNull( name.getOrganization() ),
                    PrincipalDataHandler.trimToNull( name.getOrgUnit1() ),
                    PrincipalDataHandler.trimToNull( name.getOrgUnit2() ),
                    PrincipalDataHandler.trimToNull( name.getOrgUnit3() ),
                    PrincipalDataHandler.trimToNull( name.getOrgUnit4() ) );
          }
          result = user;
        } else {
          throw new DataHandlerException( this, PrincipalDataHandler.bundle.get( "error.invalidtype", User.class, type ) );
        }
      }
      return result;
    }
  }

  @Override
  protected Item writeDocumentItem( final DataHandlerContext<Document> context,
          final FieldContext<Principal> fieldContext, final String value ) throws NotesException, IOException {
    final Item item = super.writeDocumentItem( context, fieldContext, value );
    if ( null != item ) {
      item.setNames( true );
      final String fieldType = Properties.getFieldType( fieldContext.getMetadata() );
      if ( NotesDriverConfiguration.MODEL_PROPERTY_READERS.equals( fieldType ) ) {
        item.setReaders( true );
      } else if ( NotesDriverConfiguration.MODEL_PROPERTY_AUTHORS.equals( fieldType ) ) {
        item.setAuthors( true );
      }
    }
    return item;
  }

  @Override
  public String convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Principal> fieldContext,
          final Principal value ) throws NotesException, DataHandlerException {
    if ( null != value ) {
      final StringBuilder sb = new StringBuilder();
      if ( value.isSimple() ) {
        sb.append( value.getValue() );
      } else {
        switch ( value.getType() ) {
        case ADDRESS:
          // Build RFC 822 string representation
          final Address address = ( Address ) value;
          final String phrase = address.getPhrase();
          if ( null != phrase ) {
            sb.append( phrase ).append( ' ' );
          }
          sb.append( '<' ).append( address.getAddress() ).append( '>' );
          final String[] comments = address.getComments();
          for ( final String comment : comments ) {
            sb.append( " (" ).append( comment ).append( " )" );
          }
          break;
        case USER:
          final User user = ( User ) value;
          sb.append( "CN=" ).append( user.getName() );
          for ( final String ou : user.getOrganizationalUnits() ) {
            sb.append( "/OU=" ).append( ou );
          }
          final String organization = user.getOrganization();
          if ( null != organization ) {
            sb.append( "/O=" ).append( organization );
          }
          final String country = user.getCountry();
          if ( null != country ) {
            sb.append( "/C=" ).append( country );
          }
          break;
        default:
          throw new UnsupportedOperationException();
        }
      }
      return sb.toString();
    } else {
      return null;
    }
  }
  // reader/author/name field setzen

}
