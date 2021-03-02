package cn.granitech.autoconfig;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/10 12:20
 * @Version: 1.0
 */

@Configuration
public class JacksonConfiguration {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static class DateSerializer extends JsonSerializer<java.sql.Date> {

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (date == null) {
                jsonGenerator.writeObject(null);
            } else {
                jsonGenerator.writeString( dateFormat.format(date) );
            }
        }
    }

    static class DateTimeSerializer extends JsonSerializer<java.util.Date> {

        @Override
        public void serialize(java.util.Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (date == null) {
                jsonGenerator.writeObject(null);
            } else {
                jsonGenerator.writeString( dateTimeFormat.format(date) );
            }
        }
    }

    @Bean
    public SimpleModule simpleModule() {
        SimpleModule simpleModule = new SimpleModule();
        DateSerializer dateSerializer = new DateSerializer();
        DateTimeSerializer dateTimeSerializer = new DateTimeSerializer();
        simpleModule.addSerializer(java.sql.Date.class, dateSerializer);
        simpleModule.addSerializer(java.util.Date.class, dateTimeSerializer);
        return simpleModule;
    }

}
