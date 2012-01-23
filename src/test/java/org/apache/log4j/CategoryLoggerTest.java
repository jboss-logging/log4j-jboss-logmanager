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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setUp() {
        // Uncomment to see output
        BasicConfigurator.configure();
    }

    @Before
    public void clearAppenders() {
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

        final String stringFormat = "This is a %s format test.";
        org.jboss.logging.Logger.getLogger(getClass()).infof(stringFormat, "string");

        final String msgFormat = "This is a {} format test.";
        org.jboss.logging.Logger.getLogger(getClass()).infof(stringFormat, "message");

        assertEquals(5, appender.counter);
        assertEquals(5, stringAppender.messages.size());
        assertEquals(MESSAGE, stringAppender.messages.get(0));
        assertEquals(JBL_MESSAGE, stringAppender.messages.get(1));
        assertEquals(JUL_MESSAGE, stringAppender.messages.get(2));
        assertFalse(stringFormat.equals(stringAppender.messages.get(3)));
        assertFalse(msgFormat.equals(stringAppender.messages.get(4)));
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

        assertEquals(3, appender.counter);
        assertEquals(5, stringAppender.messages.size());
        assertEquals(ROOT_LOGGER_MSG, stringAppender.messages.get(0));
        assertEquals(MESSAGE, stringAppender.messages.get(1));
        assertEquals(MESSAGE, stringAppender.messages.get(2));
        assertEquals(JBL_MESSAGE, stringAppender.messages.get(3));
        assertEquals(JBL_MESSAGE, stringAppender.messages.get(4));
    }

    @Test
    public void rootAppenderTest() {
        final CountingAppender appender = new CountingAppender();
        final StringAppender stringAppender = new StringAppender();

        root.addAppender(appender);
        root.addAppender(stringAppender);
        root.info(ROOT_LOGGER_MSG);

        final String msg = JBL_MESSAGE + " root logger";
        org.jboss.logmanager.Logger.getLogger(LogManagerFacade.JBL_ROOT_NAME).info(msg);
        org.jboss.logging.Logger.getLogger(LogManagerFacade.JBL_ROOT_NAME).info(msg);

        assertEquals(3, appender.counter);
        assertEquals(3, stringAppender.messages.size());
        assertEquals(ROOT_LOGGER_MSG, stringAppender.messages.get(0));
        assertEquals(msg, stringAppender.messages.get(1));
        assertEquals(msg, stringAppender.messages.get(2));
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
