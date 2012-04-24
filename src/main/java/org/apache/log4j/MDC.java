package org.apache.log4j;

import java.util.Hashtable;

public class MDC {

    static final MDC mdc = new MDC();

    // preserve fields as closely as possible
    static final int HT_SIZE = 7;
    boolean java1 = false;
    Object tlm = new Object();

    public static void put(String key, Object o) {
        org.jboss.logmanager.MDC.put(key, o == null ? null : o.toString());
    }

    public static Object get(String key) {
        return org.jboss.logmanager.MDC.get(key);
    }

    public static void remove(String key) {
        org.jboss.logmanager.MDC.remove(key);
    }

    @SuppressWarnings("unchecked")
    public static Hashtable getContext() {
        return new Hashtable(org.jboss.logmanager.MDC.copy());
    }

    public static void clear() {
        org.jboss.logmanager.MDC.clear();
    }
}
