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
import java.util.logging.LogRecord;

import org.apache.log4j.spi.Filter;
import org.jboss.logmanager.ExtLogRecord;

/**
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class FilterWrapper implements java.util.logging.Filter {
    private final Filter filterChain;
    private final boolean defaultResult;

    public FilterWrapper(Filter filterChain, final boolean defaultResult) {
        this.filterChain = filterChain;
        this.defaultResult = defaultResult;
    }

    public boolean isLoggable(final LogRecord record) {
        final ExtLogRecord extRec = ExtLogRecord.wrap(record);
        Filter filter = filterChain;
        while (filter != null) {
            final int result = filter.decide(new LoggingEventWrapper(extRec, Logger.getLogger(record.getLoggerName())));
            switch (result) {
                case Filter.DENY: return false;
                case Filter.ACCEPT: return true;
            }
            filter = filter.getNext();
        }
        return defaultResult;
    }
}
