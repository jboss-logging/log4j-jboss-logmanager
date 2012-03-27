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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.log4j.spi.LoggerFactory;
import org.jboss.logmanager.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggerFactoryTest implements LoggerFactory {

    @BeforeClass
    public static void setUp() {
        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
    }

    private final AtomicBoolean ran = new AtomicBoolean();
    private final String cat = getClass().getName();
    private final String factory = "factory";

    @Test
    public void testBasic() {
        Logger.getLogger(cat).addHandler(new CheckingHandler());
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(cat);
        logger.info("Hello, world");
    }

    public void testLoggerFactory() {
        Logger.getLogger(factory).addHandler(new CheckingHandler());
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(factory, this);
        logger.info("Hello, world");
        logger.info("Error", new Throwable());
        assertTrue("Right class " + logger, logger instanceof MyLogger);
        logger = org.apache.log4j.Logger.getLogger("different", this);
        assertTrue("Right class " + logger, logger instanceof MyLogger);
    }

    class MyLogger extends org.apache.log4j.Logger {

        protected MyLogger(String name) {
            super(name);
        }

    }

    public org.apache.log4j.Logger makeNewLoggerInstance(String category) {
        return new MyLogger(category);
    }

    private class CheckingHandler extends Handler {

        public void publish(final LogRecord record) {
            if (isLoggable(record)) {
                ran.set(true);
            }
            System.out.println(record.getMessage());
        }

        public void flush() {
        }

        public void close() throws SecurityException {
        }
    }
}
