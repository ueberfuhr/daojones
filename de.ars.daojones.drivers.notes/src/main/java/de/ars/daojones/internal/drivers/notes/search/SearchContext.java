package de.ars.daojones.internal.drivers.notes.search;

import lotus.domino.Database;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.Query;

public class SearchContext {
  private Database database;
  private Query query;
  private Bean bean;

  public SearchContext() {
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase( Database database ) {
    this.database = database;
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery( Query query ) {
    this.query = query;
  }

  public Bean getBean() {
    return bean;
  }

  public void setBean( Bean bean ) {
    this.bean = bean;
  }
}