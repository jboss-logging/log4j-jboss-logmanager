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

import java.io.*;


public class Compare {

    static final int B1_NULL = -1;
    static final int B2_NULL = -2;

    static
    public boolean compare(String file1, String file2) throws FileNotFoundException,
            IOException {
        BufferedReader in1 = new BufferedReader(new FileReader(file1));
        BufferedReader in2 = new BufferedReader(new FileReader(file2));

        String s1;
        int lineCounter = 0;
        while ((s1 = in1.readLine()) != null) {
            lineCounter++;
            String s2 = in2.readLine();
            if (!s1.equals(s2)) {
                System.out.println("Files [" + file1 + "] and [" + file2 + "] differ on line "
                        + lineCounter);
                System.out.println("One reads:  [" + s1 + "].");
                System.out.println("Other reads:[" + s2 + "].");
                in1.close();
                in2.close();
                return false;
            }
        }

        // the second file is longer
        if (in2.read() != -1) {
            System.out.println("File [" + file2 + "] longer than file [" + file1 + "].");
            in1.close();
            in2.close();
            return false;
        }

        in1.close();
        in2.close();
        return true;
    }

    private static final InputStream open(
            final Class testClass,
            final String fileName) throws IOException {
        String resourceName = fileName;
        if (fileName.startsWith("witness/")) {
            resourceName = fileName.substring(fileName.lastIndexOf('/') + 1);
        }
        InputStream is = testClass.getResourceAsStream(resourceName);
        if (is == null) {
            File file = new File(fileName);
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                throw new FileNotFoundException("Resource "
                        + resourceName + " not found");
            }
        }
        return is;
    }

    public static boolean compare(Class testClass,
                                  final String file1,
                                  final String file2)
            throws IOException {
        BufferedReader in1 = new BufferedReader(new FileReader(file1));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                open(testClass, file2)));
        try {
            return compare(testClass, file1, file2, in1, in2);
        } finally {
            in1.close();
            in2.close();
        }
    }

    public static boolean compare(
            Class testClass, String file1, String file2, BufferedReader in1, BufferedReader in2) throws IOException {

        String s1;
        int lineCounter = 0;

        while ((s1 = in1.readLine()) != null) {
            lineCounter++;

            String s2 = in2.readLine();

            if (!s1.equals(s2)) {
                System.out.println(
                        "Files [" + file1 + "] and [" + file2 + "] differ on line "
                                + lineCounter);
                System.out.println("One reads:  [" + s1 + "].");
                System.out.println("Other reads:[" + s2 + "].");
                outputFile(testClass, file1);
                outputFile(testClass, file2);

                return false;
            }
        }

        // the second file is longer
        if (in2.read() != -1) {
            System.out.println(
                    "File [" + file2 + "] longer than file [" + file1 + "].");
            outputFile(testClass, file1);
            outputFile(testClass, file2);

            return false;
        }

        return true;
    }

    /**
     * Prints file on the console.
     */
    private static void outputFile(Class testClass, String file)
            throws IOException {
        InputStream is = open(testClass, file);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(is));

        String s1;
        int lineCounter = 0;
        System.out.println("--------------------------------");
        System.out.println("Contents of " + file + ":");

        while ((s1 = in1.readLine()) != null) {
            lineCounter++;
            System.out.print(lineCounter);

            if (lineCounter < 10) {
                System.out.print("   : ");
            } else if (lineCounter < 100) {
                System.out.print("  : ");
            } else if (lineCounter < 1000) {
                System.out.print(" : ");
            } else {
                System.out.print(": ");
            }

            System.out.println(s1);
        }
        in1.close();
    }


}
