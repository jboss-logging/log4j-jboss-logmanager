/*
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

/**
 * Use the <code>LogManager</code> class to retreive {@link Logger}
 * instances or to operate on the current {@link
 * org.apache.log4j.spi.LoggerRepository}. When the <code>LogManager</code> class is loaded
 * into memory the default initalzation procedure is inititated. The
 * default intialization procedure</a> is described in the <a
 * href="../../../../manual.html#defaultInit">short log4j manual</a>.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LogManager {

    /**
     * @deprecated This variable is for internal use only. It will
     *             become package protected in future versions.
     */
    static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";

    static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";

    /**
     * @deprecated This variable is for internal use only. It will
     *             become private in future versions.
     */
    static final public String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";

    /**
     * @deprecated This variable is for internal use only. It will
     *             become private in future versions.
     */
    static final public String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";

    /**
     * @deprecated This variable is for internal use only. It will
     *             become private in future versions.
     */
    public static final String DEFAULT_INIT_OVERRIDE_KEY =
            "log4j.defaultInitOverride";

    /**
     * Always throws an {@link IllegalArgumentException}. This method is not supported.
     *
     * @param selector the repository selector.
     * @param guard    a guard object.
     *
     * @throws IllegalArgumentException always throws.
     */
    public static void setRepositorySelector(RepositorySelector selector, Object guard) throws IllegalArgumentException {
        throw new IllegalArgumentException("Not supported in log4j-jboss-logmanager");
    }

    public static LoggerRepository getLoggerRepository() {
        return LogManagerFacade.getLoggerRepository();
    }

    /**
     * Retrieve the appropriate root logger.
     */
    public static Logger getRootLogger() {
        // Delegate the actual manufacturing of the logger to the logger repository.
        return getLoggerRepository().getRootLogger();
    }

    /**
     * Retrieve the appropriate {@link Logger} instance.
     */
    public static Logger getLogger(final String name) {
        // Delegate the actual manufacturing of the logger to the logger repository.
        return getLoggerRepository().getLogger(name);
    }

    /**
     * Retrieve the appropriate {@link Logger} instance.
     */
    public static Logger getLogger(final Class clazz) {
        // Delegate the actual manufacturing of the logger to the logger repository.
        return getLoggerRepository().getLogger(clazz.getName());
    }


    /**
     * Retrieve the appropriate {@link Logger} instance.
     */
    public static Logger getLogger(final String name, final LoggerFactory factory) {
        // Delegate the actual manufacturing of the logger to the logger repository.
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

