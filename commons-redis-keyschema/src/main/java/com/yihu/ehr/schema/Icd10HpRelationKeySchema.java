package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author linaz
 * @created 2016.05.28 15:29
 */
@Component
public class Icd10HpRelationKeySchema extends KeySchema {

    @Value("${ehr.redis-key-schema.hp_icd10_relation.table}")
    private String Table = "hp_icd10_relation";

    @Value("${ehr.redis-key-schema.hp_icd10_relation.icd10_id}")
    private String Name = "icd10_id";

    public String icd10HpRelation(String codeName){
        return makeKey(Table, codeName, Name);
    }
}
