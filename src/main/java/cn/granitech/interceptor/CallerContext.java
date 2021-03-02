package cn.granitech.interceptor;

import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/28 16:38
 * @Version: 1.0
 */

@Service
public class CallerContext {

    private ThreadLocal<String> callerIdLocal = new ThreadLocal<>();

    public void setCallerId(String callerId) {
        callerIdLocal.set(callerId);
    }

    public String getCallerId() {
        return callerIdLocal.get();
    }

}
