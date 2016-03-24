package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.internal.runtime.configuration.ModelNormalizer;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;

/**
 * A bean model normalizer reads the bean model and commonly replaces null
 * values by default values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface BeanModelNormalizer extends ModelNormalizer<BeanModel.Id, BeanModel, BeanModelManager> {

}
