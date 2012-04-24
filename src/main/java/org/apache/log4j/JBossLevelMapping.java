package org.apache.log4j;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A mapping for {@link org.jboss.logmanager.Level jboss-log-manager levels},
 * {@link java.util.logging.Level JUL levels} and {@link Level log4j levels}.
 * <p/>
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class JBossLevelMapping {

    public static final org.jboss.logmanager.Level DEFAULT_LEVEL = org.jboss.logmanager.Level.DEBUG;
    public static final Level DEFAULT_LOG4J_LEVEL = Level.DEBUG;

    private static final Map<java.util.logging.Level, Level> priorityMap;

    private JBossLevelMapping() {
    }

    static {
        final Map<java.util.logging.Level, Level> map = new IdentityHashMap<java.util.logging.Level, Level>();
        map.put(java.util.logging.Level.SEVERE, Level.ERROR);
        map.put(java.util.logging.Level.WARNING, Level.WARN);
        map.put(java.util.logging.Level.CONFIG, Level.DEBUG);
        map.put(java.util.logging.Level.INFO, Level.INFO);
        map.put(java.util.logging.Level.FINE, Level.DEBUG);
        map.put(java.util.logging.Level.FINER, Level.DEBUG);
        map.put(java.util.logging.Level.FINEST, Level.TRACE);

        map.put(org.jboss.logmanager.Level.FATAL, Level.FATAL);
        map.put(org.jboss.logmanager.Level.ERROR, Level.ERROR);
        map.put(org.jboss.logmanager.Level.WARN, Level.WARN);
        map.put(org.jboss.logmanager.Level.INFO, Level.INFO);
        map.put(org.jboss.logmanager.Level.DEBUG, Level.DEBUG);
        map.put(org.jboss.logmanager.Level.TRACE, Level.TRACE);
        priorityMap = map;
    }

    /**
     * Finds the {@link Level log4j level} for the {@code level}.
     *
     * @param level the JUL logging level.
     *
     * @return the log4j logging level or {@code null} if it cold not be found.
     */
    public static Level getPriorityFor(java.util.logging.Level level) {
        final Level p;
        return (p = priorityMap.get(level)) == null ? DEFAULT_LOG4J_LEVEL : p;
    }

    /**
     * Finds the {@link Level log4j level} for the {@code level}.
     *
     * @param level the JUL logging level.
     *
     * @return the log4j logging level or {@code null} if it cold not be found.
     */
    public static Level getPriorityFor(int level) {
        final Level p;
        for (java.util.logging.Level l : priorityMap.keySet()) {
            if (l.intValue() == level) {
                return priorityMap.get(l);
            }
        }
        return DEFAULT_LOG4J_LEVEL;
    }

    /**
     * Finds the {@link org.jboss.logmanager.Level jboss-log-manager level} for the log4j priority.
     *
     * @param level the log4j level/priority.
     *
     * @return the jboss-log-manager level or by default {@link org.jboss.logmanager.Level#DEBUG}.
     */
    public static java.util.logging.Level getLevelFor(Priority level) {
        if (level == null) {
            return DEFAULT_LEVEL;
        }
        switch (level.toInt()) {
            case Level.ALL_INT:
                return org.jboss.logmanager.Level.ALL;
            case Level.TRACE_INT:
                return org.jboss.logmanager.Level.TRACE;
            case Level.DEBUG_INT:
                return org.jboss.logmanager.Level.DEBUG;
            case Level.INFO_INT:
                return org.jboss.logmanager.Level.INFO;
            case Level.WARN_INT:
                return org.jboss.logmanager.Level.WARN;
            case Level.ERROR_INT:
                return org.jboss.logmanager.Level.ERROR;
            case Level.FATAL_INT:
                return org.jboss.logmanager.Level.FATAL;
            case Level.OFF_INT:
                return org.jboss.logmanager.Level.OFF;
            default:
                return DEFAULT_LEVEL;
        }
    }
}
