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

import java.util.Enumeration;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;

public class LogManager {
    public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
    public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
    public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";

    /**
     * Always throws a {@link SecurityException}. This method is not supported.
     *
     * @param selector the repository selector.
     * @param guard    a guard object.
     *
     * @throws SecurityException always
     */
    public static void setRepositorySelector(RepositorySelector selector, Object guard) throws SecurityException {
        throw new SecurityException("Not supported in log4j-jboss-logmanager");
    }

    public static LoggerRepository getLoggerRepository() {
        return JBossLogManagerFacade.getLoggerRepository();
    }

    public static Logger getRootLogger() {
        return getLoggerRepository().getRootLogger();
    }

    public static Logger getLogger(final String name) {
        return getLoggerRepository().getLogger(name);
    }

    public static Logger getLogger(final Class clazz) {
        return getLoggerRepository().getLogger(clazz.getName());
    }

    public static Logger getLogger(final String name, final LoggerFactory factory) {
        return getLoggerRepository().getLogger(name, factory);
    }

    public static Logger exists(final String name) {
        return getLoggerRepository().exists(name);
    }

    public static Enumeration getCurrentLoggers() {
        return getLoggerRepository().getCurrentLoggers();
    }

    public static void shutdown() {
        getLoggerRepository().shutdown();
    }

    public static void resetConfiguration() {
        getLoggerRepository().resetConfiguration();
    }
}

