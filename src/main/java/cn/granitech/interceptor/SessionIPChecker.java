package cn.granitech.interceptor;

import cn.granitech.util.IPHelper;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/9/1 12:19
 * @Version: 1.0
 */

public class SessionIPChecker {

    private static Map<String, String> sessionIdIPMap = new HashMap<>();

    public synchronized static void addSessionIdIP(HttpServletRequest request, String sessionId) {
        String requestIP = IPHelper.getRequestIp(request);
        if (StringUtils.isNotBlank(requestIP)) {
            sessionIdIPMap.put(sessionId, requestIP);
        }
    }

    public synchronized static boolean checkRequestIP(HttpServletRequest request, String sessionId) {
        //return true;

        ///*
        if (sessionIdIPMap.containsKey(sessionId)) {
            String requestIP = IPHelper.getRequestIp(request);
            if (sessionIdIPMap.get(sessionId).equals(requestIP)) {
                return true;
            }

            return false;
        }

        return false;
        //*/
    }

    public synchronized static void removeExpiredSession(String sessionId) {
        sessionIdIPMap.remove(sessionId);
    }

}
