/*
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

// Contibutors: Alex Blewitt <Alex.Blewitt@ioshq.com>
//              Markus Oestreicher <oes@zurich.ibm.com>
//              Frank Hoering <fhr@zurich.ibm.com>
//              Nelson Minar <nelson@media.mit.edu>
//              Jim Cakalic <jim_cakalic@na.biomerieux.com>
//              Avy Sharell <asharell@club-internet.fr>
//              Ciaran Treanor <ciaran@xelector.com>
//              Jeff Turner <jeff@socialchange.net.au>
//              Michael Horwitz <MHorwitz@siemens.co.za>
//              Calvin Chan <calvin.chan@hic.gov.au>
//              Aaron Greenhouse <aarong@cs.cmu.edu>
//              Beat Meier <bmeier@infovia.com.ar>
//              Colin Sampaleanu <colinml1@exis.com>

package org.apache.log4j;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class Category implements AppenderAttachable {
    private static final Object LEVEL_LOCK = new Object();

    private static final String FQCN = Category.class.getName();

    protected volatile Level level;

    protected volatile Category parent;

    private final org.jboss.logmanager.Logger jblmLogger;

    protected Category(String name) {
        jblmLogger = LogManagerFacade.getJbossLogger(name);
    }

    public void addAppender(Appender newAppender) {
        AppenderHandler.attachAppender(jblmLogger, newAppender);
        LogManagerFacade.getLoggerRepository().fireAddAppenderEvent(this, newAppender);
    }

    public void assertLog(boolean assertion, String msg) {
        if (!assertion) error(msg);
    }

    public void callAppenders(LoggingEvent event) {
        final List<Appender> appenders = AppenderHandler.getAllAppenders(jblmLogger);
        for (Appender appender : appenders) {
            appender.doAppend(event);
        }
    }

    org.jboss.logmanager.Logger getJbossLogger() {
        return jblmLogger;
    }

    /**
     * Close all attached appenders implementing the AppenderAttachable interface.
     *
     * @since 1.0
     */
    void closeNestedAppenders() {
        AppenderHandler.closeAppenders(jblmLogger);
    }

    /**
     * Log a message object with the {@link org.apache.log4j.Level#DEBUG DEBUG} level.
     * <p/>
     * <p>This method first checks if this category is <code>DEBUG</code> enabled by comparing the level of this
     * category with the {@link org.apache.log4j.Level#DEBUG DEBUG} level. If this category is <code>DEBUG</code>
     * enabled, then it converts the message object (passed as parameter) to a string by invoking the appropriate
     * {@link
     * org.apache.log4j.or.ObjectRenderer}. It then proceeds to call all the registered appenders in this category and
     * also higher in the hierarchy depending on the value of the additivity flag.
     * <p/>
     * <p><b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the
     * <code>Throwable</code> but no stack trace. To print a stack trace use the {@link #debug(Object, Throwable)} form
     * instead.
     *
     * @param message the message object to log.
     */
    public void debug(Object message) {
        forcedLog(FQCN, Level.DEBUG, message, null);
    }

    /**
     * Log a message object with the <code>DEBUG</code> level including the stack trace of the {@link Throwable}
     * <code>t</code> passed as parameter.
     * <p/>
     * <p>See {@link #debug(Object)} form for more detailed information.
     *
     * @param message the message object to log.
     * @param t       the exception to log, including its stack trace.
     */
    public void debug(Object message, Throwable t) {
        forcedLog(FQCN, Level.DEBUG, message, t);
    }

    /**
     * Log a message object with the {@link org.apache.log4j.Level#ERROR ERROR} Level.
     * <p/>
     * <p>This method first checks if this category is <code>ERROR</code> enabled by comparing the level of this
     * category with {@link org.apache.log4j.Level#ERROR ERROR} Level. If this category is <code>ERROR</code> enabled,
     * then it converts the message object passed as parameter to a string by invoking the appropriate {@link
     * org.apache.log4j.or.ObjectRenderer}. It proceeds to call all the registered appenders in this category and also
     * higher in the hierarchy depending on the value of the additivity flag.
     * <p/>
     * <p><b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the
     * <code>Throwable</code> but no stack trace. To print a stack trace use the {@link #error(Object, Throwable)} form
     * instead.
     *
     * @param message the message object to log
     */
    public void error(Object message) {
        forcedLog(FQCN, Level.ERROR, message, null);
    }

    /**
     * Log a message object with the <code>ERROR</code> level including the stack trace of the {@link Throwable}
     * <code>t</code> passed as parameter.
     * <p/>
     * <p>See {@link #error(Object)} form for more detailed information.
     *
     * @param message the message object to log.
     * @param t       the exception to log, including its stack trace.
     */
    public void error(Object message, Throwable t) {
        forcedLog(FQCN, Level.ERROR, message, t);
    }

    /**
     * If the named category exists (in the default hierarchy) then it returns a reference to the category, otherwise
     * it
     * returns <code>null</code>.
     *
     * @since 0.8.5
     * @deprecated Please use {@link org.apache.log4j.LogManager#exists} instead.
     */
    public static Logger exists(String name) {
        return LogManager.exists(name);
    }

    /**
     * Log a message object with the {@link org.apache.log4j.Level#FATAL FATAL} Level.
     * <p/>
     * <p>This method first checks if this category is <code>FATAL</code> enabled by comparing the level of this
     * category with {@link org.apache.log4j.Level#FATAL FATAL} Level. If the category is <code>FATAL</code> enabled,
     * then it converts the message object passed as parameter to a string by invoking the appropriate {@link
     * org.apache.log4j.or.ObjectRenderer}. It proceeds to call all the registered appenders in this category and also
     * higher in the hierarchy depending on the value of the additivity flag.
     * <p/>
     * <p><b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but
     * no stack trace. To print a stack trace use the {@link #fatal(Object, Throwable)} form instead.
     *
     * @param message the message object to log
     */
    public void fatal(Object message) {
        forcedLog(FQCN, Level.FATAL, message, null);
    }

    /**
     * Log a message object with the <code>FATAL</code> level including the stack trace of the {@link Throwable}
     * <code>t</code> passed as parameter.
     * <p/>
     * <p>See {@link #fatal(Object)} for more detailed information.
     *
     * @param message the message object to log.
     * @param t       the exception to log, including its stack trace.
     */
    public void fatal(Object message, Throwable t) {
        forcedLog(FQCN, Level.FATAL, message, t);
    }

    /**
     * This method creates a new logging event and logs the event without further checks.
     */
    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        if (jblmLogger.isLoggable(LevelMapping.getLevelFor(level))) {
            callAppenders(new LoggingEvent(fqcn, this, level, message, t));
        }
    }

    /**
     * Get the additivity flag for this Category instance.
     */
    public boolean getAdditivity() {
        return jblmLogger.getUseParentHandlers();
    }

    /**
     * Get the appenders contained in this category as an {@link java.util.Enumeration}. If no appenders can be found,
     * then a {@link org.apache.log4j.helpers.NullEnumeration} is returned.
     *
     * @return Enumeration An enumeration of the appenders in this category.
     */
    public Enumeration getAllAppenders() {
        final List<Appender> appenders = AppenderHandler.getAppenders(jblmLogger);
        if (appenders.isEmpty()) {
            return NullEnumeration.getInstance();
        }
        return Collections.enumeration(appenders);
    }

    /**
     * Look for the appender named as <code>name</code>.
     * <p/>
     * <p>Return the appender with that name if in the list. Return <code>null</code> otherwise.
     */
    public Appender getAppender(String name) {
        return AppenderHandler.getAppender(jblmLogger, name);
    }

    /**
     * Starting from this category, search the category hierarchy for a non-null level and return it. Otherwise, return
     * the level of the root category.
     * <p/>
     * <p>The Category class is designed so that this method executes as quickly as possible.
     */
    public Level getEffectiveLevel() {
        return LevelMapping.getPriorityFor(jblmLogger.getEffectiveLevel());
    }

    /**
     * @deprecated Please use the the {@link #getEffectiveLevel} method instead.
     */
    public Priority getChainedPriority() {
        return getEffectiveLevel();
    }

    /**
     * Returns all the currently defined categories in the default hierarchy as an {@link java.util.Enumeration
     * Enumeration}.
     * <p/>
     * <p>The root category is <em>not</em> included in the returned {@link java.util.Enumeration}.
     *
     * @deprecated Please use {@link org.apache.log4j.LogManager#getCurrentLoggers()} instead.
     */
    public static Enumeration getCurrentCategories() {
        return LogManager.getCurrentLoggers();
    }

    /**
     * Return the default Hierarchy instance.
     *
     * @since 1.0
     * @deprecated Please use {@link org.apache.log4j.LogManager#getLoggerRepository()} instead.
     */
    public static LoggerRepository getDefaultHierarchy() {
        return LogManager.getLoggerRepository();
    }

    /**
     * Return the the {@link org.apache.log4j.Hierarchy} where this <code>Category</code> instance is attached.
     *
     * @since 1.1
     * @deprecated Please use {@link #getLoggerRepository} instead.
     */
    public LoggerRepository getHierarchy() {
        return LogManagerFacade.getLoggerRepository();
    }

    /**
     * Return the the {@link org.apache.log4j.spi.LoggerRepository} where this <code>Category</code> is attached.
     *
     * @since 1.2
     */
    public LoggerRepository getLoggerRepository() {
        return LogManagerFacade.getLoggerRepository();
    }

    /**
     * @deprecated Make sure to use {@link org.apache.log4j.Logger#getLogger(String)} instead.
     */
    public static Category getInstance(String name) {
        return LogManager.getLogger(name);
    }

    /**
     * @deprecated Please make sure to use {@link org.apache.log4j.Logger#getLogger(Class)} instead.
     */
    public static Category getInstance(Class clazz) {
        return LogManager.getLogger(clazz);
    }

    /**
     * Return the category name.
     */
    public final String getName() {
        return jblmLogger.getName();
    }

    /**
     * Returns the parent of this category. Note that the parent of a given category may change during the lifetime of
     * the category.
     * <p/>
     * <p>The root category will return <code>null</code>.
     *
     * @since 1.2
     */
    public final Category getParent() {
        return parent;
    }

    /**
     * Returns the assigned {@link org.apache.log4j.Level}, if any, for this Category.
     *
     * @return Level - the assigned Level, can be <code>null</code>.
     */
    public final Level getLevel() {
        synchronized (LEVEL_LOCK) {
            if (level != null) {
                final Level currentLevel = LevelMapping.getPriorityFor(jblmLogger.getLevel());
                if (currentLevel.toInt() != level.toInt()) {
                    jblmLogger.setLevel(LevelMapping.getLevelFor(level));
                }
            }
        }
        return level;
    }

    /**
     * @deprecated Please use {@link #getLevel} instead.
     */
    public final Level getPriority() {
        return getLevel();
    }

    /**
     * @deprecated Please use {@link org.apache.log4j.Logger#getRootLogger()} instead.
     */
    public static Category getRoot() {
        return LogManager.getRootLogger();
    }

    /**
     * Return the <em>inherited</em> {@link java.util.ResourceBundle} for this category.
     * <p/>
     * <p>This method walks the hierarchy to find the appropriate resource bundle. It will return the resource bundle
     * attached to the closest ancestor of this category, much like the way priorities are searched. In case there is
     * no
     * bundle in the hierarchy then <code>null</code> is returned.
     *
     * @since 0.9.0
     */
    public ResourceBundle getResourceBundle() {
        return jblmLogger.getResourceBundle();
    }

    /**
     * Returns the string resource coresponding to <code>key</code> in this category's inherited resource bundle. See
     * also {@link #getResourceBundle}.
     * <p/>
     * <p>If the resource cannot be found, then an {@link #error error} message will be logged complaining about the
     * missing resource.
     */
    protected String getResourceBundleString(String key) {
        ResourceBundle rb = getResourceBundle();
        // This is one of the rare cases where we can use logging in order
        // to report errors from within log4j.
        if (rb == null) {
            return null;
        } else {
            try {
                return rb.getString(key);
            } catch (MissingResourceException mre) {
                error("No resource is associated with key \"" + key + "\".");
                return null;
            }
        }
    }

    /**
     * Log a message object with the {@link org.apache.log4j.Level#INFO INFO} Level.
     * <p/>
     * <p>This method first checks if this category is <code>INFO</code> enabled by comparing the level of this
     * category
     * with {@link org.apache.log4j.Level#INFO INFO} Level. If the category is <code>INFO</code> enabled, then it
     * converts the message object passed as parameter to a string by invoking the appropriate {@link
     * org.apache.log4j.or.ObjectRenderer}. It proceeds to call all the registered appenders in this category and also
     * higher in the hierarchy depending on the value of the additivity flag.
     * <p/>
     * <p><b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but
     * no stack trace. To print a stack trace use the {@link #info(Object, Throwable)} form instead.
     *
     * @param message the message object to log
     */
    public void info(Object message) {
        forcedLog(FQCN, Level.INFO, message, null);
    }

    /**
     * Log a message object with the <code>INFO</code> level including the stack trace of the {@link Throwable}
     * <code>t</code> passed as parameter.
     * <p/>
     * <p>See {@link #info(Object)} for more detailed information.
     *
     * @param message the message object to log.
     * @param t       the exception to log, including its stack trace.
     */
    public void info(Object message, Throwable t) {
        forcedLog(FQCN, Level.INFO, message, t);
    }

    /**
     * Is the appender passed as parameter attached to this category?
     */
    public boolean isAttached(Appender appender) {
        return AppenderHandler.isAppenderAttached(jblmLogger, appender);
    }

    /**
     * Check whether this category is enabled for the <code>DEBUG</code> Level.
     * <p/>
     * <p> This function is intended to lessen the computational cost of disabled log debug statements.
     * <p/>
     * <p> For some <code>cat</code> Category object, when you write,
     * <pre>
     *      cat.debug("This is entry number: " + i );
     *  </pre>
     *
     * <p>You incur the cost constructing the message, concatenatiion in this case, regardless of whether the message
     * is
     * logged or not.
     *
     * <p>If you are worried about speed, then you should write
     * <pre>
     * 	 if(cat.isDebugEnabled()) {
     * 	   cat.debug("This is entry number: " + i );
     *      }
     *  </pre>
     *
     * <p>This way you will not incur the cost of parameter construction if debugging is disabled for <code>cat</code>.
     * On the other hand, if the <code>cat</code> is debug enabled, you will incur the cost of evaluating whether the
     * category is debug enabled twice. Once in <code>isDebugEnabled</code> and once in the <code>debug</code>.  This
     * is
     * an insignificant overhead since evaluating a category takes about 1%% of the time it takes to actually log.
     *
     * @return boolean - <code>true</code> if this category is debug enabled, <code>false</code> otherwise.
     */
    public boolean isDebugEnabled() {
        return jblmLogger.isLoggable(org.jboss.logmanager.Level.DEBUG);
    }

    /**
     * Check whether this category is enabled for a given {@link org.apache.log4j.Level} passed as parameter.
     * <p/>
     * See also {@link #isDebugEnabled}.
     *
     * @return boolean True if this category is enabled for <code>level</code>.
     */
    public boolean isEnabledFor(Priority level) {
        return jblmLogger.isLoggable(LevelMapping.getLevelFor(level));
    }

    /**
     * Check whether this category is enabled for the info Level. See also {@link #isDebugEnabled}.
     *
     * @return boolean - <code>true</code> if this category is enabled for level info, <code>false</code> otherwise.
     */
    public boolean isInfoEnabled() {
        return jblmLogger.isLoggable(org.jboss.logmanager.Level.INFO);
    }

    /**
     * Log a localized message. The user supplied parameter <code>key</code> is replaced by its localized version from
     * the resource bundle.
     *
     * @see #setResourceBundle
     * @since 0.8.4
     */
    public void l7dlog(Priority priority, String key, Throwable t) {
        if (jblmLogger.isLoggable(LevelMapping.getLevelFor(priority))) {
            String msg = getResourceBundleString(key);
            // if message corresponding to 'key' could not be found in the
            // resource bundle, then default to 'key'.
            if (msg == null) {
                msg = key;
            }
            forcedLog(FQCN, priority, msg, t);
        }
    }

    /**
     * Log a localized and parameterized message. First, the user supplied <code>key</code> is searched in the resource
     * bundle. Next, the resulting pattern is formatted using {@link java.text.MessageFormat#format(String, Object[])}
     * method with the user supplied object array <code>params</code>.
     *
     * @since 0.8.4
     */
    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        if (jblmLogger.isLoggable(LevelMapping.getLevelFor(priority))) {
            String pattern = getResourceBundleString(key);
            String msg;
            if (pattern == null) msg = key;
            else msg = java.text.MessageFormat.format(pattern, params);
            forcedLog(FQCN, priority, msg, t);
        }
    }

    /**
     * This generic form is intended to be used by wrappers.
     */
    public void log(Priority priority, Object message, Throwable t) {
        forcedLog(FQCN, priority, message, t);
    }

    /**
     * This generic form is intended to be used by wrappers.
     */
    public void log(Priority priority, Object message) {
        forcedLog(FQCN, priority, message, null);
    }

    /**
     * This is the most generic printing method. It is intended to be invoked by <b>wrapper</b> classes.
     *
     * @param callerFQCN The wrapper class' fully qualified class name.
     * @param level      The level of the logging request.
     * @param message    The message of the logging request.
     * @param t          The throwable of the logging request, may be null.
     */
    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
        forcedLog(callerFQCN, level, message, t);
    }

    /**
     * LoggerRepository forgot the fireRemoveAppenderEvent method, if using the stock Hierarchy implementation, then
     * call its fireRemove. Custom repositories can implement HierarchyEventListener if they want remove notifications.
     *
     * @param appender appender, may be null.
     */
    private void fireRemoveAppenderEvent(final LoggerRepository repository, final Appender appender) {
        if (appender != null) {
            if (repository instanceof Hierarchy) {
                ((Hierarchy) repository).fireRemoveAppenderEvent(this, appender);
            } else if (repository instanceof HierarchyEventListener) {
                ((HierarchyEventListener) repository).removeAppenderEvent(this, appender);
            }
        }
    }

    /**
     * Remove all previously added appenders from this Category instance.
     * <p/>
     * <p>This is useful when re-reading configuration information.
     */
    public void removeAllAppenders() {
        final List<Appender> removedAppenders = AppenderHandler.removeAllAppenders(jblmLogger);
        final LoggerRepository repository = LogManagerFacade.getLoggerRepository();
        for (Appender appender : removedAppenders) {
            fireRemoveAppenderEvent(repository, appender);
        }
    }

    /**
     * Remove the appender passed as parameter form the list of appenders.
     *
     * @since 0.8.2
     */
    public void removeAppender(Appender appender) {
        if (appender != null) {
            if (AppenderHandler.removeAppender(jblmLogger, appender)) {
                fireRemoveAppenderEvent(LogManagerFacade.getLoggerRepository(), appender);
            }
        }
    }

    /**
     * Remove the appender with the name passed as parameter form the list of appenders.
     *
     * @since 0.8.2
     */
    public void removeAppender(String name) {
        if (name != null) {
            removeAppender(AppenderHandler.getAppender(jblmLogger, name));
        }
    }

    /**
     * Set the additivity flag for this Category instance.
     *
     * @since 0.8.1
     */
    public void setAdditivity(boolean additive) {
        jblmLogger.setUseParentHandlers(additive);
    }

    /**
     * Only the Hiearchy class can set the hiearchy of a category. Default package access is MANDATORY here.
     */
    final void setHierarchy(LoggerRepository repository) {
        // no-op
    }

    /**
     * Set the level of this Category. If you are passing any of <code>Level.DEBUG</code>, <code>Level.INFO</code>,
     * <code>Level.WARN</code>, <code>Level.ERROR</code>, <code>Level.FATAL</code> as a parameter, you need to case
     * them
     * as Level.
     * <p/>
     * <p>As in <pre> &nbsp;&nbsp;&nbsp;logger.setLevel((Level) Level.DEBUG); </pre>
     * <p/>
     * <p/>
     * <p>Null values are admitted.
     */
    public void setLevel(Level level) {
        synchronized (LEVEL_LOCK) {
            jblmLogger.setLevel(LevelMapping.getLevelFor(level));
            this.level = level;
        }
    }

    /**
     * Set the level of this Category.
     * <p/>
     * <p>Null values are admitted.
     *
     * @deprecated Please use {@link #setLevel} instead.
     */
    public void setPriority(Priority priority) {
        setLevel((Level) priority);
    }

    /**
     * Set the resource bundle to be used with localized logging methods {@link #l7dlog(org.apache.log4j.Priority,
     * String, Throwable)} and {@link #l7dlog(org.apache.log4j.Priority, String, Object[], Throwable)}.
     *
     * @since 0.8.4
     */
    public void setResourceBundle(ResourceBundle bundle) {
        // no-op
    }

    /**
     * Calling this method will <em>safely</em> close and remove all appenders in all the categories including root
     * contained in the default hierachy.
     * <p/>
     * <p>Some appenders such as {@link org.apache.log4j.net.SocketAppender} and {@link org.apache.log4j.AsyncAppender}
     * need to be closed before the application exists. Otherwise, pending logging events might be lost.
     * <p/>
     * <p>The <code>shutdown</code> method is careful to close nested appenders before closing regular appenders. This
     * is allows configurations where a regular appender is attached to a category and again to a nested appender.
     *
     * @since 1.0
     * @deprecated Please use {@link org.apache.log4j.LogManager#shutdown()} instead.
     */
    public static void shutdown() {
        LogManager.shutdown();
    }

    /**
     * Log a message object with the {@link org.apache.log4j.Level#WARN WARN} Level.
     * <p/>
     * <p>This method first checks if this category is <code>WARN</code> enabled by comparing the level of this
     * category
     * with {@link org.apache.log4j.Level#WARN WARN} Level. If the category is <code>WARN</code> enabled, then it
     * converts the message object passed as parameter to a string by invoking the appropriate {@link
     * org.apache.log4j.or.ObjectRenderer}. It proceeds to call all the registered appenders in this category and also
     * higher in the hieararchy depending on the value of the additivity flag.
     * <p/>
     * <p><b>WARNING</b> Note that passing a {@link Throwable} to this method will print the name of the Throwable but
     * no stack trace. To print a stack trace use the {@link #warn(Object, Throwable)} form instead.  <p>
     *
     * @param message the message object to log.
     */
    public void warn(Object message) {
        forcedLog(FQCN, Level.WARN, message, null);
    }

    /**
     * Log a message with the <code>WARN</code> level including the stack trace of the {@link Throwable} <code>t</code>
     * passed as parameter.
     * <p/>
     * <p>See {@link #warn(Object)} for more detailed information.
     *
     * @param message the message object to log.
     * @param t       the exception to log, including its stack trace.
     */
    public void warn(Object message, Throwable t) {
        forcedLog(FQCN, Level.WARN, message, t);
    }
}
