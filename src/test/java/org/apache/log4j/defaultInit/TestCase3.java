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

package org.apache.log4j.defaultInit;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.TestLogContextSelector;
import org.apache.log4j.util.Paths;
import org.jboss.logmanager.LogContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCase3 {

    @BeforeClass
    public static void setUpLogContext() {
        LogContext.setLogContextSelector(TestLogContextSelector.forClass(TestCase3.class));
    }

    @Before
    public void setUp() {
        PropertyConfigurator.configure(Paths.resolveResourcePath("defaultInit3.properties"));
    }

    @After
    public void tearDown() {
        LogManager.shutdown();
        Paths.cleanUpTemp();
    }

    @Test
    public void propertiesTest() {
        Logger root = Logger.getRootLogger();
        boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
        assertTrue(rootIsConfigured);
        Enumeration e = root.getAllAppenders();
        Appender appender = (Appender) e.nextElement();
        assertEquals(appender.getName(), "D3");
    }

}

