package de.ars.daojones.runtime.docs.database_access_advanced;

import java.net.URL;
import java.util.Collection;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.query.AbstractSearchCriterion;

public class IsFavorite extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  private final Collection<URL> favorites;

  public IsFavorite(final Collection<URL> favorites) {
    super();
    this.favorites = favorites;
  }

  public Collection<URL> getFavorites() {
    return favorites;
  }

  @Override
  public boolean matches(final ApplicationContext ctx, final Object bean)
          throws ConfigurationException {
    return favorites.contains(bean);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((favorites == null) ? 0 : favorites.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || getClass() == obj.getClass()
            && favorites.equals(((IsFavorite) obj).favorites);
  }

}
