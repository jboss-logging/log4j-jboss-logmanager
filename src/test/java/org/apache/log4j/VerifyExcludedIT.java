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

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.net.JMSAppender;
import org.apache.log4j.net.JMSSink;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class VerifyExcludedIT {

    @Test
    public void ensureRemoved() throws Exception {
        final String path = System.getProperty("jar.name");
        Assert.assertNotNull("Expected system property jar.name to be present", path);
        final Path jar = Paths.get(path);
        Assert.assertTrue(String.format("JAR file %s does not exist.", jar), Files.exists(jar));
        try (FileSystem fs = zipFs(jar)) {
            ensureRemoved(fs, JMSAppender.class);
            ensureRemoved(fs, JMSSink.class);
            ensureRemoved(fs, JDBCAppender.class);
        }
    }

    private void ensureRemoved(final FileSystem fs, final Class<?> type) {
        final Path file = fs.getPath(fs.getSeparator(), type.getCanonicalName().replace('.', '/') + ".class");
        Assert.assertTrue(String.format("Expected type %s to not exist: %s", type.getCanonicalName(), file), Files.notExists(file));
    }

    private static FileSystem zipFs(final Path path) throws IOException {
        // locate file system by using the syntax
        // defined in java.net.JarURLConnection
        URI uri = URI.create("jar:" + path.toUri());
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException ignore) {
        }
        return FileSystems.newFileSystem(uri, Collections.singletonMap("create", "true"));
    }
}
