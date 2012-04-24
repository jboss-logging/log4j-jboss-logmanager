package org.apache.log4j;
import java.util.logging.LogRecord;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import org.jboss.logmanager.ExtLogRecord;

/**
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class JBossFilterWrapper implements java.util.logging.Filter {
    private final Filter filterChain;
    private final boolean defaultResult;

    public JBossFilterWrapper(Filter filterChain, final boolean defaultResult) {
        this.filterChain = filterChain;
        this.defaultResult = defaultResult;
    }

    public boolean isLoggable(final LogRecord record) {
        final ExtLogRecord extRec = ExtLogRecord.wrap(record);
        Filter filter = filterChain;
        while (filter != null) {
            final int result = filter.decide(new LoggingEvent(extRec, Logger.getLogger(record.getLoggerName())));
            switch (result) {
                case Filter.DENY: return false;
                case Filter.ACCEPT: return true;
            }
            filter = filter.getNext();
        }
        return defaultResult;
    }
}
