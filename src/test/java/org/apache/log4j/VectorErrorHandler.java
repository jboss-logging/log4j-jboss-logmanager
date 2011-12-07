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

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Vector;


/**
 * Utility class used in testing to capture errors dispatched
 * by appenders.
 *
 * @author Curt Arnold
 */
public final class VectorErrorHandler implements ErrorHandler {
  /**
   * Logger.
   */
  private Logger logger;

  /**
   * Appender.
   */
  private Appender appender;

  /**
   * Backup appender.
   */
  private Appender backupAppender;

  /**
   * Array of processed errors.
   */
  private final Vector errors = new Vector();

  /**
   * Default constructor.
   */
  public VectorErrorHandler() {
  }

  /**
   * {@inheritDoc}
   */
  public void setLogger(final Logger logger) {
    this.logger = logger;
  }

  /**
   * Gets last logger specified by setLogger.
   * @return logger.
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * {@inheritDoc}
   */
  public void activateOptions() {
  }

  /**
   * {@inheritDoc}
   */
  public void error(
    final String message, final Exception e, final int errorCode) {
    error(message, e, errorCode, null);
  }

  /**
   * {@inheritDoc}
   */
  public void error(final String message) {
    error(message, null, -1, null);
  }

  /**
   * {@inheritDoc}
   */
  public void error(
    final String message, final Exception e, final int errorCode,
    final LoggingEvent event) {
    errors.addElement(
      new Object[] { message, e, new Integer(errorCode), event });
  }

  /**
   * Gets message from specified error.
   *
   * @param index index.
   * @return message, may be null.
   */
  public String getMessage(final int index) {
    return (String) ((Object[]) errors.elementAt(index))[0];
  }

  /**
   * Gets exception from specified error.
   *
   * @param index index.
   * @return exception.
   */
  public Exception getException(final int index) {
    return (Exception) ((Object[]) errors.elementAt(index))[1];
  }

  /**
   * Gets error code from specified error.
   *
   * @param index index.
   * @return error code, -1 if not specified.
   */
  public int getErrorCode(final int index) {
    return ((Integer) ((Object[]) errors.elementAt(index))[2]).intValue();
  }

  /**
   * Gets logging event from specified error.
   *
   * @param index index.
   * @return exception.
   */
  public LoggingEvent getEvent(final int index) {
    return (LoggingEvent) ((Object[]) errors.elementAt(index))[3];
  }

  /**
   * Gets number of errors captured.
   * @return number of errors captured.
   */
  public int size() {
    return errors.size();
  }

  /**
   * {@inheritDoc}
   */
  public void setAppender(final Appender appender) {
    this.appender = appender;
  }

  /**
   * Get appender.
   * @return appender, may be null.
   */
  public Appender getAppender() {
    return appender;
  }

  /**
   * {@inheritDoc}
   */
  public void setBackupAppender(final Appender appender) {
    this.backupAppender = appender;
  }

  /**
   * Get backup appender.
   * @return backup appender, may be null.
   */
  public Appender getBackupAppender() {
    return backupAppender;
  }
}
