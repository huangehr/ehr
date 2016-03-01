package com.yihu.ehr.standard.standardsource.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.1.20
 */
@Service
@Transactional
public class StdSourceService extends BaseJpaService<StandardSource, XStdSourceRepository> {

    @Autowired
    XStdSourceRepository standardSourceRepository;

    /**
     * 根据ID删除标准来源
     *
     * @param ids
     */
    public int deleteSource(List<String> ids) {
        int result = 0;
        try {
            if (ids == null || ids.size() == 0)
                return 0;
            result = standardSourceRepository.deleteByIdIn(ids);
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }


    /**
     * 判断是否code已存在
     *
     * @param code
     * @return
     */
    public boolean isSourceCodeExist(String code) {

        return getCount("code=" + code) > 0;
    }
}