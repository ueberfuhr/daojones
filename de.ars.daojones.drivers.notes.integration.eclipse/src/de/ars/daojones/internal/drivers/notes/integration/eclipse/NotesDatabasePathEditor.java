package de.ars.daojones.internal.drivers.notes.integration.eclipse;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.NotesDatabasePath.PathType;
import de.ars.daojones.integration.eclipse.DatabasePathEditor;

/**
 * Notes Driver implementation of {@link DatabasePathEditor}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class NotesDatabasePathEditor implements DatabasePathEditor {

  private static final String PATTERN_REPLICA = "[a-f0-9]{16}";
  private static final String PATTERN_FILEPATH = ".*\\.nsf";

  private Text txtHost;
  private Text txtDatabase;

  @Override
  public void createControls( final Composite parent ) {
    parent.setLayout( GridLayoutFactory.fillDefaults().numColumns( 2 ).equalWidth( false ).margins( 5, 15 ).create() );

    // Host (Authority)
    final Label lblHost = new Label( parent, SWT.LEFT );
    lblHost.setText( "Host:" );
    this.txtHost = new Text( parent, SWT.SINGLE | SWT.BORDER );
    this.txtHost.setMessage( "(use local client)" );
    this.txtHost.setLayoutData( GridDataFactory.fillDefaults().grab( true, false ).create() );

    // Database / Replica ID
    final Label lblDatabase = new Label( parent, SWT.LEFT );
    lblDatabase.setText( "Database:" );
    this.txtDatabase = new Text( parent, SWT.SINGLE | SWT.BORDER );
    this.txtDatabase.setLayoutData( GridDataFactory.fillDefaults().grab( true, false ).create() );
    this.txtDatabase.setMessage( "(file path or replica id)" );
  }

  @Override
  public void setEditable( final boolean editable ) {
    this.txtHost.setEditable( editable );
    this.txtHost.setEnabled( editable );
    this.txtDatabase.setEditable( editable );
    this.txtDatabase.setEnabled( editable );
  }

  @Override
  public void setFocus() {
    this.txtHost.setFocus();
  }

  @Override
  public void setModel( final IObservableValue path, final DataBindingContext ctx ) {

    // Binding String to NotesDatabasePath(Bean)
    final IObservableValue notesDatabasePath = new WritableValue( null, NotesDatabasePathBean.class ) {

      private final PropertyChangeListener listener = new PropertyChangeListener() {

        @Override
        public void propertyChange( final PropertyChangeEvent evt ) {
          handlePropertyChanged( evt );
        }
      };

      private void handlePropertyChanged( final PropertyChangeEvent evt ) {
        fireValueChange( Diffs.createValueDiff( evt.getOldValue(), evt.getNewValue() ) );
      }

      @Override
      public void doSetValue( final Object value ) {
        final Object oldValue = doGetValue();
        if ( oldValue instanceof NotesDatabasePathBean ) {
          ( ( NotesDatabasePathBean ) oldValue ).removePropertyChangeListener( this.listener );
        }
        super.doSetValue( value );
        if ( value instanceof NotesDatabasePathBean ) {
          ( ( NotesDatabasePathBean ) value ).addPropertyChangeListener( this.listener );
        }

      }

      @Override
      public synchronized void dispose() {
        doSetValue( null );
        super.dispose();
      }

    };
    ctx.bindValue( notesDatabasePath, path, //
            new UpdateValueStrategy( UpdateValueStrategy.POLICY_UPDATE ).setConverter( new IConverter() {

              @Override
              public Object getToType() {
                return String.class;
              }

              @Override
              public Object getFromType() {
                return NotesDatabasePathBean.class;
              }

              @Override
              public Object convert( final Object fromObject ) {
                return null != fromObject ? fromObject.toString() : null;
              }
            } ), // 
            new UpdateValueStrategy( UpdateValueStrategy.POLICY_UPDATE ).setConverter( new IConverter() {

              @Override
              public Object getToType() {
                return NotesDatabasePathBean.class;
              }

              @Override
              public Object getFromType() {
                return String.class;
              }

              @Override
              public Object convert( final Object fromObject ) {
                try {
                  return null != fromObject ? new NotesDatabasePathBean( NotesDatabasePath.valueOf( fromObject
                          .toString() ) ) : null;
                } catch ( final URISyntaxException e ) {
                  throw new IllegalArgumentException( e );
                }
              }
            } ) //				
    );

    // Host
    final ISWTObservableValue hostTarget = WidgetProperties.text( SWT.Modify ).observe( this.txtHost );
    final IObservableValue hostSource = BeanProperties.value( NotesDatabasePathBean.class,
            NotesDatabasePathBean.PROPERTY_AUTHORITY ).observeDetail( notesDatabasePath );
    ctx.bindValue( hostTarget, hostSource );

    // Database
    final ISWTObservableValue databaseTarget = WidgetProperties.text( SWT.Modify ).observe( this.txtDatabase );
    final IObservableValue databaseSource = PojoProperties.value( NotesDatabasePathBean.class,
            NotesDatabasePathBean.PROPERTY_DATABASE ).observeDetail( notesDatabasePath );
    // Database Validation
    final ControlDecoration dbControlDecoration = new ControlDecoration( this.txtDatabase, SWT.LEFT | SWT.TOP );
    dbControlDecoration
            .setDescriptionText( "Please enter a replica id (16 hex values) or a file path (ending with .nsf)!" );
    final FieldDecoration dbFieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_ERROR );
    dbControlDecoration.setImage( dbFieldDecoration.getImage() );
    // Database Binding
    ctx.bindValue( databaseTarget, databaseSource,
            new UpdateValueStrategy( UpdateValueStrategy.POLICY_UPDATE ).setAfterConvertValidator( new IValidator() {
              @Override
              public IStatus validate( final Object value ) {
                dbControlDecoration.hide();
                if ( null != value ) {
                  final String path = value.toString().toLowerCase().trim();
                  if ( !Pattern.matches( NotesDatabasePathEditor.PATTERN_REPLICA, path )
                          && !Pattern.matches( NotesDatabasePathEditor.PATTERN_FILEPATH, path ) ) {
                    dbControlDecoration.show();
                    return ValidationStatus
                            .error( "Please enter a replica id (16 hex values) or a file path (ending with .nsf)!" );
                  }
                }
                return Status.OK_STATUS;
              }
            } ), null );

    // Type (file path or replica)
    final IObservableValue pathTypeSource = BeanProperties.value( NotesDatabasePathBean.class,
            NotesDatabasePathBean.PROPERTY_TYPE ).observeDetail( notesDatabasePath );
    ctx.bindValue( pathTypeSource, databaseTarget, null, //
            new UpdateValueStrategy( UpdateValueStrategy.POLICY_UPDATE ).setConverter( new IConverter() {

              @Override
              public Object getToType() {
                return PathType.class;
              }

              @Override
              public Object getFromType() {
                return String.class;
              }

              @Override
              public Object convert( final Object fromObject ) {
                PathType result = PathType.FILEPATH;
                // Replica IDs consist of 16 HEX-values
                if ( null != fromObject
                        && Pattern.matches( NotesDatabasePathEditor.PATTERN_REPLICA, fromObject.toString()
                                .toLowerCase().trim() ) ) {
                  result = PathType.REPLICA;
                }
                return result;
              }
            } ) //
    );

  }
}
