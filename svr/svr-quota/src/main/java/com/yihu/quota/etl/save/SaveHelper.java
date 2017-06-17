package com.yihu.quota.etl.save;

import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.save.es.ElastricSearchSave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.service.save.TjDataSaveService;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chenweida on 2017/6/2.
 */
@Component
@Scope("prototype")
public class SaveHelper {
    @Autowired
    private TjDataSaveService datsSaveService;

    public Boolean save(List<SaveModel> dataModels, QuotaVo quotaVo) {
        //查看指标保存的数据源
        TjQuotaDataSave quotaDataSave = datsSaveService.findByQuota(quotaVo.getCode());
        switch (quotaDataSave.getType()) {
            case Contant.save.mysql: {
                return null;
            }
            case Contant.save.es: {
                return SpringUtil.getBean(ElastricSearchSave.class).save(dataModels,quotaDataSave.getConfigJson());
            }
        }
        return false;
    }
}
