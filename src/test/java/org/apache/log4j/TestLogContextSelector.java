package org.apache.log4j;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.LogContextSelector;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestLogContextSelector implements LogContextSelector {
    private static final Map<String, TestLogContextSelector> contexts = new HashMap<String, TestLogContextSelector>();

    private final LogContext context;

    TestLogContextSelector(final LogContext context) {
        this.context = context;
    }

    public static TestLogContextSelector forClass(final Class<?> clazz) {
        final String name = clazz.getName();
        if (contexts.containsKey(name)) {
            return contexts.get(name);
        }
        final TestLogContextSelector result = new TestLogContextSelector(LogContext.create());
        contexts.put(name, result);
        return  result;
    }


    @Override
    public LogContext getLogContext() {
        return context;
    }
}
