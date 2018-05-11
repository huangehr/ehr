package com.yihu.quota.etl.conver;

import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.vo.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2018/5/9.
 * 维度的key值转换器
 */
@Component
@Scope("prototype")
public class ConvertHelper {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 维度的key清洗到对应的slaveKey中
     * @param filterModel     过滤之后的model
     * @param tjQuotaDimensionSlave 维度
     * @return
     * @throws Exception
     */
    public FilterModel convert(FilterModel filterModel, TjQuotaDimensionSlave tjQuotaDimensionSlave) throws Exception {
        List<Map<String, Object>> data = filterModel.getDataList();
        String clazz = tjQuotaDimensionSlave.getConverClass();
        if (!StringUtils.isEmpty(clazz)) {
            //反射出对象并且调用convert方法去转换对应的slavekey
            Object obj = Class.forName(clazz).newInstance();
            Method method = obj.getClass().getMethod("convert", JdbcTemplate.class, List.class, TjQuotaDimensionSlave.class);
            data = (List<Map<String, Object>>) method.invoke(obj, jdbcTemplate, data, tjQuotaDimensionSlave);
            filterModel.setDataList(data);
        }
        return filterModel;
    }
}
