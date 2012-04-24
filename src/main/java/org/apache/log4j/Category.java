/*
 * Modifications by Red Hat, Inc.
 *
 * This file incorporates work covered by the following notice(s):
 *
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

/**
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 */

package org.apache.log4j;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class Category implements AppenderAttachable {
    private static final Object LEVEL_LOCK = new Object();

    private static final String FQCN = Category.class.getName();

    protected volatile Level level;

    protected volatile Category parent;

    final org.jboss.logmanager.Logger jblmLogger;

    protected Category(String name) {
        jblmLogger = JBossLogManagerFacade.getJBossLogger(name);
    }

    public void addAppender(Appender newAppender) {
        JBossAppenderHandler.attachAppender(jblmLogger, newAppender);
        JBossLogManagerFacade.getLoggerRepository().fireAddAppenderEvent(this, newAppender);
    }

    public void assertLog(boolean assertion, String msg) {
        if (!assertion) error(msg);
    }

    public void callAppenders(LoggingEvent event) {
        jblmLogger.logRaw(event.getLogRecord());
    }

    org.jboss.logmanager.Logger getJBossLogger() {
        return jblmLogger;
    }

    void closeNestedAppenders() {
        JBossAppenderHandler.closeAppenders(jblmLogger);
    }

    public void debug(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.DEBUG)) {
            forcedLog(FQCN, Level.DEBUG, message, null);
        }
    }

    public void debug(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.DEBUG)) {
            forcedLog(FQCN, Level.DEBUG, message, t);
        }
    }

    public void error(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.ERROR)) {
            forcedLog(FQCN, Level.ERROR, message, null);
        }
    }

    public void error(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.ERROR)) {
            forcedLog(FQCN, Level.ERROR, message, t);
        }
    }

    public static Logger exists(String name) {
        return LogManager.exists(name);
    }

    public void fatal(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.FATAL)) {
            forcedLog(FQCN, Level.FATAL, message, null);
        }
    }

    public void fatal(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.FATAL)) {
            forcedLog(FQCN, Level.FATAL, message, t);
        }
    }

    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        callAppenders(new LoggingEvent(fqcn, this, level, message, t));
    }

    public boolean getAdditivity() {
        return jblmLogger.getUseParentHandlers();
    }

    public Enumeration getAllAppenders() {
        final List<Appender> appenders = JBossAppenderHandler.getAppenders(jblmLogger);
        if (appenders.isEmpty()) {
            return NullEnumeration.getInstance();
        }
        return Collections.enumeration(appenders);
    }

    public Appender getAppender(String name) {
        return JBossAppenderHandler.getAppender(jblmLogger, name);
    }

    public Level getEffectiveLevel() {
        return JBossLevelMapping.getPriorityFor(jblmLogger.getEffectiveLevel());
    }

    @Deprecated
    public Priority getChainedPriority() {
        return getEffectiveLevel();
    }

    public static Enumeration getCurrentCategories() {
        return LogManager.getCurrentLoggers();
    }

    @Deprecated
    public static LoggerRepository getDefaultHierarchy() {
        return LogManager.getLoggerRepository();
    }

    @Deprecated
    public LoggerRepository getHierarchy() {
        return JBossLogManagerFacade.getLoggerRepository();
    }

    public LoggerRepository getLoggerRepository() {
        return JBossLogManagerFacade.getLoggerRepository();
    }

    @Deprecated
    public static Category getInstance(String name) {
        return LogManager.getLogger(name);
    }

    @Deprecated
    public static Category getInstance(Class clazz) {
        return LogManager.getLogger(clazz);
    }

    public final String getName() {
        return jblmLogger.getName();
    }

    public final Category getParent() {
        return parent;
    }

    public final Level getLevel() {
        synchronized (LEVEL_LOCK) {
            if (level != null) {
                final Level currentLevel = JBossLevelMapping.getPriorityFor(jblmLogger.getLevel());
                if (currentLevel.toInt() != level.toInt()) {
                    jblmLogger.setLevel(JBossLevelMapping.getLevelFor(level));
                }
            }
        }
        return level;
    }

    @Deprecated
    public final Level getPriority() {
        return getLevel();
    }

    @Deprecated
    public static Category getRoot() {
        return LogManager.getRootLogger();
    }

    public ResourceBundle getResourceBundle() {
        return jblmLogger.getResourceBundle();
    }

    protected String getResourceBundleString(String key) {
        ResourceBundle rb = getResourceBundle();
        // This is one of the rare cases where we can use logging in order
        // to report errors from within log4j.
        if (rb == null) {
            return null;
        } else {
            try {
                return rb.getString(key);
            } catch (MissingResourceException mre) {
                error("No resource is associated with key \"" + key + "\".");
                return null;
            }
        }
    }

    public void info(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.INFO)) {
            forcedLog(FQCN, Level.INFO, message, null);
        }
    }

    public void info(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.INFO)) {
            forcedLog(FQCN, Level.INFO, message, t);
        }
    }

    public boolean isAttached(Appender appender) {
        return JBossAppenderHandler.isAppenderAttached(jblmLogger, appender);
    }

    public boolean isDebugEnabled() {
        return jblmLogger.isLoggable(org.jboss.logmanager.Level.DEBUG);
    }

    public boolean isEnabledFor(Priority level) {
        return jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(level));
    }

    public boolean isInfoEnabled() {
        return jblmLogger.isLoggable(org.jboss.logmanager.Level.INFO);
    }

    public void l7dlog(Priority priority, String key, Throwable t) {
        if (jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(priority))) {
            String msg = getResourceBundleString(key);
            // if message corresponding to 'key' could not be found in the
            // resource bundle, then default to 'key'.
            if (msg == null) {
                msg = key;
            }
            forcedLog(FQCN, priority, msg, t);
        }
    }

    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        if (jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(priority))) {
            String pattern = getResourceBundleString(key);
            String msg;
            if (pattern == null) msg = key;
            else msg = java.text.MessageFormat.format(pattern, params);
            forcedLog(FQCN, priority, msg, t);
        }
    }

    public void log(Priority priority, Object message, Throwable t) {
        if (jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(priority))) {
            forcedLog(FQCN, priority, message, t);
        }
    }

    public void log(Priority priority, Object message) {
        if (jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(priority))) {
            forcedLog(FQCN, priority, message, null);
        }
    }

    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
        if (jblmLogger.isLoggable(JBossLevelMapping.getLevelFor(level))) {
            forcedLog(callerFQCN, level, message, t);
        }
    }

    private void fireRemoveAppenderEvent(final LoggerRepository repository, final Appender appender) {
        if (appender != null) {
            if (repository instanceof Hierarchy) {
                ((Hierarchy) repository).fireRemoveAppenderEvent(this, appender);
            } else if (repository instanceof HierarchyEventListener) {
                ((HierarchyEventListener) repository).removeAppenderEvent(this, appender);
            }
        }
    }

    public void removeAllAppenders() {
        final List<Appender> removedAppenders = JBossAppenderHandler.removeAllAppenders(jblmLogger);
        final LoggerRepository repository = JBossLogManagerFacade.getLoggerRepository();
        for (Appender appender : removedAppenders) {
            fireRemoveAppenderEvent(repository, appender);
        }
    }

    public void removeAppender(Appender appender) {
        if (appender != null) {
            if (JBossAppenderHandler.removeAppender(jblmLogger, appender)) {
                fireRemoveAppenderEvent(JBossLogManagerFacade.getLoggerRepository(), appender);
            }
        }
    }

    public void removeAppender(String name) {
        if (name != null) {
            removeAppender(JBossAppenderHandler.getAppender(jblmLogger, name));
        }
    }

    public void setAdditivity(boolean additive) {
        jblmLogger.setUseParentHandlers(additive);
    }

    @SuppressWarnings("unused")
    final void setHierarchy(LoggerRepository repository) {
        // no-op
    }

    public void setLevel(Level level) {
        synchronized (LEVEL_LOCK) {
            jblmLogger.setLevel(JBossLevelMapping.getLevelFor(level));
            this.level = level;
        }
    }

    public void setPriority(Priority priority) {
        setLevel((Level) priority);
    }

    public void setResourceBundle(ResourceBundle bundle) {
        // no-op
    }

    public static void shutdown() {
        LogManager.shutdown();
    }

    public void warn(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.WARN)) {
            forcedLog(FQCN, Level.WARN, message, null);
        }
    }

    public void warn(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.WARN)) {
            forcedLog(FQCN, Level.WARN, message, t);
        }
    }
}
