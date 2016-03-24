package de.ars.daojones.runtime.query;

/**
 * A {@link SearchCriterion} that always returns true. This is only for
 * combining purposes.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class TautologySearchCriterion implements SearchCriterion {

    private static final long serialVersionUID = -3758063470825154450L;

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj instanceof TautologySearchCriterion;
    }

    /**
     * @see de.ars.daojones.runtime.query.SearchCriterion#toQuery(de.ars.daojones.runtime.query.TemplateManager,
     *      de.ars.daojones.runtime.query.VariableResolver)
     */
    // TODO Java6-Migration
    // @Override
    public String toQuery(TemplateManager templateManager,
            VariableResolver resolver) throws VariableResolvingException {
        return templateManager.getTemplate("logical.tautology");
    }

}
