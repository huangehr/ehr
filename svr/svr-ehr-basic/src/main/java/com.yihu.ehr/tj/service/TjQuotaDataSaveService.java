package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjQuotaDataSave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaDataSaveRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjQuotaDataSaveService extends BaseJpaService<TjQuotaDataSave,XTjQuotaDataSaveRepository> {
    @Autowired
    XTjQuotaDataSaveRepository tjQuotaDataSaveRepository;

    public TjQuotaDataSave getByQuotaCode(String quotaCode) {
        List<TjQuotaDataSave> tjQuotaDataSave = tjQuotaDataSaveRepository.getByQuotaCode(quotaCode);
        if (tjQuotaDataSave != null && tjQuotaDataSave.size() > 0) {
            return tjQuotaDataSave.get(0);
        }
        return null;
    }
}
