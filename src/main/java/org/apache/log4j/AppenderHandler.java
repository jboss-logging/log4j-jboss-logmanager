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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Formatter;

import org.apache.log4j.spi.LoggingEvent;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.LogContext;

/**
 * A handler that can be assigned {@link org.jboss.logmanager.Logger} that wraps an {@link Appender appender}.
 * <p/>
 * {@link Appender appenders} are executed for log messages that come through the JBoss Log Manager.
 * <p/>
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AppenderHandler extends ExtHandler {

    public static final String LOG4J_ROOT_NAME = "root";
    public static final String JBL_ROOT_NAME = "";

    private final Category category;
    private final String name;

    private AppenderHandler(final Category category, final String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * Creates a single handler and attaches it to a the {@link org.jboss.logmanager.Logger root logger}.
     * <p/>
     * Each time the handler is invoked via the logger all {@link Appender appenders} are processed and invoked on the
     * {@link org.apache.log4j.spi.RootLogger log4j root logger}.
     * <p/>
     * <b>Note</b> This is safe to invoke from the constructor of a {@link Category}. No methods are invoked during the
     * creation.
     *
     * @param root the log4j root logger.
     */
    public static void createAndAttach(final Category root) {
        createAndAttach(root, JBL_ROOT_NAME);
    }

    /**
     * Creates a single handler and attaches it to a the a {@link org.jboss.logmanager.Logger}.
     * <p/>
     * Each time the handler is invoked via the logger all {@link Appender appenders} are processed and invoked.
     * <p/>
     * <b>Note</b> This is safe to invoke from the constructor of a {@link Category}. No methods are invoked during the
     * creation.
     *
     * @param category the log4j logger.
     * @param name     the name of the logger.
     */
    public static void createAndAttach(final Category category, final String name) {
        final AppenderHandler handler = new AppenderHandler(category, name);
        LogContext.getLogContext().getLogger(name).addHandler(handler);
    }


    @Override
    public void setFormatter(final Formatter newFormatter) throws SecurityException {
        super.setFormatter(newFormatter);
        final FormatterLayout layout = new FormatterLayout(newFormatter);
        synchronized (category) {
            for (Appender appender : AppenderIterator.of(category)) {
                appender.setLayout(layout);
            }
        }
    }

    @Override
    protected void doPublish(final ExtLogRecord record) {
        String loggerName = record.getLoggerName();
        if (loggerName == null || JBL_ROOT_NAME.equals(loggerName)) {
            loggerName = LOG4J_ROOT_NAME;
        }
        synchronized (this.category) {
            if (loggerName.equals(category.getName())) {
                final LoggingEvent event = new LoggingEventWrapper(record, category);
                for (Appender appender : AppenderIterator.of(category)) {
                    if (new FilterWrapper(appender.getFilter(), true).isLoggable(record)) {
                        appender.doAppend(event);
                    }
                }
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        checkAccess();
        synchronized (category) {
            category.closeNestedAppenders();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = prime * hash + (name == null ? 0 : name.hashCode());
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AppenderHandler)) {
            return false;
        }
        final AppenderHandler other = (AppenderHandler) obj;
        return (this.name == null ? other.name == null : (this.name.equals(other.name)));
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getClass()).append("{").append(name).append("}").toString();
    }

    /**
     * Convenience class to use enhanced loops for the appenders of the a {@link Category}.
     */
    private static class AppenderIterator implements Iterable<Appender> {

        private final List<Appender> appenders;

        private AppenderIterator(final List<Appender> appenders) {
            this.appenders = appenders;
        }

        static AppenderIterator of(final Category category) {
            return new AppenderIterator(initAppenders(category));
        }

        private synchronized static List<Appender> initAppenders(final Category category) {
            final List<Appender> result = new ArrayList<Appender>();
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> e = (Enumeration<Appender>) category.getAllAppenders();
            if (e != null) {
                while (e.hasMoreElements()) {
                    result.addAll(Collections.list(e));
                }
            }
            if (category.getParent() != null) {
                result.addAll(initAppenders(category.getParent()));
            }
            return result;
        }

        @Override
        public Iterator<Appender> iterator() {
            return Collections.unmodifiableCollection(appenders).iterator();
        }
    }
}
