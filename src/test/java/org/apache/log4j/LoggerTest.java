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

package org.apache.log4j;

import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;
import org.jboss.logmanager.LogContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggerTest {

    @BeforeClass
    public static void setUpLogContext() {
        LogContext.setLogContextSelector(TestLogContextSelector.forClass(LoggerTest.class));
    }

    private Logger logger;
    private Appender a1;
    private Appender a2;

    private ResourceBundle rbUS;
    private ResourceBundle rbFR;
    private ResourceBundle rbCH;

    // A short message.
    private static final String MSG = "M";

    @Before
    public void setUp() {
        rbUS = ResourceBundle.getBundle("L7D", new Locale("en", "US"));
        assertNotNull(rbUS);

        rbFR = ResourceBundle.getBundle("L7D", new Locale("fr", "FR"));
        assertNotNull("Got a null resource bundle.", rbFR);

        rbCH = ResourceBundle.getBundle("L7D", new Locale("fr", "CH"));
        assertNotNull("Got a null resource bundle.", rbCH);

    }

    @After
    public void tearDown() {
        // Regular users should not use the clear method lightly!
        //Logger.getDefaultHierarchy().clear();
        BasicConfigurator.resetConfiguration();
        if (logger != null) {
            logger.removeAllAppenders();
        }
        logger = null;
        a1 = null;
        a2 = null;
    }

    /**
     * Add an appender and see if it can be retrieved.
     */
    @Test
    public void testAppender1() {
        logger = Logger.getLogger("test");
        a1 = new FileAppender();
        a1.setName("testAppender1");
        logger.addAppender(a1);

        Enumeration enumeration = logger.getAllAppenders();
        Appender aHat = (Appender) enumeration.nextElement();
        assertEquals(a1, aHat);
    }

    /**
     * Add an appender X, Y, remove X and check if Y is the only
     * remaining appender.
     */
    @Test
    public void testAppender2() {
        a1 = new FileAppender();
        a1.setName("testAppender2.1");
        a2 = new FileAppender();
        a2.setName("testAppender2.2");

        final Logger logger = Logger.getLogger("test");
        logger.removeAllAppenders();
        logger.addAppender(a1);
        logger.addAppender(a2);
        logger.removeAppender("testAppender2.1");
        Enumeration enumeration = logger.getAllAppenders();
        Appender aHat = (Appender) enumeration.nextElement();
        assertEquals(a2, aHat);
        assertTrue(!enumeration.hasMoreElements());
    }

    /**
     * Test if logger a.b inherits its appender from a.
     */
    @Test
    public void testAdditivity1() {
        Logger a = Logger.getLogger("a");
        Logger ab = Logger.getLogger("a.b");
        CountingAppender ca = new CountingAppender();
        a.addAppender(ca);

        assertEquals(ca.counter, 0);
        ab.debug(MSG);
        assertEquals(ca.counter, 1);
        ab.info(MSG);
        assertEquals(ca.counter, 2);
        ab.warn(MSG);
        assertEquals(ca.counter, 3);
        ab.error(MSG);
        assertEquals(ca.counter, 4);

        // Clean-up
        a.removeAllAppenders();
        ab.removeAllAppenders();

    }

    /**
     * Test multiple additivity.
     */
    @Test
    public void testAdditivity2() {

        Logger a = Logger.getLogger("a");
        Logger ab = Logger.getLogger("a.b");
        Logger abc = Logger.getLogger("a.b.c");
        Logger x = Logger.getLogger("x");

        CountingAppender ca1 = new CountingAppender();
        CountingAppender ca2 = new CountingAppender();

        a.addAppender(ca1);
        abc.addAppender(ca2);

        assertEquals(ca1.counter, 0);
        assertEquals(ca2.counter, 0);

        ab.debug(MSG);
        assertEquals(ca1.counter, 1);
        assertEquals(ca2.counter, 0);

        abc.debug(MSG);
        assertEquals(ca1.counter, 2);
        assertEquals(ca2.counter, 1);

        x.debug(MSG);
        assertEquals(ca1.counter, 2);
        assertEquals(ca2.counter, 1);

        // Clean-up
        a.removeAllAppenders();
        ab.removeAllAppenders();
        abc.removeAllAppenders();
        x.removeAllAppenders();
    }

    /**
     * Test additivity flag.
     */
    @Test
    public void testAdditivity3() {

        logger = Logger.getRootLogger();
        Logger a = Logger.getLogger("a");
        Logger ab = Logger.getLogger("a.b");
        Logger abc = Logger.getLogger("a.b.c");

        CountingAppender caRoot = new CountingAppender();
        CountingAppender caA = new CountingAppender();
        CountingAppender caABC = new CountingAppender();

        logger.addAppender(caRoot);
        a.addAppender(caA);
        abc.addAppender(caABC);

        assertEquals(caRoot.counter, 0);
        assertEquals(caA.counter, 0);
        assertEquals(caABC.counter, 0);

        ab.setAdditivity(false);


        a.debug(MSG);
        assertEquals(caRoot.counter, 1);
        assertEquals(caA.counter, 1);
        assertEquals(caABC.counter, 0);

        ab.debug(MSG);
        assertEquals(caRoot.counter, 1);
        assertEquals(caA.counter, 1);
        assertEquals(caABC.counter, 0);

        abc.debug(MSG);
        assertEquals(caRoot.counter, 1);
        assertEquals(caA.counter, 1);
        assertEquals(caABC.counter, 1);

        // Clean-up
        a.removeAllAppenders();
        ab.removeAllAppenders();
        abc.removeAllAppenders();

    }


    @Test
    public void testDisable1() {
        CountingAppender caRoot = new CountingAppender();
        logger = Logger.getRootLogger();
        logger.addAppender(caRoot);

        LoggerRepository h = LogManager.getLoggerRepository();
        //h.disableDebug();
        h.setThreshold(Level.INFO);
        assertEquals(caRoot.counter, 0);

        logger.debug(MSG);
        assertEquals(caRoot.counter, 0);
        logger.info(MSG);
        assertEquals(caRoot.counter, 1);
        logger.log(Level.WARN, MSG);
        assertEquals(caRoot.counter, 2);
        logger.warn(MSG);
        assertEquals(caRoot.counter, 3);

        //h.disableInfo();
        h.setThreshold((Level) Level.WARN);
        logger.debug(MSG);
        assertEquals(caRoot.counter, 3);
        logger.info(MSG);
        assertEquals(caRoot.counter, 3);
        logger.log(Level.WARN, MSG);
        assertEquals(caRoot.counter, 4);
        logger.error(MSG);
        assertEquals(caRoot.counter, 5);
        logger.log(Level.ERROR, MSG);
        assertEquals(caRoot.counter, 6);

        //h.disableAll();
        h.setThreshold(Level.OFF);
        logger.debug(MSG);
        assertEquals(caRoot.counter, 6);
        logger.info(MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.WARN, MSG);
        assertEquals(caRoot.counter, 6);
        logger.error(MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.FATAL, MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.FATAL, MSG);
        assertEquals(caRoot.counter, 6);

        //h.disable(Level.FATAL);
        h.setThreshold(Level.OFF);
        logger.debug(MSG);
        assertEquals(caRoot.counter, 6);
        logger.info(MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.WARN, MSG);
        assertEquals(caRoot.counter, 6);
        logger.error(MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.ERROR, MSG);
        assertEquals(caRoot.counter, 6);
        logger.log(Level.FATAL, MSG);
        assertEquals(caRoot.counter, 6);
    }


    @Ignore("Setting resource bundles is not supported")
    @Test
    public void testRB1() {
        logger = Logger.getRootLogger();
        logger.setResourceBundle(rbUS);
        ResourceBundle t = logger.getResourceBundle();
        assertSame(t, rbUS);

        Logger x = Logger.getLogger("x");
        Logger x_y = Logger.getLogger("x.y");
        Logger x_y_z = Logger.getLogger("x.y.z");

        t = x.getResourceBundle();
        assertSame(t, rbUS);
        t = x_y.getResourceBundle();
        assertSame(t, rbUS);
        t = x_y_z.getResourceBundle();
        assertSame(t, rbUS);
    }

    @Ignore("Setting resource bundles is not supported")
    @Test
    public void testRB2() {
        logger = Logger.getRootLogger();
        logger.setResourceBundle(rbUS);
        ResourceBundle t = logger.getResourceBundle();
        assertSame(t, rbUS);

        Logger x = Logger.getLogger("x");
        Logger x_y = Logger.getLogger("x.y");
        Logger x_y_z = Logger.getLogger("x.y.z");

        x_y.setResourceBundle(rbFR);
        t = x.getResourceBundle();
        assertSame(t, rbUS);
        t = x_y.getResourceBundle();
        assertSame(t, rbFR);
        t = x_y_z.getResourceBundle();
        assertSame(t, rbFR);
    }

    @Ignore("Setting resource bundles is not supported")
    @Test
    public void testRB3() {
        logger = Logger.getRootLogger();
        logger.setResourceBundle(rbUS);
        ResourceBundle t = logger.getResourceBundle();
        assertSame(t, rbUS);

        Logger x = Logger.getLogger("x");
        Logger x_y = Logger.getLogger("x.y");
        Logger x_y_z = Logger.getLogger("x.y.z");

        x_y.setResourceBundle(rbFR);
        x_y_z.setResourceBundle(rbCH);
        t = x.getResourceBundle();
        assertSame(t, rbUS);
        t = x_y.getResourceBundle();
        assertSame(t, rbFR);
        t = x_y_z.getResourceBundle();
        assertSame(t, rbCH);
    }

    @Test
    public void testExists() {
        Logger a = Logger.getLogger("a");
        Logger a_b = Logger.getLogger("a.b");
        Logger a_b_c = Logger.getLogger("a.b.c");

        Logger t;
        t = LogManager.exists("xx");
        assertNull(t);
        t = LogManager.exists("a");
        assertSame(a, t);
        t = LogManager.exists("a.b");
        assertSame(a_b, t);
        t = LogManager.exists("a.b.c");
        assertSame(a_b_c, t);
    }

    @Test
    public void testHierarchy1() {
        Hierarchy h = new Hierarchy(new RootLogger((Level) Level.ERROR));
        Logger a0 = h.getLogger("a");
        assertEquals("a", a0.getName());
        assertNull(a0.getLevel());
        assertSame(Level.ERROR, a0.getEffectiveLevel());

        Logger a1 = h.getLogger("a");
        assertSame(a0, a1);
    }

    private static final class CountingHierarchyEventListener implements HierarchyEventListener {
        private int addEventCount;
        private int removeEventCount;

        public CountingHierarchyEventListener() {
            addEventCount = removeEventCount = 0;
        }

        public void addAppenderEvent(Category cat, Appender appender) {
            addEventCount++;
        }

        public void removeAppenderEvent(Category cat, Appender appender) {
            removeEventCount++;
        }

        public int getAddEventCount() {
            return addEventCount;
        }

        public int getRemoveEventCount() {
            return removeEventCount;
        }
    }


    @Test
    public void testAppenderEvent1() {
        CountingHierarchyEventListener listener = new CountingHierarchyEventListener();
        LogManager.getLoggerRepository().addHierarchyEventListener(listener);
        CountingAppender appender = new CountingAppender();
        logger = Logger.getRootLogger();
        logger.addAppender(appender);
        assertEquals(1, listener.getAddEventCount());
        assertEquals(0, listener.getRemoveEventCount());
        logger.removeAppender(appender);
        assertEquals(1, listener.getAddEventCount());
        assertEquals(1, listener.getRemoveEventCount());
    }

    @Test
    public void testAppenderEvent2() {
        CountingHierarchyEventListener listener = new CountingHierarchyEventListener();
        LogManager.getLoggerRepository().addHierarchyEventListener(listener);
        CountingAppender appender = new CountingAppender();
        appender.setName("A1");
        logger = Logger.getRootLogger();
        logger.addAppender(appender);
        assertEquals(1, listener.getAddEventCount());
        assertEquals(0, listener.getRemoveEventCount());
        logger.removeAppender(appender.getName());
        assertEquals(1, listener.getAddEventCount());
        assertEquals(1, listener.getRemoveEventCount());
    }

    @Test
    public void testAppenderEvent3() {
        CountingHierarchyEventListener listener = new CountingHierarchyEventListener();
        LogManager.getLoggerRepository().addHierarchyEventListener(listener);
        CountingAppender appender = new CountingAppender();
        logger = Logger.getRootLogger();
        logger.addAppender(appender);
        assertEquals(1, listener.getAddEventCount());
        assertEquals(0, listener.getRemoveEventCount());
        logger.removeAllAppenders();
        assertEquals(1, listener.getAddEventCount());
        assertEquals(1, listener.getRemoveEventCount());
    }

    @Ignore("At this point resetting the LogManager does nothing.")
    @Test
    public void testAppenderEvent4() {
        CountingHierarchyEventListener listener = new CountingHierarchyEventListener();
        LogManager.getLoggerRepository().addHierarchyEventListener(listener);
        CountingAppender appender = new CountingAppender();
        logger = Logger.getRootLogger();
        logger.addAppender(appender);
        assertEquals(1, listener.getAddEventCount());
        assertEquals(0, listener.getRemoveEventCount());
        LogManager.resetConfiguration();
        assertEquals(1, listener.getAddEventCount());
        assertEquals(1, listener.getRemoveEventCount());
    }

    static private class CountingAppender extends AppenderSkeleton {

        int counter;

        CountingAppender() {
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
}
