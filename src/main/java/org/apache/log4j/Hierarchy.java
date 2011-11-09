/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.apache.log4j;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jboss.logmanager.LogContext;

import static org.jboss.logmanager.Logger.AttachmentKey;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;

/**
 * Our replacement for the log4j {@code Hierarchy} class.  We redirect management of the hierarchy
 * completely to the logmanager's log context.
 */
public class Hierarchy implements LoggerRepository, RendererSupport, ThrowableRendererSupport {

    private final AttachmentKey<Logger> loggerKey = new AttachmentKey<Logger>();

    private LoggerFactory defaultFactory;
    private Set<HierarchyEventListener> listeners;
    private volatile Logger root;
    private final LogContext context;

    /**
     * Create a new logger hierarchy.
     *
     * @param root The root of the new hierarchy.
     */
    public Hierarchy(Logger root) {
        listeners = new CopyOnWriteArraySet<HierarchyEventListener>();
        context = LogContext.getLogContext();
        this.root = root;
        this.root.setHierarchy(this);
        defaultFactory = new DefaultCategoryFactory();
    }

    public void addRenderer(Class classToRender, ObjectRenderer or) {
    }

    public void addHierarchyEventListener(HierarchyEventListener listener) {
        if (! listeners.add(listener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        }
    }

    public void clear() {
    }

    public void emitNoAppenderWarning(Category cat) {
    }

    public Logger exists(String name) {
        final org.jboss.logmanager.Logger logger = context.getLoggerIfExists(name);
        return logger == null ? null : logger.getAttachment(loggerKey);
    }

    public void setThreshold(String levelStr) {
    }

    public void setThreshold(Level l) {
    }

    public void fireAddAppenderEvent(Category logger, Appender appender) {
        for (HierarchyEventListener listener : listeners) {
            listener.addAppenderEvent(logger, appender);
        }
    }

    void fireRemoveAppenderEvent(Category logger, Appender appender) {
        for (HierarchyEventListener listener : listeners) {
            listener.removeAppenderEvent(logger, appender);
        }
    }

    public Level getThreshold() {
        return Level.ALL;
    }

    public Logger getLogger(String name) {
        return getLogger(name, defaultFactory);
    }

    public Logger getLogger(final String name, LoggerFactory factory) {
        final org.jboss.logmanager.Logger lmLogger = LogContext.getLogContext().getLogger(name);
        Logger logger = lmLogger.getAttachment(loggerKey);
        if (logger != null) {
            return logger;
        }
        logger = factory.makeNewLoggerInstance(name);
        logger.setHierarchy(this);
        final Logger appearingLogger = lmLogger.attachIfAbsent(loggerKey, logger);
        return appearingLogger != null ? appearingLogger : logger;
    }

    public Enumeration getCurrentLoggers() {
        return Collections.enumeration(Collections.emptySet());
    }

    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }

    public RendererMap getRendererMap() {
        return null;
    }

    public Logger getRootLogger() {
        return root;
    }

    public boolean isDisabled(int level) {
        return false;
    }

    public void overrideAsNeeded(String override) {
    }

    public void resetConfiguration() {
    }

    public void setDisableOverride(String override) {
    }

    public void setRenderer(Class renderedClass, ObjectRenderer renderer) {
    }

    public void setThrowableRenderer(final ThrowableRenderer renderer) {
    }

    public ThrowableRenderer getThrowableRenderer() {
        return null;
    }

    public void shutdown() {
    }
}


