package com.yihu.ehr.standard.cdatype.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.standard.cdatype.service.CDAType;
import com.yihu.ehr.standard.cdatype.service.CDATypeManager;
import com.yihu.ehr.standard.cdatype.service.MCDAType;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by AndyCai on 2015/12/14.
 */
@RequestMapping("/cdatype")
@RestController
public class CdaTypeController extends BaseRestController {

    @Autowired
    private CDATypeManager cdaTypeManager;
    
    @RequestMapping("getTreeGridData")
    @ResponseBody
    public List<CDAType> getTreeGridData() throws Exception {
        List<CDAType> listType = cdaTypeManager.getCDATypeListByParentId("");
        return listType;
    }

    @RequestMapping("getParentType")
    @ResponseBody
    public List<CDAType> getParentType(String[] Ids, String key) throws Exception {
        List<CDAType> listParentType = cdaTypeManager.getCdatypeInfoByIds(Ids); //type 列表
        String childrenIds = getCdaTypeChildId(listParentType,"");
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        List<CDAType> typeList = cdaTypeManager.getParentType(childrenIds,key);
       return typeList;

    }


    /**
     * 根据父级类别获取全部的子类别ID，返回值包括父级的ID
     * @param info 父级信息
     * @param childrenIds   子级ID
     * @return 全部子级
     */
    public String getCdaTypeChildId(List<CDAType> info,String childrenIds) {
        for (int i = 0; i < info.size(); i++) {
            CDAType typeInfo = (CDAType) info.get(i);
            childrenIds+=typeInfo.getId()+",";
            List<CDAType> listChild = cdaTypeManager.getCDATypeListByParentId(typeInfo.getId());
            if(listChild.size()>0){
                childrenIds=getCdaTypeChildId(listChild,childrenIds);
            }
        }
        return childrenIds;
    }


    @RequestMapping("GetCdaTypeListByKey")
    @ResponseBody
    public List<CDAType> GetCdaTypeListByKey(String code,String name) {
        List<CDAType> typeList = cdaTypeManager.getCDATypeListByKey(code,name);
        return typeList;
    }

    @RequestMapping("getCdaTypeById")
    @ResponseBody
    public CDAType getCdaTypeById(String[] Ids) {
        List<CDAType> listType = cdaTypeManager.getCdatypeInfoByIds(Ids);

        return listType.get(0);
    }



    @RequestMapping("SaveCdaType")
    @ResponseBody
    public Object SaveCdaType(String cdaTypeJsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CDAType cdaType =  objectMapper.readValue(cdaTypeJsonData, CDAType.class);
        //id 不为空则先获取数据，再进行修改
        String code = cdaTypeManager.getCdatypeInfoByIds(new String[]{cdaType.getId()}).get(0).getCode();
        if (!StringUtils.isEmpty(cdaType.getId())) {  //add
            if(cdaTypeManager.isCodeExist(cdaType.getCode())){
                return new Exception("代码已存在");
            }
            cdaType.setUpdateDate(new Date());
        } else {
            if(!code.equals(cdaType.getCode()) && cdaTypeManager.isCodeExist(cdaType.getCode())){
                return new Exception("代码已存在");
            }
            cdaType.setCreateDate(new Date());
        }
        return cdaTypeManager.save(cdaType);
    }



    @RequestMapping("getCDATypeListByParentId")
    @ResponseBody
    public List<MCDAType> getCDATypeListByParentId(String ids) throws Exception {
        List<CDAType> typeList = cdaTypeManager.getCDATypeListByParentId(ids);
        List<MCDAType> typeListModel = (List<MCDAType>)convertToModel(typeList,MCDAType.class);
        return typeListModel;
    }


    /**
     * 删除CDA类别，若该类别存在子类别，将一并删除子类别
     * 先根据当前的类别ID获取全部子类别ID，再进行删除
     * @param  ids  cdaType Id
     *  @return result 操作结果
     */
    @RequestMapping("delteCdaTypeInfo")
    @ResponseBody
    public Object delteCdaTypeInfo(String[] ids) {
        List<CDAType> listParentType = cdaTypeManager.getCdatypeInfoByIds(ids);
        String childrenIds = getCdaTypeChildId(listParentType, "");
        if(childrenIds.length()>0) {
            childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
        }
        return cdaTypeManager.deleteCdaType(childrenIds);
    }

}
