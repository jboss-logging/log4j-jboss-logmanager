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

import org.apache.log4j.spi.LoggerFactory;

public class Logger extends Category {

    private static final String FQCN = Logger.class.getName();

    protected Logger(String name) {
        super(name);
    }

    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    public static Logger getLogger(Class clazz) {
        return LogManager.getLogger(clazz.getName());
    }

    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return LogManager.getLogger(name, factory);
    }

    public void trace(Object message) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.TRACE)) {
            forcedLog(FQCN, Level.TRACE, message, null);
        }
    }

    public void trace(Object message, Throwable t) {
        if (jblmLogger.isLoggable(org.jboss.logmanager.Level.TRACE)) {
            forcedLog(FQCN, Level.TRACE, message, t);
        }
    }

    public boolean isTraceEnabled() {
        return jblmLogger.isLoggable(org.jboss.logmanager.Level.TRACE);
    }
}
