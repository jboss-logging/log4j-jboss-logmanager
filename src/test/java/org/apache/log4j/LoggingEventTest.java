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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.log4j.spi.LoggingEvent;
import org.jboss.logmanager.ExtLogRecord;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggingEventTest {

    @Test
    public void testSerialization() throws Exception {
        final Exception e = new Exception("testException");
        final String category = "org.apache.log4j.test";
        final Logger logger = Logger.getLogger(category);
        final ExtLogRecord logRecord = new ExtLogRecord(org.jboss.logmanager.Level.INFO, "A test logging event", category);
        logRecord.setThrown(e);
        final LoggingEvent loggingEvent = new LoggingEvent(logRecord, logger);
        // Serialize the logging event
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream objOut = new ObjectOutputStream(out);
        try {
            objOut.writeObject(loggingEvent);
        } finally {
            objOut.close();
        }

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            final LoggingEvent deserializedEvent = (LoggingEvent) objIn.readObject();
            final String[] array = deserializedEvent.getThrowableStrRep();
            Assert.assertEquals(Arrays.toString(loggingEvent.getThrowableStrRep()), Arrays.toString(array));
        } finally {
            objIn.close();
        }
    }

    @Test
    public void testThrowableInformation() throws Exception {
        final String category = "org.apache.log4j.test";
        final Logger logger = Logger.getLogger(category);
        final ExtLogRecord logRecord = new ExtLogRecord(org.jboss.logmanager.Level.INFO, "A test logging event", category);
        LoggingEvent loggingEvent = new LoggingEvent(logRecord, logger);

        Assert.assertNull("Expected the throwableInformation to be null", loggingEvent.getThrowableInformation());
        Assert.assertNull(loggingEvent.getThrowableStrRep());

        final RuntimeException thrown = new RuntimeException("testException");
        logRecord.setThrown(thrown);
        loggingEvent = new LoggingEvent(logRecord, logger);

        Assert.assertNotNull(loggingEvent.getThrowableInformation());
        Assert.assertNotNull(loggingEvent.getThrowableStrRep());
        Assert.assertEquals(thrown, loggingEvent.getThrowableInformation().getThrowable());

        loggingEvent = new LoggingEvent(LoggingEventTest.class.getName(), logger, Level.INFO, "A test logging event", thrown);

        Assert.assertNotNull(loggingEvent.getThrowableInformation());
        Assert.assertNotNull(loggingEvent.getThrowableStrRep());
        Assert.assertEquals(thrown, loggingEvent.getThrowableInformation().getThrowable());
    }
}
