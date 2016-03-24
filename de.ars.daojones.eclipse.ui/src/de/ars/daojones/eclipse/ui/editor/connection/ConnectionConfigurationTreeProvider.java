package de.ars.daojones.eclipse.ui.editor.connection;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.ars.daojones.connections.model.IConnection;
import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.IImportDeclaration;
import de.ars.daojones.eclipse.ui.Images;

/**
 * A {@link ITreeContentProvider} for the list of elements that are contained within an
 * {@link IConnectionConfiguration}.
 * It groups the items by their kind.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ConnectionConfigurationTreeProvider implements ITreeContentProvider, ILabelProvider, IFontProvider {
	
    private IConnectionConfiguration conf;
    private Object root;
    private final Collection<ILabelProviderListener> labelProviderListeners = new HashSet<ILabelProviderListener>();
    
    private static final String TITLE_IMPORTS = "Imported Configurations";
    private static final String TITLE_CREDENTIALS = "Credentials";
    private static final String TITLE_CONNECTIONS = "Connections";
    
    /*
     * GENERAL INFORMATION
     * ===================
     * This provider shows imports, credentials and connections of a connection configuration.
     * Their are grouped under their kind like the following example shows:
     * 
     * <image> Imported Configurations
     *   |--  Configuration1
     *   |--  Configuration2
     * <image> Credentials
     *   |--  Credential1
     *   |--  Credential2
     * <image> Connections
     *   |--  Connections1
     *   |--  Connections2
     *   
     *   If there are no Credentials, the "Credentials" title is not shown. (The same with imports and connections.)
     *   The group titles are the constants TITLE_IMPORTS, TITLE_CREDENTIALS and TITLE_CONNECTIONS.
     */
    
    /**
     * Creates an instance.
     * @param conf
     */
    public ConnectionConfigurationTreeProvider(IConnectionConfiguration conf) {
        this.conf = conf;
    }

    /**
     * Returns the {@link IConnectionConfiguration}.
     * @return the {@link IConnectionConfiguration}
     */
    protected IConnectionConfiguration getConfiguration() {
        return conf;
    }

    /**
     * @see ITreeContentProvider#getChildren(Object)
     */
	// TODO Java6-Migration
	// @Override
    public Object[] getChildren(Object parentElement) {
    	/*
    	 * RULES:
    	 * ======
    	 * If the root element is the parent, return the constants TITLE_IMPORTS, TITLE_CREDENTIALS and TITLE_CONNECTIONS.
    	 *  
    	 */
    	final List<Object> childs = new LinkedList<Object>();
        if(root == parentElement) {
        	if(!getConfiguration().getImportDeclarations().isEmpty()) childs.add(TITLE_IMPORTS);
        	if(!getConfiguration().getCredentials().isEmpty()) childs.add(TITLE_CREDENTIALS);
        	if(!getConfiguration().getConnections().isEmpty()) childs.add(TITLE_CONNECTIONS);
        } else if(parentElement == TITLE_IMPORTS) {
        	childs.addAll(getConfiguration().getImportDeclarations());
        } else if(parentElement == TITLE_CREDENTIALS) {
        	childs.addAll(getConfiguration().getCredentials());
        } else if(parentElement == TITLE_CONNECTIONS) {
        	childs.addAll(getConfiguration().getConnections());
        };
        return childs.toArray(new Object[childs.size()]);
    }

    /**
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
    	if(element == TITLE_IMPORTS) {
            return root;
    	} else if(element == TITLE_CREDENTIALS) {
            return root;
    	} else if(element == TITLE_CONNECTIONS) {
            return root;
    	} else if(element instanceof IImportDeclaration) {
    		return TITLE_IMPORTS;
    	} else if(element instanceof ICredential) {
    		return TITLE_CREDENTIALS;
    	} else if(element instanceof IConnection) {
    		return TITLE_CONNECTIONS;
    	};
    	return null;
    }

    /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        return
        	(
        		element == TITLE_IMPORTS
        		||
        		element == TITLE_CREDENTIALS
        		||
        		element == TITLE_CONNECTIONS
        	)
        	||
        	root == element && 
        	(
        		!getConfiguration().getImportDeclarations().isEmpty()
        		||
        		!getConfiguration().getCredentials().isEmpty()
        		||
        		!getConfiguration().getConnections().isEmpty()
        	);
    }

    /**
     * @see ITreeContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /**
     * @see ITreeContentProvider#dispose()
     */
    public void dispose() {}
    /**
     * @see ITreeContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer1, Object obj, Object obj1) {
    	this.root = obj1;
    }

    public boolean isLabelProperty(Object element, String property) {
        return (element instanceof IConnection) && "name".equals(property);
    }

    public void addListener(ILabelProviderListener listener) {
        labelProviderListeners.add(listener);
    }

    public void removeListener(ILabelProviderListener listener) {
        labelProviderListeners.remove(listener);
    }

    public Image getImage(Object element) {
        if(element == TITLE_CONNECTIONS/*instanceof IConnection*/) {
            return Images.CONNECTION.getImage();
        } else if(element == TITLE_CREDENTIALS/*instanceof ICredential*/) {
            return Images.CREDENTIAL.getImage();
        } else if(element == TITLE_IMPORTS/*instanceof IImportDeclaration*/) {
            return Images.CONNECTION_IMPORT.getImage();
        } else {
            return null;
        }
    }

    public String getText(Object element) {
        if(element instanceof IConnection) {
            return ((IConnection)element).getName();
        } else if(element instanceof ICredential) {
            return ((ICredential)element).getId();
        } else if(element instanceof IImportDeclaration) {
            return ((IImportDeclaration)element).getFile();
        } else {
            return new StringBuilder().append(element).toString();
        }
    }

	// TODO Java6-Migration
	// @Override
	public Font getFont(Object element) {
        final FontData systemFontData = Display.getCurrent().getSystemFont().getFontData()[0];
		if(element == TITLE_IMPORTS
        		||
        		element == TITLE_CREDENTIALS
        		||
        		element == TITLE_CONNECTIONS
		) {
	        return new Font(
	            	Display.getCurrent(), 
	            	new FontData( 
	            		systemFontData.getName(), 
	            		systemFontData.getHeight(),
	                    systemFontData.getStyle() | SWT.BOLD
	                ) 
	           );
		} else {
	        return new Font(
	            	Display.getCurrent(), 
	            	systemFontData 
	           );
		}
	}

}
