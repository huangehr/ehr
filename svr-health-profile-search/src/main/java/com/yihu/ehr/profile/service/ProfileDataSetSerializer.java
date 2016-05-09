package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.model.profile.MRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 9:38
 */
@Component
@DependsOn("objectMapper")
public class ProfileDataSetSerializer extends JsonSerializer<MDataSet> {
    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        SimpleModule module = new SimpleModule();
        module.addSerializer(MDataSet.class, this);
        objectMapper.registerModule(module);

        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);
    }

    @Override
    public void serialize(MDataSet dataSet, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("code", dataSet.getCode());
        jgen.writeStringField("name", dataSet.getName());

        jgen.writeObjectFieldStart("records");

        Map<String, MRecord> records = dataSet.getRecords();
        for (String key : records.keySet()){
            MRecord record = records.get(key);
            if (record != null && record.getCells() != null){
                jgen.writeObjectFieldStart(key);

                for (String cell : record.getCells().keySet()){
                    jgen.writeStringField(cell, record.getCells().get(cell));
                }

                jgen.writeEndObject();
            } else {
                jgen.writeStringField(key, null);
            }
        }

        jgen.writeEndObject();
        jgen.writeEndObject();
    }
}
