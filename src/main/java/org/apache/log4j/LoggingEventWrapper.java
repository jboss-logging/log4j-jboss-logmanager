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

package org.apache.log4j;

import java.util.Collections;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.jboss.logmanager.ExtLogRecord;

/**
 * A {@link LoggingEvent} that wraps an {@link ExtLogRecord LogRecord}.
 * <p/>
 * Date: 29.11.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class LoggingEventWrapper extends LoggingEvent {

    /**
     * Creates a new logging event.
     *
     * @param record   the log record.
     * @param category the category the event was for.
     */
    public LoggingEventWrapper(final ExtLogRecord record, final Category category) {
        super(record.getLoggerClassName(),
                category,
                record.getMillis(),
                LevelMapping.getPriorityFor(record.getLevel()),
                record.getFormattedMessage(),
                record.getThreadName(),
                record.getThrown() == null ? null : new ThrowableInformation(record.getThrown()),
                record.getNdc(),
                new LocationInfo(new Throwable(), record.getLoggerClassName()),
                Collections.singletonMap("org.jboss.logmanager.record", record));
    }

    /**
     * Get a log record for a log4j event. If the event wraps a log record, that record is returned; otherwise
     * a new record is built up from the event.
     *
     * @param event the event
     *
     * @return the log record
     */
    public static ExtLogRecord getLogRecordFor(LoggingEvent event) {
        final ExtLogRecord rec = (ExtLogRecord) event.getProperties().get("org.jboss.logmanager.record");
        if (rec != null) {
            return rec;
        }
        final ExtLogRecord newRecord = new ExtLogRecord(LevelMapping.getLevelFor(event.getLevel()), (String) event.getMessage(), event.getFQNOfLoggerClass());
        newRecord.setLoggerName(event.getLoggerName());
        newRecord.setMillis(event.getTimeStamp());
        newRecord.setThreadName(event.getThreadName());
        newRecord.setThrown(event.getThrowableInformation().getThrowable());
        newRecord.setNdc(event.getNDC());
        if (event.locationInformationExists()) {
            final LocationInfo locationInfo = event.getLocationInformation();
            newRecord.setSourceClassName(locationInfo.getClassName());
            newRecord.setSourceFileName(locationInfo.getFileName());
            newRecord.setSourceLineNumber(Integer.parseInt(locationInfo.getLineNumber()));
            newRecord.setSourceMethodName(locationInfo.getMethodName());
        }
        return newRecord;
    }
}
