package de.ars.daojones.internal.integration.eclipse.preferences.pages;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {
  private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
  public static String CONNECTIONSPAGE_CONNECTIONSTREE_TITLE_COLUMN1;
  public static String CONNECTIONSPAGE_CONNECTIONSTREE_TITLE_COLUMN2;
  static {
    // initialize resource bundle
    NLS.initializeMessages( Messages.BUNDLE_NAME, Messages.class );
  }

  private Messages() {
  }
}
