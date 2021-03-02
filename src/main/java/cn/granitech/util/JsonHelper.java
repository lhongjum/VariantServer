package cn.granitech.util;

import cn.granitech.web.pojo.layout.LayoutJsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/28 12:38
 * @Version: 1.0
 */

public class JsonHelper {

    public static void writeResponseJson(HttpServletResponse response, Object object) throws IOException {
        //response.setHeader("Content-type", "application/json;charset=UTF-8"); //老版本IE浏览器会弹出下载文件框！！
        response.setHeader("Content-type", "text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        writer.print( objectMapper.writeValueAsString(object) );
        writer.close();
        response.flushBuffer();
    }

    public static String writeObjectAsString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    /* 反序列化不支持泛型，泛型反序列化后得到是map对象，必须指定具体类型！！ */
    /*
    public static<T> T readJsonObject(String jsonStr) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = objectMapper.readValue(jsonStr, new TypeReference<T>(){});
        return result;
    }
    */

//    public static LayoutJsonObject readLayoutJsonObject(String jsonStr) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  /* 跳过未映射的json属性，不报错！ */
//        return objectMapper.readValue(jsonStr, new TypeReference<LayoutJsonObject>(){});
//    }

    public static<T> T readJsonValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  /* 跳过未映射的json属性，不报错！ */
        return objectMapper.readValue(content, valueTypeRef);
    }

}
