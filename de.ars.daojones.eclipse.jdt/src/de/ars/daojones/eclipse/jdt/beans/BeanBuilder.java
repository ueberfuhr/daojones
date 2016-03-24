package de.ars.daojones.eclipse.jdt.beans;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.DEBUG;
import static de.ars.daojones.eclipse.jdt.LoggerConstants.ERROR;
import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.isDaoJonesBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.ars.daojones.Inheritations;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.ast.ASTBeanImpl;
import de.ars.daojones.eclipse.jdt.Activator;
import de.ars.daojones.eclipse.jdt.Artifacts;
import de.ars.daojones.eclipse.jdt.InheritationsFileFinder;
import de.ars.daojones.eclipse.jdt.ProjectNature;
import de.ars.daojones.eclipse.jdt.internal.util.StubUtility3;
import de.ars.daojones.runtime.Naming;
import de.ars.daojones.sdk.codegen.generators.Generator;
import de.ars.daojones.sdk.codegen.generators.GeneratorException;
import de.ars.daojones.sdk.codegen.generators.GeneratorFactory;
import de.ars.equinox.utilities.rcp.ResourceUtil;

/**
 * A builder for DaoJones beans.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class BeanBuilder extends IncrementalProjectBuilder implements
    IResourceDeltaVisitor, IResourceProxyVisitor, IResourceVisitor {

  /**
   * The ID of this builder.
   */
  public static final String ID = Activator.PLUGIN_ID + ".BeanBuilder";

  /**
   * The {@link Logger} for the BeanBuilder.
   */
  public static final Logger logger = Logger.getLogger( BeanBuilder.class
      .getName() );

  private IJavaProject javaProject;
  private IFolder destinationFolder;
  private Inheritations inheritations;
  private IProgressMonitor monitor;
  private boolean inheritationsChanged = false;

  /**
   * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
   *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  protected IProject[] build( int kind, Map args, IProgressMonitor monitor )
      throws CoreException {
    javaProject = ProjectNature.getJavaProject( getProject() );
    if ( null == getJavaProject() )
      return null;
    this.monitor = monitor;
    if ( null != monitor )
      monitor.beginTask( "Building DaoJones beans... (" + kind + ", " + args
          + ")", 1 );
    final long time = System.currentTimeMillis();
    logger.log( DEBUG, "Start building beans... (ID=" + time + ")" );
    try {
      final IResourceDelta delta = getDelta( getProject() );
      inheritations = new Inheritations();
      final IFile inhFile = new InheritationsFileFinder(
          getDestinationFolder(), getJavaProject() ).getInheritationsFile();
      if ( null == delta || INCREMENTAL_BUILD != kind && AUTO_BUILD != kind ) {
        inheritationsChanged = true;
        // Recreate source folder
        recreateDestinationFolder();
        getProject().accept( this, IContainer.INCLUDE_HIDDEN );
      } else {
        inheritationsChanged = false;
        if ( null != inhFile && inhFile.exists() ) {
          final InputStream in = inhFile.getContents();
          try {
            try {
              inheritations.load( in );
            } finally {
              in.close();
            }
          } catch ( IOException e ) {
            logger.log( ERROR, "Unable to load inheritations file!", e );
          }
        }
        delta.accept( this, false );
      }
      if ( null != inhFile && inheritationsChanged ) {
        try {
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          try {
            inheritations.save( out );
            final InputStream in = new ByteArrayInputStream( out.toByteArray() );
            try {
              if ( inhFile.exists() ) {
                inhFile.setContents( in, true, false, monitor );
              } else {
                inhFile.create( in, true, monitor );
              }
              inhFile.setDerived( true );
            } finally {
              in.close();
            }
          } finally {
            out.close();
          }
        } catch ( IOException e ) {
          logger.log( ERROR, "Unable to save inheritations file!", e );
        }
      }
      inheritations = null;
      return null; // new IProject[]{getProject()};
    } finally {
      logger.log( DEBUG, "Finished building beans... (ID=" + time + ", "
          + ( System.currentTimeMillis() - time ) + " ms)\n" );
      if ( null != getDestinationFolder() )
        getDestinationFolder().refreshLocal( IResource.DEPTH_INFINITE,
            this.monitor );
      if ( null != monitor )
        monitor.worked( 1 );
    }
  }

  private void recreateDestinationFolder() throws CoreException {
    if ( getDestinationFolder().exists() )
      getDestinationFolder().delete( true, monitor );
    getDestinationFolder().create( true, true, monitor );
  }

  /**
   * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  protected void clean( IProgressMonitor monitor ) throws CoreException {
    recreateDestinationFolder();
    super.clean( monitor );
  }

  private Inheritations getInheritations() {
    return this.inheritations;
  }

  /**
   * Returns the {@link IJavaProject}.
   * 
   * @return the {@link IJavaProject}
   */
  protected IJavaProject getJavaProject() {
    return this.javaProject;
  }

  /**
   * Returns the folder where resources have to be created in.
   * 
   * @return the destination folder
   * @throws CoreException
   */
  protected IFolder getDestinationFolder() throws CoreException {
    if ( null == destinationFolder ) {
      destinationFolder = Artifacts.getDestinationSourceFolder( getProject() );
    }
    return destinationFolder;
  }

  /* *****************************************************************************
   *   I R E S O U R C E D E L T A V I S I T O R - I M P L E M E N T A T I O N   * 
   ***************************************************************************** */

  private void ensureExisting( IContainer folder ) throws CoreException {
    if ( null == folder )
      return;
    if ( folder instanceof IFolder ) {
      final IFolder f = ( IFolder ) folder;
      if ( !f.exists() ) {
        ensureExisting( folder.getParent() );
        f.create( true, true, monitor );
      }
    }
    ;
  }

  @SuppressWarnings( "unchecked" )
  private boolean visit( final IResource res, final int deltaKind )
      throws CoreException {
    logger.log( DEBUG, "Visiting resource \"" + res.getName()
        + "\" during bean build" );
    if ( IResource.FILE == res.getType() ) {
      // Must be Java Source
      if ( res.getName().endsWith( ".java" )
          && !getDestinationFolder().getProjectRelativePath().isPrefixOf(
              res.getProjectRelativePath() ) ) {
        if ( deltaKind == IResourceDelta.REMOVED ) {
          IPath pathToSearchFor = res.getParent().getProjectRelativePath();
          IJavaElement element = null;
          while ( !pathToSearchFor.isEmpty()
              && null == ( element = getJavaProject().findElement(
                  pathToSearchFor ) ) )
            pathToSearchFor = pathToSearchFor.removeFirstSegments( 1 );
          if ( null != element
              && ( element instanceof IPackageFragment || element instanceof IPackageFragmentRoot ) ) {
            getInheritations()
                .removeClass(
                    element instanceof IPackageFragment ? ( ( IPackageFragment ) element )
                        .getElementName().concat(
                            "."
                                + res.getName().substring( 0,
                                    res.getName().length() - 5 ) )
                        : res.getName().substring( 0,
                            res.getName().length() - 5 ) );
            inheritationsChanged = true;
            final IFile fileToDelete = getDestinationFolder()
                .getFile(
                    new Path(
                        element instanceof IPackageFragment ? ( ( IPackageFragment ) element )
                            .getElementName().replaceAll( "\\.", "/" ).concat(
                                "/" + "DaoJones" + res.getName() )
                            : "DaoJones" + res.getName() ) );
            if ( null != fileToDelete && fileToDelete.exists() )
              fileToDelete.delete( true, monitor );
          }
        } else {
          final ICompilationUnit compilationUnit = StubUtility3
              .findCompilationUnit( res );
          final CompilationUnit root = StubUtility3.getRoot( compilationUnit,
              null );
          if ( null != root ) {
            for ( AbstractTypeDeclaration type : ( List<AbstractTypeDeclaration> ) root
                .types() ) {
              final ITypeBinding typeBinding = type.resolveBinding();
              if ( !isDaoJonesBean( typeBinding ) )
                continue;
              switch ( deltaKind ) {
              case ( IResourceDelta.ADDED ):
              case ( IResourceDelta.CHANGED ):
                final ProblemDetectorEnvironment env = new ProblemDetectorEnvironment() {
                  @Override
                  public IResource getSource() {
                    return res;
                  }
                };
                // detect problems
                ProblemDetectors.getInstance().accept( env, root );
                // do not create source for abstract types or interfaces
                // Allow abtract implementations because of the Inheritations
                // annotation
                // if(null != getAnnotation(typeBinding, Abstract.class))
                // continue;
                if ( res.findMaxProblemSeverity( null, true,
                    IResource.DEPTH_INFINITE ) == IMarker.SEVERITY_ERROR )
                  continue;

                final IBean bean = new ASTBeanImpl( typeBinding ) {
                  @Override
                  public String getInheritationsFile() {
                    try {
                      return new InheritationsFileFinder( BeanBuilder.this
                          .getDestinationFolder(), BeanBuilder.this.javaProject )
                          .getInheritationsFilename();
                    } catch ( CoreException e ) {
                      logger
                          .log( ERROR, "Unable to find inheriations file!", e );
                      return "";
                    }
                  }
                }; // createBean(type, compilationUnit);
                final String implementationClassName = Naming
                    .getImplementationClassName( typeBinding.getQualifiedName() );
                final IFile destFile = getDestinationFolder().getFile(
                    new Path( implementationClassName.replaceAll( "\\.", "/" )
                        + ".java" ) );
                ensureExisting( destFile.getParent() );
                // if(!bean.isAbstract()) {
                try {
                  final ByteArrayOutputStream out = new ByteArrayOutputStream();
                  try {
                    final Generator generator = GeneratorFactory
                        .createBeanGenerator();
                    generator.generate( bean, out );
                    final ByteArrayInputStream in = new ByteArrayInputStream(
                        out.toByteArray() );
                    try {
                      if ( destFile.exists() ) {
                        destFile.setContents( in, true, false, monitor );
                      } else {
                        destFile.create( in, true, monitor );
                      }
                      destFile.setDerived( true );
                    } finally {
                      in.close();
                    }
                  } finally {
                    out.close();
                  }
                } catch ( GeneratorException e ) {
                  throw new CoreException( new Status( IStatus.ERROR,
                      Activator.PLUGIN_ID,
                      "Unable to generate DaoJones bean implementation!", e ) );
                } catch ( IOException e ) {
                  throw new CoreException( new Status( IStatus.ERROR,
                      Activator.PLUGIN_ID,
                      "Unable to generate DaoJones bean implementation!", e ) );
                }
                // }
                if ( null != typeBinding/* && null != typeBinding.getSuperclass()*/) {
                  if ( null != typeBinding.getSuperclass() )
                    getInheritations().registerInheritation(
                        typeBinding.getSuperclass().getQualifiedName(),
                        typeBinding.getQualifiedName() );
                  for ( ITypeBinding i : typeBinding.getInterfaces() ) {
                    getInheritations().registerInheritation(
                        i.getQualifiedName(), typeBinding.getQualifiedName() );
                  }
                  inheritationsChanged = true;
                }
                // destinationFolder.refreshLocal(IResource.DEPTH_INFINITE,
                // this.monitor);
                break;
              }
              ;
            }
          }
        }
      }
    } else if ( IResource.FOLDER == res.getType() ) {
      if ( deltaKind == IResourceDelta.REMOVED ) {
        if ( !getDestinationFolder().getProjectRelativePath().isPrefixOf(
            res.getProjectRelativePath() ) ) {
          IPath pathToSearchFor = res.getParent().getProjectRelativePath();
          IJavaElement element = null;
          while ( !pathToSearchFor.isEmpty()
              && null == ( element = getJavaProject().findElement(
                  pathToSearchFor ) ) )
            pathToSearchFor = pathToSearchFor.removeFirstSegments( 1 );
          if ( null != element
              && ( element instanceof IPackageFragment || element instanceof IPackageFragmentRoot ) ) {
            final IFolder folderToDelete = getDestinationFolder()
                .getFolder(
                    new Path(
                        element instanceof IPackageFragment ? ( ( IPackageFragment ) element )
                            .getElementName().concat( "." + res.getName() )
                            .replaceAll( "\\.", "/" )
                            : res.getName() ) );
            if ( null != folderToDelete && folderToDelete.exists() )
              folderToDelete.delete( true, monitor );
          }
        }
      }
    }
    logger.log( DEBUG, "Finished visiting resource \"" + res.getName()
        + "\" during bean build" );
    return true;
  }

  /**
   * This is called in case of full builds.
   * 
   * @see IResourceProxyVisitor#visit(IResourceProxy)
   */
  @Override
  public boolean visit( IResourceProxy proxy ) throws CoreException {
    return visit( ResourceUtil.createProxyResource( proxy ) );
  }

  /**
   * This is called in case of full builds.
   * 
   * @see IResourceVisitor#visit(IResource)
   */
  @Override
  public boolean visit( IResource proxy ) throws CoreException {
    return visit( proxy, IResourceDelta.ADDED );
  }

  /**
   * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
   */
  @Override
  public boolean visit( IResourceDelta delta ) throws CoreException {
    return visit( delta.getResource(), delta.getKind() );
  }

}
