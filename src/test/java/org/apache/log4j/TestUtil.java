package org.apache.log4j;

import java.util.Collection;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestUtil {

    private static void resetConfiguration() {
        final Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
        root.setResourceBundle(null);
        LogManager.getLoggerRepository().setThreshold(Level.ALL);
        shutdown();
        final Collection<Logger> loggers = JBossLogManagerFacade.getLoggers();
        for (Logger logger : loggers) {
            logger.setLevel(null);
            logger.setAdditivity(true);
            logger.setResourceBundle(null);
        }
    }

    public static void shutdown() {
        final Logger root = Logger.getRootLogger();

        // begin by closing nested appenders
        root.closeNestedAppenders();
        final Collection<Logger> loggers = JBossLogManagerFacade.getLoggers();
        for (Logger logger : loggers) {
            logger.closeNestedAppenders();
        }
        root.removeAllAppenders();
        for (Logger logger : loggers) {
            logger.removeAllAppenders();
        }
    }
}
