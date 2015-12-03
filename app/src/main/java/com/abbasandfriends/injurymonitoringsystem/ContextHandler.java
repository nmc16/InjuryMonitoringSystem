package com.abbasandfriends.injurymonitoringsystem;

import java.util.Hashtable;

/**
 * Singleton helper class to pass the Connection handlers and other
 * objects between the activities.
 *
 * @version 1
 */
public class ContextHandler {
    private static ContextHandler instance;
    private Hashtable<String, Object> context;
    public static final String RECEIVE = "receive";
    public static final String REQUEST = "request";
    public static final String HANDLER = "handler";

    private ContextHandler() {
        context = new Hashtable<String, Object>();
    }

    private static ContextHandler getInstance() {
        if (instance == null) {
            instance = new ContextHandler();
        }

        return instance;
    }

    public static void add(String key, Object o) {
        getInstance().context.put(key, o);
    }

    public static Object get(String key) {
        return getInstance().context.get(key);
    }

}
