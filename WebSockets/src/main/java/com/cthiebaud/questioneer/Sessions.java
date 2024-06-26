package com.cthiebaud.questioneer;

import java.io.IOException;
import java.util.Set;

import jakarta.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

public enum Sessions {
    INSTANCE;

    final private Set<Session> activeSessions = ConcurrentHashMap.newKeySet();

    public void register(Session session) {
        System.out.println("added web socket " + session.getId());
        activeSessions.add(session);
    }

    public void unregister(Session session) {
        System.out.println("removed web socket " + session.getId());
        activeSessions.remove(session);
    }

    public void broadcast(Integer count) {
        for (Session session : activeSessions) {
            try {
                session.getBasicRemote().sendText(String.format("%d", count));
            } catch (IOException e) {
                // throw new RuntimeException(e);
                System.err.println("Could not send text to " + session.getId());
            }
        }
    }
}
