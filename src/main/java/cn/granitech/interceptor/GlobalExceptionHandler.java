package cn.granitech.interceptor;

import cn.granitech.util.ResponseHelper;
import cn.granitech.web.pojo.ResponseBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: Controller类全局异常处理
 * @Author: VDP 思南
 * @Date: 2021/1/22 12:33
 * @Version: 1.0
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseBean exceptionHandler(HttpServletRequest req, Exception e){  /* 必须有HttpServletRequest参数才生效！！ */
        e.printStackTrace();
        return ResponseHelper.fail(null, StringUtils.isBlank(e.getMessage()) ? "后台错误，请查看日志" : e.getMessage());
    }

}
