/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.log4j;


import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Extended ObjectInputStream that only allows certain classes to be deserialized.
 *
 * Backported from 2.8.2
 */
public class FilteredObjectInputStream extends ObjectInputStream {

    private static final List<String> REQUIRED_JAVA_CLASSES = Arrays.asList(
            // Types of non-trainsient fields of LoggingEvent
            "java.lang.String",
            "java.util.Hashtable",
            // ThrowableInformation
            "[Ljava.lang.String;"
    );

    private final Collection<String> allowedClasses;

    public FilteredObjectInputStream(final InputStream in, final Collection<String> allowedClasses) throws IOException {
        super(in);
        this.allowedClasses = allowedClasses;
    }

    @Override
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (!(isAllowedByDefault(name) || allowedClasses.contains(name))) {
            throw new InvalidObjectException("Class is not allowed for deserialization: " + name);
        }
        return super.resolveClass(desc);
    }

    private static boolean isAllowedByDefault(final String name) {
        return name.startsWith("org.apache.log4j.") ||
                name.startsWith("[org.apache.log4j.") ||
                REQUIRED_JAVA_CLASSES.contains(name);
    }

}
