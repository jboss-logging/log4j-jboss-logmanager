package org.apache.log4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger.AttachmentKey;

/**
 * A simple facade to interact between {@link org.apache.log4j.LogManager} and {@link org.jboss.logmanager.LogManager}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class JBossLogManagerFacade {

    public static final String LOG4J_ROOT_NAME = "root";
    public static final String JBL_ROOT_NAME = "";

    private static final AttachmentKey<Logger> LOGGER_KEY = new AttachmentKey<Logger>();
    private static final AttachmentKey<Hierarchy> HIERARCHY_KEY = new AttachmentKey<Hierarchy>();
    private static final AttachmentKey<LoggerNode> LOGGER_NODE_KEY = new AttachmentKey<LoggerNode>();

    private JBossLogManagerFacade() {
    }

    /**
     * Returns a JBoss Log Manger logger.
     *
     * @param name the name of the logger.
     *
     * @return a logger.
     */
    static org.jboss.logmanager.Logger getJBossLogger(final LogContext logContext, final String name) {
        final String loggerName = (name == null || name.equals(LOG4J_ROOT_NAME)) ? JBL_ROOT_NAME : name;
        return logContext.getLogger(loggerName);
    }

    /**
     * Gets the log4j logger repository for the log context.
     *
     * @param logContext the log context which the log4j repository is located on or should be created on
     *
     * @return the log4j logger repository
     */
    public static LoggerRepository getLoggerRepository(LogContext logContext) {
        final org.jboss.logmanager.Logger jbossRoot = getJBossLogger(logContext, JBL_ROOT_NAME);
        Hierarchy hierarchy = jbossRoot.getAttachment(HIERARCHY_KEY);
        if (hierarchy == null) {
            // Always attach the root logger
            Logger root = getLogger(jbossRoot);
            if (root == null) {
                root = new RootLogger(JBossLevelMapping.getPriorityFor(jbossRoot.getLevel()));
                final Logger appearing = jbossRoot.attachIfAbsent(LOGGER_KEY, root);
                if (appearing != null) {
                    root = appearing;
                }
            }
            hierarchy = new Hierarchy(root);
            final Hierarchy appearing = jbossRoot.attachIfAbsent(HIERARCHY_KEY, hierarchy);
            if (appearing != null) {
                hierarchy = appearing;
            }
        }
        return hierarchy;
    }

    /**
     * Checks the log context for an attached logger. If the logger is found it is returned, otherwise {@code null} is
     * returned.
     *
     * @param name the name of the logger to check.
     *
     * @return the logger or {@code null} if the logger does not exist.
     */
    static Logger exists(String name) {
        final org.jboss.logmanager.Logger logger = LogContext.getLogContext().getLoggerIfExists(name);
        return logger == null ? null : getLogger(logger);
    }

    /**
     * Gets the log4j logger that is attached to the JBoss Log Manager logger. If the logger does not exist, {@code
     * null} is returned.
     *
     * @param lmLogger the JBoss Log Manager logger.
     *
     * @return the logger or {@code null} if no logger is attached.
     */
    static Logger getLogger(org.jboss.logmanager.Logger lmLogger) {
        return lmLogger.getAttachment(LOGGER_KEY);
    }

    /**
     * Gets the logger or creates the logger via the .
     *
     * @param repository the repository the logger should be set to use.
     * @param name       the name of the logger.
     * @param factory    the factory to create the logger if it does not exist.
     *
     * @return the logger.
     */
    static Logger getOrCreateLogger(final LoggerRepository repository, final String name, final LoggerFactory factory) {
        final org.jboss.logmanager.Logger lmLogger = getJBossLogger(LogContext.getLogContext(), name);
        Logger logger = getLogger(lmLogger);
        if (logger == null) {
            logger = factory.makeNewLoggerInstance(name);
            final Logger currentLogger = lmLogger.attachIfAbsent(LOGGER_KEY, logger);
            if (currentLogger != null) {
                logger = currentLogger;
            }
            final LoggerNode loggerNode = lmLogger.detach(LOGGER_NODE_KEY);
            if (loggerNode != null) {
                updateChildren(loggerNode, logger);
            }
            updateParents(repository, logger, lmLogger);
        }
        return logger;
    }

    /**
     * Returns a collection of the loggers that exist.
     *
     * @return a collection of the loggers.
     */
    static Collection<Logger> getLoggers() {
        final LogContext logContext = LogContext.getLogContext();
        final List<String> loggerNames = logContext.getLoggingMXBean().getLoggerNames();
        final List<Logger> currentLoggers = new ArrayList<Logger>(loggerNames.size());
        for (String name : loggerNames) {
            final org.jboss.logmanager.Logger lmLogger = logContext.getLoggerIfExists(name);
            if (lmLogger != null) {
                final Logger logger = getLogger(lmLogger);
                if (logger != null) {
                    currentLoggers.add(logger);
                }
            }
        }
        return currentLoggers;
    }

    /**
     * This method is not thread safe.
     */
    @SuppressWarnings("unchecked")
    private static void updateParents(final LoggerRepository repository, final Logger cat, final org.jboss.logmanager.Logger parentLogger) {
        final LogContext logContext = parentLogger.getLogContext();
        final String name = cat.getName();
        int length = name.length();
        boolean addRootAsParent = true;
        // if name = "w.x.y.z", loop through "w.x.y", "w.x" and "w", but not "w.x.y.z"
        for (int i = name.lastIndexOf('.', length - 1); i >= 0; i = name.lastIndexOf('.', i - 1)) {
            final String loggerName = name.substring(0, i);
            final org.jboss.logmanager.Logger lmLogger = logContext.getLoggerIfExists(loggerName);
            // This should never be null, but we'll be safe
            if (lmLogger != null) {
                cat.parent = getLogger(lmLogger);
                if (cat.parent == null) {
                    final LoggerNode loggerNode = lmLogger.getAttachment(LOGGER_NODE_KEY);
                    if (loggerNode == null) {
                        final LoggerNode appearing = lmLogger.attachIfAbsent(LOGGER_NODE_KEY, new LoggerNode(cat));
                        if (appearing != null) {
                            appearing.add(cat);
                        }
                    } else {
                        loggerNode.add(cat);
                    }
                } else {
                    addRootAsParent = false;
                    break;
                }
            }
        }
        // If we could not find any existing parents, then link with root.
        if (addRootAsParent) cat.parent = repository.getRootLogger();
    }

    private static void updateChildren(final LoggerNode loggerNode, final Logger logger) {

        for (Logger l : loggerNode) {
            // Note below was taken directly from log4j

            // Unless this child already points to a correct (lower) parent,
            // make cat.parent point to l.parent and l.parent to cat.
            if (!l.parent.getName().startsWith(logger.getName())) {
                logger.parent = l.parent;
                l.parent = logger;
            }
        }
    }

    private static class LoggerNode implements Iterable<Logger> {
        private final Set<Logger> loggers;

        public LoggerNode(final Logger logger) {
            loggers = new ConcurrentSkipListSet<Logger>(LoggerComparator.INSTANCE);
            loggers.add(logger);
        }

        public void add(final Logger logger) {
            loggers.add(logger);
        }

        @Override
        public Iterator<Logger> iterator() {
            return new LinkedHashSet<Logger>(loggers).iterator();
        }
    }

    private static class LoggerComparator implements Comparator<Logger> {
        static final LoggerComparator INSTANCE = new LoggerComparator();

        @Override
        public int compare(final Logger o1, final Logger o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
