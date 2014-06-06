package org.apache.log4j;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;

import org.apache.log4j.spi.AppenderAttachable;
import org.jboss.logmanager.Logger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class Appenders {

    private static final org.jboss.logmanager.Logger.AttachmentKey<CopyOnWriteArrayList<Appender>> APPENDERS_KEY = new org.jboss.logmanager.Logger.AttachmentKey<CopyOnWriteArrayList<Appender>>();

    /**
     * Attaches an appender to the logger.
     *
     * @param logger   the logger to attach the appender to.
     * @param appender the appender to attach.
     */
    public static void attachAppender(final Logger logger, final Appender appender) {
        Handler[] oldHandlers;
        Handler[] newHandlers;
        CAS:
        do {
            oldHandlers = logger.getHandlers();
            for (Handler handler : oldHandlers) {
                if (handler instanceof JBossAppenderHandler) {
                    break CAS;
                }
            }
            final int size = oldHandlers.length;
            newHandlers = Arrays.copyOf(oldHandlers, size + 1);
            if (System.getSecurityManager() == null) {
                newHandlers[size] = new JBossAppenderHandler(logger);
            } else {
                newHandlers[size] = AccessController.doPrivileged(new PrivilegedAction<Handler>() {
                    @Override
                    public Handler run() {
                        return new JBossAppenderHandler(logger);
                    }
                });
            }
        } while (!logger.compareAndSetHandlers(oldHandlers, newHandlers));
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
     * Closes the appenders attached to the logger if the appenders are an instance of {@link org.apache.log4j.spi.AppenderAttachable}.
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

    static CopyOnWriteArrayList<Appender> getAppenderList(final Logger logger) {
        CopyOnWriteArrayList<Appender> result = logger.getAttachment(APPENDERS_KEY);
        if (result == null) {
            result = new CopyOnWriteArrayList<Appender>();
            final CopyOnWriteArrayList<Appender> current;
            if (System.getSecurityManager() == null) {
                current = logger.attachIfAbsent(APPENDERS_KEY, result);
            } else {
                final CopyOnWriteArrayList<Appender> attachment = result;
                current = AccessController.doPrivileged(new PrivilegedAction<CopyOnWriteArrayList<Appender>>() {
                    @Override
                    public CopyOnWriteArrayList<Appender> run() {
                        return logger.attachIfAbsent(APPENDERS_KEY, attachment);
                    }
                });
            }
            if (current != null) {
                result = current;
            }
        }
        return result;
    }
}
