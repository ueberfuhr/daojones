package de.ars.daojones.runtime.query;

/**
 * The {@link SearchCriterion} combining two {@link SearchCriterion} instances
 * with a {@link LogicalCombination}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class LogicalSearchCriterion implements SearchCriterion {

	private static final long serialVersionUID = 5784142688755099149L;
	private final SearchCriterion c1;
	private final SearchCriterion c2;
	private final LogicalCombination combination;

	/**
	 * Creates an instance.
	 * 
	 * @param c1
	 *            the first {@link SearchCriterion}
	 * @param combination
	 *            the {@link LogicalCombination}
	 * @param c2
	 *            the second {@link SearchCriterion}
	 */
	public LogicalSearchCriterion(final SearchCriterion c1,
			final LogicalCombination combination, final SearchCriterion c2) {
		super();
		this.c1 = c1;
		this.combination = combination;
		this.c2 = c2;
	}

	/**
	 * @see de.ars.daojones.runtime.query.SearchCriterion#toQuery(de.ars.daojones.runtime.query.TemplateManager,
	 *      de.ars.daojones.runtime.query.VariableResolver)
	 */
	public String toQuery(TemplateManager templateManager,
			final VariableResolver resolver) throws VariableResolvingException {
		return combination.toQuery(templateManager, c1.toQuery(templateManager,
				resolver), c2.toQuery(templateManager, resolver));
	}

	private static SearchCriterion toSearchCriterion(int index,
			LogicalCombination combination, SearchCriterion... criterions) {
		if (index == criterions.length - 1)
			return criterions[index];
		if (index >= criterions.length)
			return new TautologySearchCriterion();
		return new LogicalSearchCriterion(criterions[index], combination,
				toSearchCriterion(index + 1, combination, criterions));
	}

	/**
	 * Combines a couple of {@link SearchCriterion} instances with one {@link LogicalCombination}.
	 * @param combination the {@link LogicalCombination}
	 * @param criterions the {@link SearchCriterion} instances
	 * @return the combined {@link SearchCriterion}
	 */
	public static SearchCriterion toSearchCriterion(
			LogicalCombination combination, SearchCriterion... criterions) {
		if (null == criterions || criterions.length<1)
			return new TautologySearchCriterion();
		return toSearchCriterion(0, combination, criterions);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((c1 == null) ? 0 : c1.hashCode());
		result = PRIME * result + ((c2 == null) ? 0 : c2.hashCode());
		result = PRIME * result
				+ ((combination == null) ? 0 : combination.hashCode());
		return result;
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
		if (getClass() != obj.getClass())
			return false;
		final LogicalSearchCriterion other = (LogicalSearchCriterion) obj;
		if (c1 == null) {
			if (other.c1 != null)
				return false;
		} else if (!c1.equals(other.c1))
			return false;
		if (c2 == null) {
			if (other.c2 != null)
				return false;
		} else if (!c2.equals(other.c2))
			return false;
		if (combination == null) {
			if (other.combination != null)
				return false;
		} else if (!combination.equals(other.combination))
			return false;
		return true;
	}

}
