package cn.granitech.interceptor;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/9/1 11:59
 * @Version: 1.0
 */

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        SessionIPChecker.removeExpiredSession(sessionId);
    }

}
