package com.cthiebaud.questioneer;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import jakarta.servlet.ServletContext;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setMaxInactiveInterval(30);

        System.out.println(
                String.format("Session [%s] created. Will be destroyed in %d seconds",
                        session.getId(),
                        session.getMaxInactiveInterval()));

        counterUpdate(session.getServletContext(), 1);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println(
                String.format("Session [%s] destroyed.",
                        session.getId()));

        counterUpdate(session.getServletContext(), -1);
    }

    private void counterUpdate(ServletContext servletContext, int howmuch) {
        Integer counter;
        synchronized (this) {
            counter = (Integer) servletContext.getAttribute("counter");
            if (counter == null) {
                counter = 0;
            }
            if (counter + howmuch >= 0) {
                counter += howmuch;
            }

            servletContext.setAttribute("counter", counter);

            Sessions.INSTANCE.broadcast(counter);
        }

    }
}
