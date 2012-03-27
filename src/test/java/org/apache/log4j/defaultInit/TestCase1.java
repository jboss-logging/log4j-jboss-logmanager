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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.TestLogContextSelector;
import org.jboss.logmanager.LogContext;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCase1 {

    @BeforeClass
    public static void setUpLogContext() {
        LogContext.setLogContextSelector(TestLogContextSelector.forClass(TestCase1.class));
    }

    @After
    public void tearDown() {
        LogManager.shutdown();
    }

    @Test
    public void noneTest() {
        Logger root = Logger.getRootLogger();
        boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
        assertTrue(!rootIsConfigured);
    }

}

