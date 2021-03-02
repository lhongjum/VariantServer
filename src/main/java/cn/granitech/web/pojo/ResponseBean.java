package cn.granitech.web.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/26 15:11
 * @Version: 1.0
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseBean<T> {

    private Integer code;

    private String error;

    private String message;

    private T data;

//    private BigDecimal testBd = new BigDecimal("101.31");
//
//    public BigDecimal getTestBd() {
//        return testBd;
//    }
//
//    public void setTestBd(BigDecimal testBd) {
//        this.testBd = testBd;
//    }

    public ResponseBean(Integer code, String error, String message, T data) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
