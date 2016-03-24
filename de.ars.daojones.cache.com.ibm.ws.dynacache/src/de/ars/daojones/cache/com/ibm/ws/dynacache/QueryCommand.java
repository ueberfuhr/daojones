package de.ars.daojones.cache.com.ibm.ws.dynacache;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.ibm.websphere.command.CacheableCommandImpl;
import de.ars.daojones.cache.CacheKey;
import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.RepeatableCommand;

/**
 * A query command using WebSphere Command Cache.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class QueryCommand<K, V> extends CacheableCommandImpl {

    private static final long serialVersionUID = -9034495312422665433L;
    private static final Logger logger = Logger.getLogger(QueryCommand.class
            .getName());

    private CacheKey<K> key;
    private CacheValue<V> value;
    private RepeatableCommand<CacheValue<V>> callable;

    /**
     * Returns the value.
     * 
     * @return the value
     */
    public CacheValue<V> getValue() {
        return value;
    }

    /**
     * Sets the key.
     * 
     * @param key
     *            the key
     */
    public void setKey(CacheKey<K> key) {
        this.key = key;
    }

    /**
     * Returns the key.
     * 
     * @return the key
     */
    public CacheKey<K> getKey() {
        return key;
    }

    /**
     * Returns the cache id.
     * 
     * @return the cache id
     */
    public long getCacheId() {
        try {
            return getKey().hashCode();
        } catch (Throwable t) {
            logger.log(Level.WARNING,
                    "Unable to get cache id for entry of type "
                            + QueryCommand.class.getName() + ".", t);
            return -1;
        }
    }

    /**
     * Sets the {@link RepeatableCommand}.
     * 
     * @param callable
     *            the {@link RepeatableCommand}
     */
    public void setCallable(RepeatableCommand<CacheValue<V>> callable) {
        this.callable = callable;
    }

    /**
     * @see com.ibm.websphere.command.TargetableCommandImpl#isReadyToCallExecute()
     */
    @Override
    public boolean isReadyToCallExecute() {
        return null != this.key && null != this.callable;
    }

    /**
     * @see com.ibm.websphere.command.TargetableCommandImpl#performExecute()
     */
    @Override
    public void performExecute() throws Exception {
        // TODO value#isValid berücksichtigen
        this.value = this.callable.call();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof QueryCommand
                && Long.valueOf(getCacheId()).equals(
                        ((QueryCommand) obj).getCacheId());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.valueOf(getCacheId()).hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (null != getValue()) {
            sb.append(getValue().getData());
            if (!getValue().isValid()) {
                sb.append(" (INVALID)");
            }
            sb.append(", found by ");
        }
        sb.append(getKey());
        return sb.toString();
    }

}
