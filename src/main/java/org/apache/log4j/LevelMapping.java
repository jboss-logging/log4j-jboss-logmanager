/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import java.util.HashMap;
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
class LevelMapping {

    private static final Map<java.util.logging.Level, Level> priorityMap;

    private LevelMapping() {
    }

    static {
        final Map<java.util.logging.Level, Level> map = new IdentityHashMap<java.util.logging.Level, Level>();
        map.put(java.util.logging.Level.SEVERE, Log4jJDKLevel.SEVERE);
        map.put(java.util.logging.Level.WARNING, Log4jJDKLevel.WARNING);
        map.put(java.util.logging.Level.CONFIG, Log4jJDKLevel.CONFIG);
        map.put(java.util.logging.Level.INFO, Log4jJDKLevel.INFO);
        map.put(java.util.logging.Level.FINE, Log4jJDKLevel.FINE);
        map.put(java.util.logging.Level.FINER, Log4jJDKLevel.FINER);
        map.put(java.util.logging.Level.FINEST, Log4jJDKLevel.FINEST);

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
    static Level getPriorityFor(java.util.logging.Level level) {
        final Level p;
        return (p = priorityMap.get(level)) == null ? Level.DEBUG : p;
    }

    /**
     * Finds the {@link org.jboss.logmanager.Level jboss-log-manager level} for the log4j priority.
     *
     * @param level the log4j level/priority.
     *
     * @return the jboss-log-manager level or by default {@link org.jboss.logmanager.Level#DEBUG}.
     */
    static org.jboss.logmanager.Level getLevelFor(Priority level) {
        switch (level.toInt()) {
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
            default:
                return org.jboss.logmanager.Level.DEBUG;
        }
    }

    private static class Log4jJDKLevel extends Level {

        private static final long serialVersionUID = -2456662804627419121L;

        /**
         * Instantiate a Level object.
         */
        protected Log4jJDKLevel(int level, String levelStr, int syslogEquivalent) {
            super(level, levelStr, syslogEquivalent);
        }

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#SEVERE SEVERE} level; numerically
         * equivalent to log4j's {@link Level#ERROR ERROR} level.
         */
        public static final Level SEVERE = new Log4jJDKLevel(Level.ERROR_INT, "SEVERE", 3);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#WARNING WARNING} level; numerically
         * equivalent to log4j's {@link Level#WARN WARN} level.
         */
        public static final Level WARNING = new Log4jJDKLevel(Level.WARN_INT, "WARNING", 4);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#INFO INFO} level; numerically
         * equivalent to log4j's {@link Level#INFO INFO} level.
         */
        public static final Level INFO = new Log4jJDKLevel(Level.INFO_INT, "INFO", 5);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#CONFIG CONFIG} level; numerically
         * falls between log4j's {@link Level#INFO INFO} and {@link Level#DEBUG DEBUG} levels.
         */
        public static final Level CONFIG = new Log4jJDKLevel(Level.INFO_INT - 5000, "CONFIG", 6);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#FINE FINE} level; numerically
         * equivalent to log4j's {@link Level#DEBUG DEBUG} level.
         */
        public static final Level FINE = new Log4jJDKLevel(Level.DEBUG_INT, "FINE", 7);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#FINER FINER} level; numerically
         * falls between log4j's {@link Level#DEBUG DEBUG} and {@link Level#TRACE TRACE} levels.
         */
        public static final Level FINER = new Log4jJDKLevel(Level.DEBUG_INT - 2500, "FINER", 7);

        /**
         * A mapping of the JDK logging {@link java.util.logging.Level#FINEST FINEST} level; numerically
         * equivalent to log4j's {@link Level#TRACE TRACE} level.
         */
        public static final Level FINEST = new Log4jJDKLevel(Level.TRACE_INT, "FINEST", 7);

        private static final Map<String, Level> levelMapping = new HashMap<String, Level>();

        private static void add(Level lvl) {
            levelMapping.put(lvl.toString(), lvl);
        }

        static {
            add(SEVERE);
            add(WARNING);
            add(INFO);
            add(CONFIG);
            add(FINE);
            add(FINER);
            add(FINEST);
        }

        /**
         * Get the level for the given name. If the level is not one of the levels defined in this class,
         * this method delegates to {@link Level#toLevel(String) toLevel(String)} on the superclass.
         *
         * @param name the level name
         *
         * @return the equivalent level
         */
        public static Level toLevel(String name) {
            final Level level = levelMapping.get(name.trim().toUpperCase());
            return level != null ? level : Level.toLevel(name);
        }
    }
}
