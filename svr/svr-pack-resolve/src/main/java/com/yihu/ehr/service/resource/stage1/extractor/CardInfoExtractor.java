package com.yihu.ehr.service.resource.stage1.extractor;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 就诊卡信息提取。
 *
 * @author hzp
 * @version 2.0
 * @created 2017.04.23
 */
@Component
@ConfigurationProperties(prefix="ehr.pack-extractor.card")
public class CardInfoExtractor extends KeyDataExtractor {
    private List<String> dataSets = new ArrayList<>();        // 包含卡信息的数据集

    @Value("${ehr.pack-extractor.card.meta-data.card-no}")
    private String CardType;

    @Value("${ehr.pack-extractor.card.meta-data.card-no}")
    private String CardId;


    /**
     * 获取此数据集中的卡信息.
     *
     * @return
     */
    @Override
    public Map<String,Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashedMap();

        String cardId = "";
        String cardType = "";
        //获取就诊卡号和卡类型
        if (dataSets.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(key);

                //获取就诊卡号
                if(StringUtils.isEmpty(cardId)) {
                    String val = record.getMetaData(CardId);
                    if (val != null) {
                        cardId = val;
                        cardType = record.getMetaData(CardType);
                        break;
                    }
                }
            }
        }

        properties.put(MasterResourceFamily.BasicColumns.CardId,cardId);
        properties.put(MasterResourceFamily.BasicColumns.CardType,cardType);
        return properties;
    }
}
