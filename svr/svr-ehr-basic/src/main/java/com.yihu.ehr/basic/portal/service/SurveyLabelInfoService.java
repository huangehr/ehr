package com.yihu.ehr.basic.portal.service;


import com.yihu.ehr.basic.portal.dao.SurveyLabelInfoDao;
import com.yihu.ehr.basic.portal.model.SurveyLabelInfo;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问卷调查标签信息表
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyLabelInfoService extends BaseJpaService<SurveyLabelInfo,Long> {
    @Autowired
    SurveyLabelInfoDao surveyLabelInfoDao;


    /**
     * 根据类型获取标签数据
     * @param useType 0模板 1问卷
     * @return
     */
    List<SurveyLabelInfo> findByUseType (Integer useType){
        return surveyLabelInfoDao.findByUseType(useType);
    }

    /**
     * 根据类型和关联编码获取标签数据
     * @param useType 0模板 1问卷
     *  @param relationCode 关联编码
     * @return
     */
    List<SurveyLabelInfo> findByUseTypeAndRelationCode (Integer useType,String relationCode){
        return surveyLabelInfoDao.findByUseTypeAndRelationCode(useType,relationCode);
    }

    /**
     * 根据类型和关联CODE删除标签
     * @param useType
     * @param relationCode
     */
     void  deleteByUseTypeAndRelationCode(Integer useType,String relationCode){
          surveyLabelInfoDao.deleteByUseTypeAndRelationCode(useType,relationCode);
     }

     public void save(List<SurveyLabelInfo> surveyLabelInfos){
         surveyLabelInfoDao.save(surveyLabelInfos);
     }

     public SurveyLabelInfo save(SurveyLabelInfo surveyLabelInfo){
         return surveyLabelInfoDao.save(surveyLabelInfo);
     }


}
