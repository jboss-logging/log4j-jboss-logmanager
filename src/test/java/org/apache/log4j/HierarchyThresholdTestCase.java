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

import org.apache.log4j.util.Compare;
import org.apache.log4j.util.Paths;
import org.apache.log4j.xml.XLevel;
import org.jboss.logmanager.LogContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the configuration of the hierarchy-wide threshold.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class HierarchyThresholdTestCase {

    private Logger logger = null;

    @BeforeClass
    public static void setUpLogContext() {
        LogContext.setLogContextSelector(TestLogContextSelector.forClass(HierarchyThresholdTestCase.class));
    }

    @Before
    public void setUp() throws Exception {
        logger = Logger.getLogger(HierarchyThresholdTestCase.class);
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

    @Ignore("Doesn't currently work with the LevelMapping since it's using the XLevel")
    @Test
    public void test6() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold6.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.6")));
    }

    @Ignore("Doesn't currently work with the LevelMapping since it's using the XLevel")
    @Test
    public void test7() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold7.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.7")));
    }

    @Ignore("Doesn't currently work with the LevelMapping since it's using the XLevel")
    @Test
    public void test8() throws Exception {
        PropertyConfigurator.configure(Paths.resolveResourcePath("input/hierarchyThreshold8.properties"));
        common();
        assertTrue(Compare.compare(Paths.TEMP, Paths.resolveResourcePath("witness/hierarchyThreshold.8")));
    }


    void common() {
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
