package de.ars.daojones.integration.equinox.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;

/**
 * An {@link ISchedulingRule} for a single connection.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class ConnectionLock implements ISchedulingRule {

  /**
   * The operation that a connection is used for.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH
   * @since 1.1.0
   */
  public static enum Operation implements ISchedulingRule {
    /**
     * The connection is used for reading data.
     */
    READ_DATA,
    /**
     * The connection is used for writing data.
     */
    WRITE_DATA,
    /**
     * The connection is deleted.
     */
    ALL( READ_DATA, WRITE_DATA );

    private final Set<Operation> containing = new HashSet<Operation>();

    private Operation( final Operation... containing ) {
      if ( null != containing && containing.length > 0 ) {
        this.containing.addAll( Arrays.asList( containing ) );
      }
    }

    @Override
    public boolean contains( final ISchedulingRule rule ) {
      return this.containing.contains( rule );
    }

    @Override
    public boolean isConflicting( final ISchedulingRule rule ) {
      return rule == this || rule.contains( this );
    }
  }

  private final Operation operation;
  private final ConnectionModel connectionModel;
  private final Class<?> beanClass;

  /**
   * Creates an instance that locks a single instance assigned to a dao class.
   * 
   * @param connectionModel
   *          the connection model
   * @param operation
   *          the operation that the connection is locked for (one of
   *          {@link Operation#READ_DATA} or {@link Operation#WRITE_DATA})
   * @param beanClass
   *          the bean class
   */
  public ConnectionLock( final ConnectionModel connectionModel, final Operation operation, final Class<?> beanClass ) {
    super();
    this.connectionModel = connectionModel;
    this.operation = operation;
    this.beanClass = beanClass;
  }

  /**
   * Creates an instance that locks all operations.
   * 
   * @param connectionModel
   *          the connection model
   */
  public ConnectionLock( final ConnectionModel connectionModel ) {
    this( connectionModel, Operation.ALL, null );
  }

  @Override
  public boolean contains( final ISchedulingRule rule ) {
    if ( rule == this ) {
      return true;
    }
    if ( rule instanceof ConnectionLock ) {
      final ConnectionLock lock = ( ConnectionLock ) rule;
      return this.operation.contains( lock.operation ) && ConnectionLock.equals( this, lock );
    }
    return false;
  }

  @Override
  public boolean isConflicting( final ISchedulingRule rule ) {
    if ( rule == this ) {
      return true;
    }
    if ( rule instanceof ConnectionLock ) {
      final ConnectionLock lock = ( ConnectionLock ) rule;
      return this.operation.isConflicting( lock.operation ) && ConnectionLock.equals( this, lock );
    }
    return false;
  }

  private static boolean equals( final Object o1, final Object o2 ) {
    if ( o1 == o2 ) {
      return true;
    }
    return null != o1 ? o1.equals( o2 ) : false;
  }

  private static boolean equals( final ConnectionLock lock1, final ConnectionLock lock2 ) {
    // check application id and bean class
    if ( ConnectionLock.equals( lock1.connectionModel.getId().getApplicationId(), lock2.connectionModel.getId()
            .getApplicationId() )
            && ConnectionLock.equals( lock1.beanClass, lock2.beanClass ) ) {
      // same id -> equal
      if ( null != lock1.connectionModel.getId()
              && lock1.connectionModel.getId().equals( lock2.connectionModel.getId() ) ) {
        return true;
      }
      // both default or same for classes -> equal
      if ( !lock1.connectionModel.getConnection().getAssignedBeanTypes().isEmpty() ) {
        if ( Arrays.equals( lock1.connectionModel.getConnection().getAssignedBeanTypes().toArray(),
                lock2.connectionModel.getConnection().getAssignedBeanTypes().toArray() ) ) {
          return true;
        }
      } else if ( lock1.connectionModel.getConnection().isDefault()
              && lock2.connectionModel.getConnection().isDefault() ) {
        return true;
      }
    }
    return false;
  }

}
