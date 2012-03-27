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
