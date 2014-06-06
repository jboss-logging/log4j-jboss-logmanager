package org.apache.log4j;

import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Logger;

/**
 * A handler that can be assigned {@link org.jboss.logmanager.Logger} that wraps an {@link Appender appender}.
 * <p/>
 * {@link Appender appenders} are executed for log messages that come through the JBoss Log Manager.
 * <p/>
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
final class JBossAppenderHandler extends ExtHandler {

    private final Logger logger;

    protected JBossAppenderHandler(final Logger logger) {
        this.logger = logger;
    }


    @Override
    protected void doPublish(final ExtLogRecord record) {
        final LoggingEvent event = new LoggingEvent(record, JBossLogManagerFacade.getLogger(logger));
        final List<Appender> appenders = Appenders.getAppenderList(logger);
        for (Appender appender : appenders) {
            if (new JBossFilterWrapper(appender.getFilter(), true).isLoggable(record)) {
                appender.doAppend(event);
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        checkAccess(this);
        Appenders.closeAppenders(logger);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = prime * hash + (logger == null ? 0 : logger.hashCode());
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JBossAppenderHandler)) {
            return false;
        }
        final JBossAppenderHandler other = (JBossAppenderHandler) obj;
        return (logger == null ? other.logger == null : (logger.equals(other.logger)));
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getClass()).append("{").append(logger.getName()).append("}").toString();
    }

}
