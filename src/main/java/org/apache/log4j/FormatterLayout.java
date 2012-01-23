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

import java.util.logging.Formatter;

import org.apache.log4j.spi.LoggingEvent;

/**
 * A {@link Layout} that delegates to a {@link Formatter}.
 * <p/>
 * Date: 30.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class FormatterLayout extends Layout {
    private final Formatter formatter;

    /**
     * Construct a new instance.
     *
     * @param formatter the formatter to delegate to
     */
    public FormatterLayout(final Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String format(final LoggingEvent event) {
        return formatter.format(LoggingEventWrapper.getLogRecordFor(event));
    }

    @Override
    public boolean ignoresThrowable() {
        // just be safe
        return false;
    }

    @Override
    public void activateOptions() {
        // options are always activated already
    }
}
