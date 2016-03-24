package de.ars.daojones.runtime.docs.integration;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import de.ars.daojones.integration.web.DaoJonesEnvironment;
import de.ars.daojones.integration.web.WebApplication;

public class PrintEnvironmentTag extends SimpleTagSupport {

  @Override
  public void doTag() throws JspException, IOException {
    final DaoJonesEnvironment dj = WebApplication.getEnvironment(getJspContext());
    final JspWriter out = getJspContext().getOut();
    try {
      // "DaoJones Runtime"
      out.print(dj.getTitle());
      out.print(" ");
      // e.g. "2.0.0"
      out.println(dj.getVersion());
      // DaoJones application id assigned to this web application
      out.println(dj.getApplication().getApplicationId());
    } finally {
      out.close();
    }
  }

}
