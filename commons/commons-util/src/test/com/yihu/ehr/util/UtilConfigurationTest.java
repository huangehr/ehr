package com.yihu.ehr.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Sand
 * @created 2016.04.26 17:50
 */
public class UtilConfigurationTest {
    public static class Test{
        Date date;
        String name;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @org.junit.Test
    public void objectMapper() throws IOException {
        Test test = new Test();
        test.setName("Sand");
        test.setDate(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);

        String value = objectMapper.writeValueAsString(test);
        Test anotherTest = objectMapper.readValue(value, Test.class);

        System.out.println(value);
    }
}