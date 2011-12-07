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

import org.apache.log4j.spi.LoggingEvent;

import java.util.Vector;

/**
   An appender that appends logging events to a vector.
   @author Ceki  G&uuml;lc&uuml;
*/
public class VectorAppender extends AppenderSkeleton {

  public Vector vector;

  public VectorAppender() {
    vector = new Vector();
  }

  /**
     Does nothing.
  */
  public void activateOptions() {
  }


  /**
     This method is called by the {@link AppenderSkeleton#doAppend}
     method.

  */
  public void append(LoggingEvent event) {
    //System.out.println("---Vector appender called with message ["+event.getRenderedMessage()+"].");
    //System.out.flush();
    try {
      Thread.sleep(100);
    } catch(Exception e) {
    }
    vector.addElement(event);
   }

  public Vector getVector() {
    return vector;
  }

  public synchronized void close() {
    if(this.closed)
      return;
    this.closed = true;
  }


  public boolean isClosed() {
    return closed;
  }

  public boolean requiresLayout() {
    return false;
  }
}
