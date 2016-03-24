package de.ars.daojones.drivers.notes;

import java.util.concurrent.Callable;

import java.io.Serializable;

/**
 * An interface merging the {@link Serializable} interface and the
 * {@link Callable} interface.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
interface SerializableCallable<T> extends Serializable, Callable<T> {

}
