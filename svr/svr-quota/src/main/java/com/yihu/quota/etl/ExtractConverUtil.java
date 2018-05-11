package com.yihu.quota.etl;

import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.conver.ConvertHelper;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.FilterModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by janseny on 2018/5/10.
 */
@Component
@Scope("prototype")
public class ExtractConverUtil {

    private Logger logger = LoggerFactory.getLogger(ExtractConverUtil.class);

    /**
     * 细维度数据转换
     * @param filterModel
     * @param qds
     * @return
     */
    public FilterModel convert(FilterModel filterModel, List<TjQuotaDimensionSlave> qds) throws Exception {
        try {
            if( qds != null && qds.size() > 0  && filterModel.getDataList()!= null  && filterModel.getDataList().size() > 0){
                for(TjQuotaDimensionSlave slave : qds){
                    if(  !StringUtils.isEmpty(slave.getConverClass())){
                        return SpringUtil.getBean(ConvertHelper.class).convert(filterModel, slave);
                    }
                }
            }else {
                return  filterModel;
            }
        } catch (Exception e) {
            throw new Exception("数据转换异常"+e.getMessage());
        }
        return null;
    }

}
