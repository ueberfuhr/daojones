package de.ars.daojones.integration.web;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

/**
 * A central object to access the DaoJones environment of this web application.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class WebApplication {

  private WebApplication() {
    super();
  }

  /**
   * Returns the DaoJones environment for the given servlet context.
   * 
   * @param ctx
   *          the servlet context
   * @return the DaoJones environment
   */
  public static DaoJonesEnvironment getEnvironment( final ServletContext ctx ) {
    return ( DaoJonesEnvironment ) ctx.getAttribute( Configuration.getEnvironmentName( ctx ) );
  }

  /**
   * Returns the DaoJones environment for the given page context.
   * 
   * @param ctx
   *          the page context
   * @return the DaoJones environment
   */
  public static DaoJonesEnvironment getEnvironment( final PageContext ctx ) {
    final ServletContext sc = ctx.getRequest().getServletContext();
    return WebApplication.getEnvironment( sc );
  }

  /**
   * Returns the DaoJones environment for the given JSP context.
   * 
   * @param ctx
   *          the JSP context
   * @return the DaoJones environment
   */
  public static DaoJonesEnvironment getEnvironment( final JspContext ctx ) {
    return WebApplication.getEnvironment( ( PageContext ) ctx );
  }

  /**
   * Returns the DaoJones environment for the given JSP context.
   * 
   * @param ctx
   *          the JSP context
   * @return the DaoJones environment
   */
  public static DaoJonesEnvironment getEnvironment( final FacesContext ctx ) {
    return WebApplication.getEnvironment( ( ServletContext ) ctx.getExternalContext().getContext() );
  }

}
