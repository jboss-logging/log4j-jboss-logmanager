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

package org.apache.log4j.util;

import java.io.File;

/**
 * Date: 06.12.2011
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Paths {
    public static final String TEMP = "output/temp";

    public static final File TEMP_FILE = new File(TEMP);

    public static final String RESOURCES_PATH;

    static {
        final String currentDir = new File(".").getAbsoluteFile().getParent();
        RESOURCES_PATH = (currentDir.endsWith(File.separator) ? currentDir : currentDir + File.separator) + "src" + File.separator + "test" + File.separator + "resources";
    }

    public static String resolveResourcePath(final String path) {
        return new File(RESOURCES_PATH, path).getAbsolutePath();
    }

    public static void cleanUpTemp() {
        TEMP_FILE.delete();
        TEMP_FILE.getParentFile().delete();
    }
}
