package cn.granitech.util;

import cn.granitech.web.pojo.ResponseBean;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/9/3 16:20
 * @Version: 1.0
 */

public class ResponseHelper {

    public static<T> ResponseBean<T> ok(T object, String message) {
        return new ResponseBean<>(200, null, message, object);
    }

    public static<T> ResponseBean<T> fail(T object, String error) {
        return new ResponseBean<>(500, error, null, object);
    }

}
