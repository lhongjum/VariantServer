package cn.granitech.interceptor;

import cn.granitech.util.JsonHelper;
import cn.granitech.web.constant.SessionNames;
import cn.granitech.web.pojo.ResponseBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/25 12:26
 * @Version: 1.0
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    CallerContext callerContext;

    //TODO: 拦截器路径可能需要调整！！
    private static final String SKIP_INTERCEPTOR_PATH = ".*/((index.html)|(.*.ico)|(css/)|(js/)|(fonts/)|(login)|(picture/get)|(file/get)|(anon)).*";
    //private static final String SKIP_INTERCEPTOR_PATH = ".*/((.css)|(.ico)|(.js)|(images)|(login)|(usertest)|(anon)).*";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        if (path.matches(SKIP_INTERCEPTOR_PATH)) {
            return true;
        }

        System.err.println("path: " + path);

        ResponseBean<String> rBean = new ResponseBean<String>(403, "请登录", "用户未登录", "");
        String uId = getCookieUid(request);
        if (StringUtils.isBlank(uId)) {
            JsonHelper.writeResponseJson(response, rBean);
            return false;
        }

        String headerUId = getHeaderUserId(request);
        if (StringUtils.isBlank(headerUId)) {
            JsonHelper.writeResponseJson(response, rBean);
            return false;
        }

        HttpSession session = request.getSession(false);
        if ((session == null) || (session.getAttribute(SessionNames.LoginUserId) == null)) {
            JsonHelper.writeResponseJson(response, rBean);
            return false;
        } else {
            boolean checkResult = SessionIPChecker.checkRequestIP(request, session.getId());
            if (!checkResult) {
                ResponseBean<String> checkFailedBean = new ResponseBean<String>(500, "session被冒用", "非法用户请求", "");
                JsonHelper.writeResponseJson(response, checkFailedBean);
                return false;
            }
        }

        String sId = (String) session.getAttribute(SessionNames.LoginUserId);
        if (!uId.equalsIgnoreCase(sId) || !uId.equals(headerUId)) {
            JsonHelper.writeResponseJson(response, rBean);
            return false;
        } else {
            callerContext.setCallerId(uId);  //设置调用者Id线程变量
            return true;
        }
    }

    private String getCookieUid(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ( "uid".equalsIgnoreCase(cookie.getName()) ) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private String getHeaderUserId(HttpServletRequest request) {
        return request.getHeader("ruId");  // 前端保存在SessionStorage中的登录用户Id
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清空线程变量
        callerContext.setCallerId(null);
    }
}
