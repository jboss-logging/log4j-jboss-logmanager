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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
class JBossLogManagerFacade {

    public static final String LOG4J_ROOT_NAME = "root";
    public static final String JBL_ROOT_NAME = "";

    private static final AttachmentKey<Logger> LOGGER_KEY = new AttachmentKey<Logger>();
    private static final AttachmentKey<Hierarchy> HIERARCHY_KEY = new AttachmentKey<Hierarchy>();

    private JBossLogManagerFacade() {
    }

    /**
     * Returns a JBoss Log Manger logger.
     *
     * @param name the name of the logger.
     *
     * @return a logger.
     */
    public static org.jboss.logmanager.Logger getJbossLogger(final String name) {
        final String loggerName = (name == null || name.equals(LOG4J_ROOT_NAME)) ? JBL_ROOT_NAME : name;
        org.jboss.logmanager.Logger result = LogContext.getLogContext().getLogger(loggerName);
        JBossAppenderHandler.createAndAttach(result);
        return result;
    }

    /**
     * Locates the log4j logger repository.
     *
     * @return the log4j logger repository.
     */
    public static LoggerRepository getLoggerRepository() {
        final org.jboss.logmanager.Logger jbossRoot = getJbossRootLogger();
        Hierarchy result;
        synchronized (HIERARCHY_KEY) {
            result = jbossRoot.getAttachment(HIERARCHY_KEY);
            if (result == null) {
                // Always attach the root logger
                Logger root;
                synchronized (LOGGER_KEY) {
                    root = jbossRoot.getAttachment(LOGGER_KEY);
                    if (root == null) {
                        root = new RootLogger(JBossLevelMapping.DEFAULT_LOG4J_LEVEL);
                        jbossRoot.attach(LOGGER_KEY, root);
                    }
                }
                result = new Hierarchy(root);
                final Hierarchy current = jbossRoot.attachIfAbsent(HIERARCHY_KEY, result);
                if (current != null) {
                    result = current;
                }
            }
        }
        return result;
    }

    /**
     * Returns the root JBoss logger from the JBoss log manager.
     *
     * @return the root logger.
     */
    public static org.jboss.logmanager.Logger getJbossRootLogger() {
        return getJbossLogger(JBL_ROOT_NAME);
    }

    /**
     * Checks the log context for an attached logger. If the logger is found it is returned, otherwise {@code null} is
     * returned.
     *
     * @param name the name of the logger to check.
     *
     * @return the logger or {@code null} if the logger does not exist.
     */
    public static Logger exists(String name) {
        final org.jboss.logmanager.Logger logger = LogContext.getLogContext().getLoggerIfExists(name);
        return logger == null ? null : logger.getAttachment(LOGGER_KEY);
    }

    /**
     * Gets the log4j logger that is attached to the JBoss Log Manager logger. If the logger does not exist, {@code
     * null} is returned.
     *
     * @param lmLogger the JBoss Log Manager logger.
     *
     * @return the logger or {@code null} if no logger is attached.
     */
    public static Logger getLogger(org.jboss.logmanager.Logger lmLogger) {
        return lmLogger.getAttachment(LOGGER_KEY);
    }

    /**
     * Gets the logger.
     *
     * @param repository the repository the logger should be set to use.
     * @param name       the name of the logger.
     * @param factory    the factory to create the logger if it does not exist.
     *
     * @return the logger.
     */
    public static Logger getLogger(final LoggerRepository repository, final String name, final LoggerFactory factory) {
        final org.jboss.logmanager.Logger lmLogger = getJbossLogger(name);
        Logger logger = lmLogger.getAttachment(LOGGER_KEY);
        if (logger == null) {
            synchronized (LOGGER_KEY) {
                logger = factory.makeNewLoggerInstance(name);
                final Logger currentLogger = lmLogger.attachIfAbsent(LOGGER_KEY, logger);
                if (currentLogger != null) {
                    logger = currentLogger;
                }
                updateParents(repository, logger);
            }
        }
        return logger;
    }

    /**
     * Returns a collection of the loggers that exist.
     *
     * @return a collection of the loggers.
     */
    public static Collection<Logger> getLoggers() {
        final LogContext context = LogContext.getLogContext();
        final List<String> currentLoggerNames = context.getLoggingMXBean().getLoggerNames();
        final List<Logger> currentLoggers = new ArrayList<Logger>(currentLoggerNames.size());
        for (String name : currentLoggerNames) {
            final org.jboss.logmanager.Logger lmLogger = context.getLoggerIfExists(name);
            if (lmLogger != null) {
                final Logger logger = lmLogger.getAttachment(LOGGER_KEY);
                if (logger != null) {
                    currentLoggers.add(logger);
                }
            }
        }
        return currentLoggers;
    }

    /**
     * Returns a collection of the loggers that exist.
     *
     * @return a collection of the loggers.
     */
    public static Collection<org.jboss.logmanager.Logger> getJbossLoggers() {
        final LogContext context = LogContext.getLogContext();
        final List<String> currentLoggerNames = context.getLoggingMXBean().getLoggerNames();
        final List<org.jboss.logmanager.Logger> currentLoggers = new ArrayList<org.jboss.logmanager.Logger>(currentLoggerNames.size());
        for (String name : currentLoggerNames) {
            final org.jboss.logmanager.Logger lmLogger = context.getLoggerIfExists(name);
            if (lmLogger != null) {
                currentLoggers.add(lmLogger);
            }
        }
        return currentLoggers;
    }


    private static void updateParents(final LoggerRepository repository, final Logger cat) {
        String name = cat.getName();
        int length = name.length();
        boolean addRootAsParent = true;
        // if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not "w.x.y.z"
        for (int i = name.lastIndexOf('.', length - 1); i >= 0; i = name.lastIndexOf('.', i - 1)) {
            final org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLoggerIfExists(name.substring(0, i));
            if (lmLogger != null) {
                cat.parent = lmLogger.getAttachment(LOGGER_KEY);
                if (cat.parent != null) {
                    addRootAsParent = false;
                    break;
                }
            }
        }
        // If we could not find any existing parents, then link with root.
        if (addRootAsParent) cat.parent = repository.getRootLogger();
    }
}
