package com.yihu.ehr.resolve.service.resource.stage1.extractor;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 就诊卡信息提取。
 *
 * @author hzp
 * @version 2.0
 * @created 2017.04.23
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.card")
public class CardInfoExtractor extends KeyDataExtractor {

    //就诊卡界定数据集
    private List<String> dataSets = new ArrayList<>();
    private List<String> cardNum = new ArrayList<>();
    private List<String> cardType = new ArrayList<>();

    /**
     * 获取此数据集中的卡信息
     * @param dataSet
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashedMap();
        String id = "";
        String type = "";
        //获取就诊卡号和卡类型
        if (dataSets.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
                    MetaDataRecord record = dataSet.getRecord(key);
                    if (StringUtils.isEmpty(id)) {
                        for (String item : cardNum) {
                            String _id = record.getMetaData(item);
                            if (!StringUtils.isEmpty(_id)) {
                                id = _id;
                                break;
                            }
                        }
                    }
                    if (StringUtils.isEmpty(type)) {
                        for (String item : cardType) {
                            String _type = record.getMetaData(item);
                            if (!StringUtils.isEmpty(_type)) {
                                type = _type;
                                break;
                            }
                        }
                    }
                } else {
                    break;
                }
            }
        }
        properties.put(MasterResourceFamily.BasicColumns.CardId, id);
        properties.put(MasterResourceFamily.BasicColumns.CardType, type);
        return properties;
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }

    public List<String> getCardNum() {
        return cardNum;
    }

    public List<String> getCardType() {
        return cardType;
    }

}
