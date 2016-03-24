package de.ars.daojones.internal.drivers.notes.search.encoders;

import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.query.SearchCriterion;

public class UnsupportedCollectionContentException extends QueryLanguageException {

  private static final long serialVersionUID = 1L;

  public UnsupportedCollectionContentException( final SearchCriterion criterion, final Bean model,
          final String message, final Throwable cause ) {
    super( criterion, model, message, cause );
  }

  public UnsupportedCollectionContentException( final SearchCriterion criterion, final Bean model, final String message ) {
    super( criterion, model, message );
  }

  public UnsupportedCollectionContentException( final SearchCriterion criterion, final Bean model, final Throwable cause ) {
    super( criterion, model, cause );
  }

  public UnsupportedCollectionContentException( final SearchCriterion criterion, final Bean model ) {
    super( criterion, model );
  }

}
