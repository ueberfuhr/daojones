package de.ars.daojones.sdk.codegen.generators;

/**
 * A factory generating some {@link Generator}s to generate source code.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class GeneratorFactory {

	private static final String IMPL_CLASS = Generator.class.getName() + "Impl";

	/**
	 * Creates a generator that generates a java source file for a DaoJones bean
	 * implementation.
	 * 
	 * @return the generator
	 * @throws GeneratorException
	 *             if creating the generator fails
	 */
	public static Generator createBeanGenerator() throws GeneratorException {

		try {
			return (Generator) Class.forName(IMPL_CLASS).newInstance();
		} catch (InstantiationException e) {
			throw new GeneratorException(
					"The class "
							+ IMPL_CLASS
							+ " provided by the plug-in fragment cannot be instantiated!",
					e);
		} catch (IllegalAccessException e) {
			throw new GeneratorException(
					"The class "
							+ IMPL_CLASS
							+ " provided by the plug-in fragment cannot be instantiated!",
					e);
		} catch (ClassNotFoundException e) {
			throw new GeneratorException(
					"Missing plug-in fragment implementing the DaoJones SDK Generator interface!",
					e);
		}
	}

}
