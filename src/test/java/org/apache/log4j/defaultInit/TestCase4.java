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

package org.apache.log4j.defaultInit;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.util.Paths;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCase4 {

    @Before
    public void setUp() {
        PropertyConfigurator.configure(Paths.resolveResourcePath("defaultInit3.properties"));
        DOMConfigurator.configure(Paths.resolveResourcePath("defaultInit.xml"));
    }

    @After
    public void tearDown() {
        LogManager.shutdown();
        Paths.cleanUpTemp();
    }

    @Test
    public void combinedTest() {
        Logger root = Logger.getRootLogger();
        boolean rootIsConfigured = root.getAllAppenders().hasMoreElements();
        assertTrue(rootIsConfigured);
        Enumeration e = root.getAllAppenders();
        Appender appender = (Appender) e.nextElement();
        assertEquals(appender.getName(), "D1");
        assertEquals(e.hasMoreElements(), false);
    }

}

