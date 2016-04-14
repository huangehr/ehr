package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.model.profile.MRecord;

import java.io.IOException;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 9:38
 */
public class ProfileSerializer extends JsonSerializer<MDataSet> {
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

    /*@Override
    public void serialize(MProfile mProfile, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField("id", mProfile.getId());
        jgen.writeStringField("demographicId", mProfile.getDemographicId());
        jgen.writeObjectField("eventDate", mProfile.getEventDate());
        jgen.writeStringField("orgName", mProfile.getOrgCode());
        jgen.writeStringField("orgCode", mProfile.getOrgName());
        jgen.writeStringField("cdaVersion", mProfile.getCdaVersion());
        jgen.writeStringField("summary", mProfile.getSummary());

        jgen.write
        jgen.writeEndObject();
    }*/
}
