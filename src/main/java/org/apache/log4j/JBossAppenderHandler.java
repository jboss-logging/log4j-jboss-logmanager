package org.apache.log4j;

import static org.apache.log4j.JBossLogManagerFacade.JBL_ROOT_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.spi.AppenderAttachable;
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

    private static final org.jboss.logmanager.Logger.AttachmentKey<CopyOnWriteArrayList<Appender>> APPENDERS_KEY = new org.jboss.logmanager.Logger.AttachmentKey<CopyOnWriteArrayList<Appender>>();

    private final Logger logger;

    private JBossAppenderHandler(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates a new handler for handling appenders if one does not already exist.
     *
     * @param logger the logger to attach the handler to and process appenders for
     */
    public static void createAndAttach(final Logger logger) {
        if (logger.getAttachment(APPENDERS_KEY) == null) {
            CopyOnWriteArrayList<Appender> list = new CopyOnWriteArrayList<Appender>();
            final CopyOnWriteArrayList<Appender> existing = logger.attachIfAbsent(APPENDERS_KEY, list);
            if (existing != null) {
                list = existing;
            } else {
                // add handler
                final JBossAppenderHandler handler = new JBossAppenderHandler(logger);
                logger.addHandler(handler);
            }
        }
    }


    @Override
    protected void doPublish(final ExtLogRecord record) {
        String loggerName = record.getLoggerName();
        if (loggerName == null) {
            loggerName = JBL_ROOT_NAME;
        }
        if (loggerName.equals(logger.getName())) {
            final LoggingEvent event = new LoggingEvent(record, JBossLogManagerFacade.getLogger(logger));
            final List<Appender> appenders = getAllAppenders(logger);
            for (Appender appender : appenders) {
                if (new JBossFilterWrapper(appender.getFilter(), true).isLoggable(record)) {
                    appender.doAppend(event);
                }
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        checkAccess(this);
        closeAppenders(logger);
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

    /**
     * Attaches an appender to the logger.
     *
     * @param logger   the logger to attach the appender to.
     * @param appender the appender to attach.
     */
    public static void attachAppender(final Logger logger, final Appender appender) {
        getAppenderList(logger).addIfAbsent(appender);
    }

    /**
     * Retrieves all the appenders that are associated with this logger only.
     *
     * @param logger the logger to retrieve the appenders on.
     *
     * @return a collection of the appenders.
     */
    public static List<Appender> getAppenders(final Logger logger) {
        List<Appender> appenders = getAppenderList(logger);
        if (appenders == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Appender>(appenders);
    }

    /**
     * Retrieves all the appenders that are associated with the logger and any parent loggers. If the {@link
     * org.jboss.logmanager.Logger#getUseParentHandlers()} returns {@code false}, the chain is broken and no more
     * appenders are returned.
     *
     * @param logger the logger to retrieve the appenders on.
     *
     * @return a collection of the appenders or an empty collection.
     */
    public static List<Appender> getAllAppenders(final Logger logger) {
        final List<Appender> result = new ArrayList<Appender>();
        getAllAppenders(logger, result);
        return result;
    }

    /**
     * Retrieves all the appenders that are associated with the logger and any parent loggers. If the {@link
     * org.jboss.logmanager.Logger#getUseParentHandlers()} returns {@code false}, the chain is broken and no more
     * appenders are returned.
     *
     * @param logger the logger to retrieve the appenders on.
     * @param result the list that will have the appenders added to it.
     */
    static void getAllAppenders(final Logger logger, final List<Appender> result) {
        result.addAll(getAppenderList(logger));
        if (logger.getUseParentHandlers()) {
            final Category category = JBossLogManagerFacade.getLogger(logger);
            if (category != null && category.getParent() != null) {
                getAllAppenders(category.getParent().jblmLogger, result);
            }
        }
    }

    /**
     * Retrieves a single appender based on the appender name.
     *
     * @param logger the logger to check fot the appender.
     * @param name   the name of the appender.
     *
     * @return the appender or {@code null} if the appender was not found.
     */
    public static Appender getAppender(final Logger logger, final String name) {
        Appender result = null;
        if (name != null) {
            List<Appender> appenders = getAppenders(logger);
            for (Appender appender : appenders) {
                if (name.equals(appender.getName())) {
                    result = appender;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks the logger to see if the appender is attached.
     *
     * @param logger   the logger to check for the appender.
     * @param appender the appender to check for.
     *
     * @return {@code true} if the appender is found on the logger, otherwise {@code false}.
     */
    public static boolean isAppenderAttached(final Logger logger, final Appender appender) {
        return getAppenders(logger).contains(appender);
    }

    /**
     * Removes all the appenders from the logger and returns the all the appenders that were removed.
     *
     * @param logger the logger to remove the appenders from.
     *
     * @return a collection of the appenders that were removed.
     */
    public static List<Appender> removeAllAppenders(final Logger logger) {
        List<Appender> result = Collections.emptyList();
        final List<Appender> currentAppenders = getAppenderList(logger);
        if (currentAppenders != null && !currentAppenders.isEmpty()) {
            result = new ArrayList<Appender>(currentAppenders);
            currentAppenders.clear();
        }
        return result;
    }

    /**
     * Removes a single appender from the logger.
     *
     * @param logger   the logger to remove the appender from.
     * @param appender the appender to remove.
     *
     * @return {@code true} if the appender wasn't {@code null} and was successfully removed, otherwise {@code false}.
     */
    public static boolean removeAppender(final Logger logger, final Appender appender) {
        boolean result = false;
        final List<Appender> currentAppenders = getAppenderList(logger);
        if (currentAppenders != null) {
            result = currentAppenders.remove(appender);
        }
        return result;
    }

    /**
     * Closes the appenders attached to the logger if the appenders are an instance of {@link AppenderAttachable}.
     *
     * @param logger the logger to close the appenders on.
     */
    public static void closeAppenders(final Logger logger) {
        final List<Appender> appenders = getAppenderList(logger);
        for (Appender appender : appenders) {
            if (appender instanceof AppenderAttachable) {
                appender.close();
            }
        }
    }

    private static CopyOnWriteArrayList<Appender> getAppenderList(final Logger logger) {
        CopyOnWriteArrayList<Appender> result = logger.getAttachment(APPENDERS_KEY);
        if (result == null) {
            result = new CopyOnWriteArrayList<Appender>();
            final CopyOnWriteArrayList<Appender> current = logger.attachIfAbsent(APPENDERS_KEY, result);
            if (current != null) {
                result = current;
            }
        }
        return result;
    }
}
