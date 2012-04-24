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
 */

package org.apache.log4j;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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

    private final LoggerFactory defaultFactory;
    private final org.jboss.logmanager.Logger jblmRootLogger;
    private final Set<HierarchyEventListener> listeners;
    private final RendererMap rendererMap;
    private ThrowableRenderer throwableRenderer = null;

    public Hierarchy(Logger root) {
        listeners = new CopyOnWriteArraySet<HierarchyEventListener>();
        jblmRootLogger = JBossLogManagerFacade.getJBossRootLogger();
        jblmRootLogger.setLevel(JBossLevelMapping.getLevelFor(root.getLevel()));
        defaultFactory = new DefaultCategoryFactory();
        rendererMap = new RendererMap();
    }

    public void addRenderer(Class classToRender, ObjectRenderer or) {
        rendererMap.put(classToRender, or);
    }

    @Override
    public void addHierarchyEventListener(HierarchyEventListener listener) {
        if (!listeners.add(listener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        }
    }

    public void clear() {
        // No-op should
    }

    @Override
    public void emitNoAppenderWarning(Category cat) {
        // no-op
    }

    @Override
    public Logger exists(String name) {
        return JBossLogManagerFacade.exists(name);
    }

    @Override
    public void setThreshold(String levelStr) {
        Level l = Level.toLevel(levelStr, null);
        if (l != null) {
            setThreshold(l);
        } else {
            LogLog.warn("Could not convert [" + levelStr + "] to Level.");
        }
    }

    @Override
    public void setThreshold(Level l) {
        if (l != null) {
            JBossLogManagerFacade.getJBossRootLogger().setLevel(JBossLevelMapping.getLevelFor(l));
        }
    }

    @Override
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

    @Override
    public Level getThreshold() {
        return JBossLevelMapping.getPriorityFor(JBossLogManagerFacade.getJBossRootLogger().getLevel());
    }

    @Override
    public Logger getLogger(String name) {
        return getLogger(name, defaultFactory);
    }

    @Override
    public Logger getLogger(final String name, LoggerFactory factory) {
        return JBossLogManagerFacade.getLogger(this, name, factory);
    }

    @Override
    public Enumeration getCurrentLoggers() {
        return Collections.enumeration(JBossLogManagerFacade.getLoggers());
    }

    @Override
    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }

    @Override
    public RendererMap getRendererMap() {
        return rendererMap;
    }

    @Override
    public Logger getRootLogger() {
        return JBossLogManagerFacade.getLogger(jblmRootLogger);
    }

    @Override
    public boolean isDisabled(int level) {
        return JBossLevelMapping.getPriorityFor(JBossLogManagerFacade.getJBossRootLogger().getLevel()).toInt() > level;
    }

    @Deprecated
    public void overrideAsNeeded(String override) {
        // Only logs a warning in log4j implementation
    }

    @Override
    public void resetConfiguration() {
        // no-op
    }

    @Deprecated
    public void setDisableOverride(String override) {
        // Only logs a warning in log4j implementation
    }

    @Override
    public void setRenderer(Class renderedClass, ObjectRenderer renderer) {
        rendererMap.put(renderedClass, renderer);
    }

    @Override
    public void setThrowableRenderer(final ThrowableRenderer renderer) {
        this.throwableRenderer = renderer;
    }

    @Override
    public ThrowableRenderer getThrowableRenderer() {
        return throwableRenderer;
    }

    @Override
    public void shutdown() {
        // no-op
    }
}


