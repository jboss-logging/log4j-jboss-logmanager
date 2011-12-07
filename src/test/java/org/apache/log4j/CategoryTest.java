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

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Method;


/**
 * Tests of Category.
 *
 * @author Curt Arnold
 * @since 1.2.14
 */
public class CategoryTest {
  /**

  /**
   * Tests Category.forcedLog.
   */
  @Test
  public void testForcedLog() {
    MockCategory category = new MockCategory("org.example.foo");
    category.setAdditivity(false);
    category.addAppender(new VectorAppender());
    category.info("Hello, World");
  }

  /**
   * Tests that the return type of getChainedPriority is Priority.
   * @throws Exception thrown if Category.getChainedPriority can not be found.
   */
  @Test
  public void testGetChainedPriorityReturnType() throws Exception {
    Method method = Category.class.getMethod("getChainedPriority", (Class[]) null);
    assertTrue(method.getReturnType() == Priority.class);
  }

  /**
   * Tests l7dlog(Priority, String, Throwable).
   */
  @Test
  public void testL7dlog() {
    Logger logger = Logger.getLogger("org.example.foo");
    logger.setLevel(Level.ERROR);
    Priority debug = Level.DEBUG;
    logger.l7dlog(debug, "Hello, World", null);
  }

  /**
   * Tests l7dlog(Priority, String, Object[], Throwable).
   */
  @Test
  public void testL7dlog4Param() {
    Logger logger = Logger.getLogger("org.example.foo");
    logger.setLevel(Level.ERROR);
    Priority debug = Level.DEBUG;
    logger.l7dlog(debug, "Hello, World", new Object[0], null);
  }

  /**
   * Tests setPriority(Priority).
   * @deprecated
   */
  @Test
  public void testSetPriority() {
    Logger logger = Logger.getLogger("org.example.foo");
    Priority debug = Level.DEBUG;
    logger.setPriority(debug);
  }

  /**
   * Derived category to check method signature of forcedLog.
   */
  private static class MockCategory extends Logger {
    /**
     * Create new instance of MockCategory.
     * @param name category name
     */
    public MockCategory(final String name) {
      super(name);
      repository = new Hierarchy(this);
    }

    /**
     * Request an info level message.
     * @param msg message
     */
    public void info(final String msg) {
      Priority info = Level.INFO;
      forcedLog(MockCategory.class.toString(), info, msg, null);
    }
  }
}
