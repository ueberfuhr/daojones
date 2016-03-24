package de.ars.daojones.runtime.spi.cache;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * A command that can be serialized for caching.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <V> the result type of method <tt>call</tt>
 */
public interface RepeatableCommand<V> extends Callable<V>, Serializable {

}
