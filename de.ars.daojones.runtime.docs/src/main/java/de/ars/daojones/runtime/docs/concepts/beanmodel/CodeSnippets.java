package de.ars.daojones.runtime.docs.concepts.beanmodel;

import java.util.Collection;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModel.Id;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;
import de.ars.daojones.runtime.docs.concepts.Memo;

@SuppressWarnings("unused")
public class CodeSnippets {

  public void beanModelAccess() throws ConfigurationException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    // -1-
    final BeanModelManager bmm = ctx.getConfiguration().getBeanModelManager();
    final Collection<BeanModel> models = bmm.getModels();
  }

  public void beanModelAccessSingle() throws ConfigurationException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    final BeanModelManager bmm = ctx.getConfiguration().getBeanModelManager();
    // -1-
    final String app = "mail";
    final Id id = new Id(app, Memo.class.getName());
    final BeanModel model = bmm.getModel(id);
  }

  public void beanModelEffective() throws ConfigurationException {
    final DaoJonesContextFactory factory = new DaoJonesContextFactory();
    final DaoJonesContext ctx = factory.createContext();
    final String app = "mail";
    final BeanModelManager bmm = ctx.getConfiguration().getBeanModelManager();
    // -1-
    final BeanModel model = bmm.getEffectiveModel(app, Memo.class);
  }

}
