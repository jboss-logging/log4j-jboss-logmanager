/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 01.12.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class CategoryLoggerTest {
    private static final String MESSAGE = "This is a test log message.";
    private static final String JBL_MESSAGE = "This is a test from JBL";
    private static final String JUL_MESSAGE = "This is a test from JUL";
    private static final String ROOT_LOGGER_MSG = "Message from root logger";
    private final Logger log4jLogger = Logger.getLogger(CategoryLoggerTest.class);
    private final Logger root = Logger.getRootLogger();

    @Before
    public void setUp() {
        // Uncomment to see output
        BasicConfigurator.configure();
    }

    @After
    public void cleanUp() {
        log4jLogger.removeAllAppenders();
        root.removeAllAppenders();
    }

    @Test
    public void appenderTest() {
        final CountingAppender appender = new CountingAppender();
        final StringAppender stringAppender = new StringAppender();

        log4jLogger.addAppender(appender);
        log4jLogger.addAppender(stringAppender);
        log4jLogger.info(MESSAGE);

        org.jboss.logging.Logger.getLogger(getClass()).info(JBL_MESSAGE);
        java.util.logging.Logger.getLogger(getClass().getName()).info(JUL_MESSAGE);

        assertEquals(appender.counter, 3);
        assertEquals(stringAppender.messages.size(), 3);
        assertEquals(stringAppender.messages.get(0), MESSAGE);
        assertEquals(stringAppender.messages.get(1), JBL_MESSAGE);
        assertEquals(stringAppender.messages.get(2), JUL_MESSAGE);
    }

    @Test
    public void duplicateAppenderTest() {
        final CountingAppender appender = new CountingAppender();
        final StringAppender stringAppender = new StringAppender();

        root.addAppender(appender);
        root.addAppender(stringAppender);
        root.info(ROOT_LOGGER_MSG);

        log4jLogger.addAppender(stringAppender);
        log4jLogger.info(MESSAGE);

        org.jboss.logging.Logger.getLogger(getClass()).info(JBL_MESSAGE);

        assertEquals(appender.counter, 3);
        assertEquals(stringAppender.messages.size(), 5);
        assertEquals(stringAppender.messages.get(0), ROOT_LOGGER_MSG);
        assertEquals(stringAppender.messages.get(1), MESSAGE);
        assertEquals(stringAppender.messages.get(2), MESSAGE);
        assertEquals(stringAppender.messages.get(3), JBL_MESSAGE);
        assertEquals(stringAppender.messages.get(4), JBL_MESSAGE);
    }

    @Test
    public void rootAppenderTest() {
        final CountingAppender appender = new CountingAppender();
        final StringAppender stringAppender = new StringAppender();

        root.addAppender(appender);
        root.addAppender(stringAppender);
        root.info(ROOT_LOGGER_MSG);

        final String msg = JBL_MESSAGE + " root logger";
        org.jboss.logmanager.Logger.getLogger(AppenderHandler.JBL_ROOT_NAME).info(msg);
        org.jboss.logging.Logger.getLogger(AppenderHandler.JBL_ROOT_NAME).info(msg);

        assertEquals(appender.counter, 3);
        assertEquals(stringAppender.messages.size(), 3);
        assertEquals(stringAppender.messages.get(0), ROOT_LOGGER_MSG);
        assertEquals(stringAppender.messages.get(1), msg);
        assertEquals(stringAppender.messages.get(2), msg);
    }

    private static class CountingAppender extends AbstractAppender {

        int counter;

        CountingAppender() {
            super("CountingAppender");
            counter = 0;
        }

        public void close() {
        }

        public void append(LoggingEvent event) {
            counter++;
        }

        public boolean requiresLayout() {
            return true;
        }
    }

    private static class StringAppender extends AbstractAppender {

        private final List<String> messages = new ArrayList<String>();

        StringAppender() {
            super("StringAppender");
        }

        public void close() {
        }

        public void append(LoggingEvent event) {
            messages.add(event.getRenderedMessage());
        }

        public boolean requiresLayout() {
            return true;
        }
    }
}
