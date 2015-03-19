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

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.helpers.LogLog;

/**
 * ConsoleAppender appends log events to <code>System.out</code> or
 * <code>System.err</code> using a layout specified by the user. The
 * default target is <code>System.out</code>.
 * <p>
 * Modification was done in JBoss fork to use the real <strong>stdout</strong>/<strong>stderr</strong>
 * via <code>FileDescriptor.out</code>/<code>FileDescriptor.err</code> instead of
 * <code>System.out</code>/<code>System.err</code>, because in some cases, the standard
 * stream may be reassigned.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 * @since 1.1
 */
public class ConsoleAppender extends WriterAppender {

    public static final String SYSTEM_OUT = "System.out";
    public static final String SYSTEM_ERR = "System.err";

    protected String target = SYSTEM_OUT;

    /**
     * Determines if the appender honors reassignments of System.out
     * or System.err made after configuration.
     */
    private boolean follow = false;

    /**
     * Constructs an unconfigured appender.
     */
    public ConsoleAppender() {
    }

    /**
     * Creates a configured appender.
     *
     * @param layout layout, may not be null.
     */
    public ConsoleAppender(Layout layout) {
        this(layout, SYSTEM_OUT);
    }

    /**
     * Creates a configured appender.
     *
     * @param layout layout, may not be null.
     * @param target target, either "System.err" or "System.out".
     */
    public ConsoleAppender(Layout layout, String target) {
        setLayout(layout);
        setTarget(target);
        activateOptions();
    }

    /**
     * Sets the value of the <b>Target</b> option. Recognized values
     * are "System.out" and "System.err". Any other value will be
     * ignored.
     */
    public void setTarget(String value) {
        String v = value.trim();

        if (SYSTEM_OUT.equalsIgnoreCase(v)) {
            target = SYSTEM_OUT;
        } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
            target = SYSTEM_ERR;
        } else {
            targetWarn(value);
        }
    }

    /**
     * Returns the current value of the <b>Target</b> property. The
     * default value of the option is "System.out".
     *
     * See also {@link #setTarget}.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets whether the appender honors reassignments of System.out
     * or System.err made after configuration.
     * <p>
     * <strong>Note:</strong> The follow value is not used and streams will always be closed and recreated if
     * necessary.
     * </p>
     *
     * @param newValue if true, appender will use value of System.out or
     *                 System.err in force at the time when logging events are appended.
     *
     * @since 1.2.13
     */
    public final void setFollow(final boolean newValue) {
        follow = newValue;
    }

    /**
     * Gets whether the appender honors reassignments of System.out
     * or System.err made after configuration.
     * <p>
     * <strong>Note:</strong> The follow value is not used and streams will always be closed and recreated if
     * necessary.
     * </p>
     *
     * @return true if appender will use value of System.out or
     * System.err in force at the time when logging events are appended.
     *
     * @since 1.2.13
     */
    public final boolean getFollow() {
        return follow;
    }

    void targetWarn(String val) {
        LogLog.warn("[" + val + "] should be System.out or System.err.");
        LogLog.warn("Using previously set target, System.out by default.");
    }

    /**
     * Prepares the appender for use.
     */
    public void activateOptions() {
        if (target.equals(SYSTEM_ERR)) {
            setWriter(createWriter(new SystemErrStream()));
        } else {
            setWriter(createWriter(new SystemOutStream()));
        }

        super.activateOptions();
    }

    /**
     * {@inheritDoc}
     */
    protected final void closeWriter() {
        super.closeWriter();
    }

    private static PrintStream createPrintStream(final FileDescriptor fd) {
        return new PrintStream(new BufferedOutputStream(new FileOutputStream(fd), 128));
    }


    /**
     * An implementation of OutputStream that redirects to the
     * current System.err.
     */
    private static class SystemErrStream extends OutputStream {
        private final PrintStream err;

        public SystemErrStream() {
            err = createPrintStream(FileDescriptor.err);
        }

        public void close() {
            err.close();
        }

        public void flush() {
            err.flush();
        }

        public void write(final byte[] b) throws IOException {
            err.write(b);
        }

        public void write(final byte[] b, final int off, final int len) throws IOException {
            err.write(b, off, len);
        }

        public void write(final int b) throws IOException {
            err.write(b);
        }
    }

    /**
     * An implementation of OutputStream that redirects to the
     * current System.out.
     */
    private static class SystemOutStream extends OutputStream {
        private final PrintStream out;

        public SystemOutStream() {
            out = createPrintStream(FileDescriptor.out);
        }

        public void close() {
            out.close();
        }

        public void flush() {
            out.flush();
        }

        public void write(final byte[] b) throws IOException {
            out.write(b);
        }

        public void write(final byte[] b, final int off, final int len) throws IOException {
            out.write(b, off, len);
        }

        public void write(final int b) throws IOException {
            out.write(b);
        }
    }

}
