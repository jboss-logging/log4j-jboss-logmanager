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

import org.apache.log4j.util.Compare;
import org.apache.log4j.util.Paths;
import org.apache.log4j.xml.XLevel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test the configuration of the hierarchy-wide threshold.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class HierarchyThresholdTestCase {

    private static Logger logger = Logger.getLogger(HierarchyThresholdTestCase.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
        System.out.println("Tearing down test case.");
        logger.getLoggerRepository().resetConfiguration();
        Paths.cleanUpTemp();
    }

    @Test
    public void test1() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold1.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.1")));
    }

    @Test
    public void test2() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold2.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.2")));
    }

    @Test
    public void test3() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold3.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.3")));
    }

    @Test
    public void test4() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold4.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.4")));
    }

    @Test
    public void test5() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold5.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.5")));
    }

    @Test
    public void test6() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold6.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.6")));
    }

    @Test
    public void test7() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold7.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.7")));
    }

    @Test
    public void test8() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold8.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.8")));
    }


    static void common() {
        String oldThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName("main");

        logger.log(XLevel.TRACE, "m0");
        logger.debug("m1");
        logger.info("m2");
        logger.warn("m3");
        logger.error("m4");
        logger.fatal("m5");

        Thread.currentThread().setName(oldThreadName);
    }
}
