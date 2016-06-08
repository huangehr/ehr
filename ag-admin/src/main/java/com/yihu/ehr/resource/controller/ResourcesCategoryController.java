package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsCategoryModel;
import com.yihu.ehr.agModel.resource.RsCategoryTypeTreeModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceCategory", description = "资源分类服务接口")
public class ResourcesCategoryController extends BaseController {

    @Autowired
    private ResourcesCategoryClient resourcesCategoryClient;

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST)
    @ApiOperation("资源类别创建")
    public Envelop createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsCategory detailModel = objectMapper.readValue(resourceCategory,MRsCategory.class);
            if(StringUtils.isNotBlank(detailModel.getPid())){
                List<MRsCategory> mRsCategories =  resourcesCategoryClient.getAllCategories("pid=" + detailModel.getPid() + ";name=" + detailModel.getName());
                if(mRsCategories.size()>0){
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("同级分类下已经存在名称为【"+detailModel.getName()+"】的分类，请修改！");
                    return envelop;
                }
            }else {
                List<MRsCategory> mRsCategories = resourcesCategoryClient.getRsCategoryByPid("");
                Boolean isExit = false;
                for(MRsCategory mRsCategory: mRsCategories){
                    if(mRsCategory.getName().equals(detailModel.getName())){
                        isExit  =true;
                        break;
                    }
                }
                if(isExit){
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("顶级分类下已经存在名称为【" + detailModel.getName() + "】的分类，请修改！");
                    return envelop;
                }
            }
            MRsCategory rsCategory = resourcesCategoryClient.createRsCategory(resourceCategory);
            envelop.setObj(rsCategory);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT)
    @ApiOperation("资源类别更新")
    public Envelop updateRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsCategory rsCategory = resourcesCategoryClient.updateRsCategory(resourceCategory);
            envelop.setObj(rsCategory);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public Envelop deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesCategoryClient.deleteResourceCategory(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
    @RequestMapping(value = ServiceApi.Resources.Category,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源类别")
    public Envelop getRsCategoryById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsCategory rsCategory = resourcesCategoryClient.getRsCategoryById(id);
            RsCategoryModel rsCategoryModel = new RsCategoryModel();
            BeanUtils.copyProperties(rsCategory,rsCategoryModel);
            if(StringUtils.isNotBlank(rsCategoryModel.getPid())){
                MRsCategory rsCategoryParent = resourcesCategoryClient.getRsCategoryById(id);
                if(rsCategoryParent!=null){
                    rsCategoryModel.setPname(rsCategoryParent.getName());
                }
            }
            envelop.setObj(rsCategoryModel);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/resources/categories/pid",method = RequestMethod.GET)
    @ApiOperation("根据pid获取资源类别列表")
    public Envelop getRsCategoryByPid(
            @ApiParam(name="pid",value="pid",defaultValue = "")
            @RequestParam(value="pid",required = false) String pid) throws Exception {
        Envelop envelop = new Envelop();
        try{
            List<MRsCategory> rsCategoryList = resourcesCategoryClient.getRsCategoryByPid(pid);
            envelop.setDetailModelList(rsCategoryList);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Envelop getRsCategories(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try {
            ResponseEntity<List<MRsCategory>> responseEntity = resourcesCategoryClient.getRsCategories(fields,filters,sorts,page,size);
            List<MRsCategory> rsCategories = responseEntity.getBody();
            Envelop envelop = getResult(rsCategories, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @RequestMapping(value = "/resources/types/parent", method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public Envelop getCdaTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        List<MRsCategory> mcdaTypeList = resourcesCategoryClient.getCateTypeExcludeSelfAndChildren(id);
        if(mcdaTypeList.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配的cda类别列表！");
        }
        envelop.setDetailModelList(convertToRsCategoryModels(mcdaTypeList));
        return  envelop;
    }

    @RequestMapping(value = "/resources/categories/parent_ids", method = RequestMethod.GET)
    @ApiOperation("获取该资源类别父级、及父级的父级id组成的字符串,返回前一页面树的定位")
    public Envelop getCategoryParentIdsById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @RequestParam(value="id") String id) throws Exception{
        Envelop envelop = new Envelop();
        List<String> list = new ArrayList<>();
        list = getPid(id,list);
        String ids = "";
        for(String s :list){
            ids = s+","+ids;
        }
        if(!StringUtils.isEmpty(ids)){
            ids = ids.substring(0,ids.length()-1);
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(ids);
        return envelop;
    }

    public List<String> getPid(String id,List<String> list){
        if(!StringUtils.isEmpty(id)){
            list.add(id);
            MRsCategory m = resourcesCategoryClient.getRsCategoryById(id);
            list =this.getPid(m == null?"":m.getPid(),list);
        }
        return list;
    }

    @RequestMapping(value = ServiceApi.Resources.NoPageCategories,method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Envelop getAllCategories(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsCategory> resources = resourcesCategoryClient.getAllCategories(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
    /**
     * 将微服务返回结果转化为前端RsCategoryModel对象集合
     *
     * @param mRsCategories
     * @return
     */
    private List<RsCategoryModel> convertToRsCategoryModels(List<MRsCategory> mRsCategories) {
        List<RsCategoryModel> rsCategoryModelList = (List<RsCategoryModel>) convertToModels(mRsCategories, new ArrayList<RsCategoryModel>(mRsCategories.size()), RsCategoryModel.class, null);
        return rsCategoryModelList;
    }

    /**
     * 用于资源类别前端页面树形显示
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resources/categories/tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有资源类别转成的RsCategoryTypeTreeModel列表，初始页面显示")
    public Envelop getRsCategoryTreeModels(
            @ApiParam(name = "name", value = "资源类别的名称")
            @RequestParam(value = "name",required = false) String name) throws Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        //获取到的顶级cda类别集合
        //String pid = null;
        List<MRsCategory> mRsCategories = resourcesCategoryClient.getRsCategoryByPid("");
        //顶级类别中符合条件的类别集合
        List<MRsCategory> mRsCategoriesSome = new ArrayList<>();
        //顶级类别中不符合条件的类别集合
        List<MRsCategory> mRsCategoriesOthers = new ArrayList<>();

        if (mRsCategories.size() == 0){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("没有匹配条件的资源类别！");
            return envelop;
        }
        List<RsCategoryTypeTreeModel> treeList = new ArrayList<>();
        if(StringUtils.isEmpty(name)){
            treeList = getRsCategoryTreeModelChild(mRsCategories);
            envelop.setDetailModelList(treeList);
            return envelop;
        }
        for(MRsCategory mRsCategory : mRsCategories){
            if(mRsCategory.getName().contains(name)){
                mRsCategoriesSome.add(mRsCategory);
                continue;
            }
            mRsCategoriesOthers.add(mRsCategory);
        }
        if (mRsCategoriesSome.size()!=0){
            treeList.addAll(getRsCategoryTreeModelChild(mRsCategoriesSome));
        }
        treeList .addAll(getRsCategoryTreeModelByName(mRsCategoriesOthers, name));
        envelop.setDetailModelList(treeList);
        return envelop;
    }

    /**
     *
     * 根据父级信息获取全部的子级信息（树形model）
     * @param info 父级信息
     * @return 全部子级信息
     */
    public List<RsCategoryTypeTreeModel> getRsCategoryTreeModelChild(List<MRsCategory> info) {
        List<RsCategoryTypeTreeModel> treeInfo = new ArrayList<>();
        for (int i = 0; i < info.size(); i++) {
            MRsCategory typeInfo = info.get(i);
            RsCategoryTypeTreeModel tree = new RsCategoryTypeTreeModel();
            tree = convertToModel(typeInfo,RsCategoryTypeTreeModel.class);
            List<MRsCategory> listChild = resourcesCategoryClient.getRsCategoryByPid(typeInfo.getId());
            List<RsCategoryTypeTreeModel> listChildTree = getRsCategoryTreeModelChild(listChild);
            tree.setChildren(listChildTree);
            treeInfo.add(tree);
        }
        return treeInfo;
    }

    /**
     * 递归不满足的父级类别集合的子集中满足条件TreeModel集合的方法
     * @param mRsCategories 不符合的父级类别的集合
     * @param name
     * @return
     */
    public  List<RsCategoryTypeTreeModel> getRsCategoryTreeModelByName(List<MRsCategory> mRsCategories,String name){
        //结构：treeList 包含treeModel，treeModel包含listOfParent
        List<RsCategoryTypeTreeModel> treeList = new ArrayList<>();
        for(MRsCategory mRsCategory:mRsCategories){
            List<RsCategoryTypeTreeModel> childList = new ArrayList<>();
            RsCategoryTypeTreeModel treeModel = convertToModel(mRsCategory,RsCategoryTypeTreeModel.class);
            String pid = mRsCategory.getId();
            //获取所有下一级cda类别
            List<MRsCategory> listAll = resourcesCategoryClient.getRsCategoryByPid(pid);
            if(listAll.size() == 0){
                continue;
            }
            //获取所有下一级符合要求的资源类别
            String filters ="pid="+pid+";name?"+name;

            //modify by cws
            List<MRsCategory> listSome = resourcesCategoryClient.getAllCategories(filters);
            //List<MRsCategory> listSome = (List<MRsCategory>)responseEntity.getBody();
            if(listSome.size()!=0){
                childList.addAll(getRsCategoryTreeModelChild(listSome));
            }
            //取剩下不符合要求的进行递归
            listAll.removeAll(listSome);
            if(listAll.size() != 0){
                childList.addAll(getRsCategoryTreeModelByName(listAll, name));
            }
            if(childList.size() != 0){
                treeModel.setChildren(childList);
                treeList.add(treeModel);
            }
        }
        return treeList;
    }


}
