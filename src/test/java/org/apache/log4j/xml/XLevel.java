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

package org.apache.log4j.xml;

import org.apache.log4j.Level;


/**
   This class introduces a new level level called TRACE. TRACE has
   lower level than DEBUG.

 */
public class XLevel extends Level {
  private static final long serialVersionUID = 7288304330257085144L;

  static public final int  TRACE_INT   = Level.DEBUG_INT - 1;
  static public final int  LETHAL_INT  = Level.FATAL_INT + 1;


  private static String TRACE_STR  = "TRACE";
  private static String LETHAL_STR  = "LETHAL";


  public static final XLevel TRACE = new XLevel(TRACE_INT, TRACE_STR, 7);
  public static final XLevel LETHAL = new XLevel(LETHAL_INT, LETHAL_STR,
						       0);


  protected XLevel(int level, String strLevel, int syslogEquiv) {
    super(level, strLevel, syslogEquiv);
  }

  /**
     Convert the string passed as argument to a level. If the
     conversion fails, then this method returns {@link #TRACE}.
  */
  public
  static Level toLevel(String sArg) {
    return (Level) toLevel(sArg, XLevel.TRACE);
  }


  public
  static Level toLevel(String sArg, Level defaultValue) {

    if(sArg == null) {
      return defaultValue;
    }
    String stringVal = sArg.toUpperCase();

    if(stringVal.equals(TRACE_STR)) {
      return XLevel.TRACE;
    } else if(stringVal.equals(LETHAL_STR)) {
      return XLevel.LETHAL;
    }

    return Level.toLevel(sArg, (Level) defaultValue);
  }


  public
  static Level toLevel(int i) throws IllegalArgumentException {
    switch(i) {
    case TRACE_INT: return XLevel.TRACE;
    case LETHAL_INT: return XLevel.LETHAL;
    }
    return Level.toLevel(i);
  }

}

