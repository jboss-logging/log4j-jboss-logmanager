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

import java.util.Collection;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestUtil {

    private static void resetConfiguration() {
        final Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);
        root.setResourceBundle(null);
        LogManager.getLoggerRepository().setThreshold(Level.ALL);
        shutdown();
        final Collection<Logger> loggers = JBossLogManagerFacade.getLoggers();
        for (Logger logger : loggers) {
            logger.setLevel(null);
            logger.setAdditivity(true);
            logger.setResourceBundle(null);
        }
    }

    public static void shutdown() {
        final Logger root = Logger.getRootLogger();

        // begin by closing nested appenders
        root.closeNestedAppenders();
        final Collection<Logger> loggers = JBossLogManagerFacade.getLoggers();
        for (Logger logger : loggers) {
            logger.closeNestedAppenders();
        }
        root.removeAllAppenders();
        for (Logger logger : loggers) {
            logger.removeAllAppenders();
        }
    }
}
