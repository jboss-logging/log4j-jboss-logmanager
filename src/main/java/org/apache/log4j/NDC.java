package org.apache.log4j;

import java.util.Stack;

public class NDC {

    private NDC() {}

    public static void clear() {
        org.jboss.logmanager.NDC.clear();
    }

    public static Stack cloneStack() {
        final Stack<String> stack = new Stack<String>();
        final int depth = org.jboss.logmanager.NDC.getDepth();
        for (int i = 0; i < depth; i ++) {
            stack.push(org.jboss.logmanager.NDC.get(i));
        }
        return stack;
    }

    public static void inherit(Stack stack) {
        if (stack != null) {
            org.jboss.logmanager.NDC.clear();
            for (Object item : stack) {
                if (item != null) org.jboss.logmanager.NDC.push(item.toString());
            }
        }
    }

    public static String get() {
        return org.jboss.logmanager.NDC.get();
    }

    public static int getDepth() {
        return org.jboss.logmanager.NDC.getDepth();
    }

    public static String pop() {
        return org.jboss.logmanager.NDC.pop();
    }

    public static String peek() {
        return org.jboss.logmanager.NDC.get();
    }

    public static void push(String message) {
        org.jboss.logmanager.NDC.push(message);
    }

    public static void remove() {
        clear();
    }

    public static void setMaxDepth(int maxDepth) {
        org.jboss.logmanager.NDC.trimTo(maxDepth);
    }
}

