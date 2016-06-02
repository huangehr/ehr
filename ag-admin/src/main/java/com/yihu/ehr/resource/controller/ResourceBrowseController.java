package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsCategoryTypeTreeModel;
import com.yihu.ehr.agModel.resource.RsResourceMetadataModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.model.resource.MRsResourceMetadata;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.client.MetadataClient;
import com.yihu.ehr.resource.client.ResourceMetadataClient;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.resource.client.ResourcesClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wq on 2016/5/30.
 */

@RestController
@Api(value = "ResourceBrowse", description = "资源浏览")
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
public class ResourceBrowseController extends BaseController {

    @Autowired
    private ResourcesClient resourcesClient;

    @Autowired
    private ResourceMetadataClient resourceMetadataClient;

    @Autowired
    private ResourcesCategoryClient resourcesCategoryClient;

    @Autowired
    private MetadataClient metadataClient;

    @ApiOperation("获取资源数据元信息，column信息")
    @RequestMapping(value = "/resources/ResourceBrowses", method = RequestMethod.GET)
    public Envelop queryResources(
            @ApiParam(name = "category_id", value = "返回字段", defaultValue = "")
            @RequestParam(value = "category_id") String categoryId) throws Exception {

        Envelop envelop = new Envelop();
        List<RsResourceMetadataModel> rsMetadataModels = new ArrayList<>();

        try {
            //查询资源注册信息
            ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", "categoryId=" + categoryId, "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
            List<MRsResources> rsResources = categoryResponseEntity.getBody();

            //查询资源数据元信息
            ResponseEntity<List<MRsResourceMetadata>> resourceMetadataResponseEntity = resourceMetadataClient.queryDimensions("", "resourcesId=" + rsResources.get(0).getId(), "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
            List<MRsResourceMetadata> rsMetadatas = resourceMetadataResponseEntity.getBody();

            //查询资源数据元详情
            for (MRsResourceMetadata mrm : rsMetadatas) {
                RsResourceMetadataModel rsMetadataModel = convertToModel(mrm, RsResourceMetadataModel.class);

                ResponseEntity<List<MRsMetadata>> RsresponseEntity = metadataClient.getMetadata("", "id=" + rsMetadataModel.getMetadataId(), "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
                List<MRsMetadata> mRsMetadataList = RsresponseEntity.getBody();

                if (mRsMetadataList.size() > 0) {
                    rsMetadataModel.setStdCode(mRsMetadataList.get(0).getStdCode());
                    rsMetadataModel.setName(mRsMetadataList.get(0).getName());
                    rsMetadataModel.setColumnType(mRsMetadataList.get(0).getColumnType());
                    rsMetadataModel.setDictId(mRsMetadataList.get(0).getDictCode());
                    rsMetadataModels.add(rsMetadataModel);
                }
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(rsMetadataModels);
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("查询资源分类")
    @RequestMapping(value = "/resources/ResourceBrowses/categories", method = RequestMethod.GET)
    public Envelop getCategories(
            @ApiParam(name = "id", value = "返回字段", defaultValue = "")
            @RequestParam(value = "id" ,required = false) String id) throws Exception {

        Envelop envelop = new Envelop();
        List<RsCategoryTypeTreeModel> rsCategoryTypeTreeModelList = new ArrayList<>();

        try {

            String filters = "";
            if (!StringUtils.isEmpty(id))
                filters = "pid=" + id;

            //查询资源分类
            ResponseEntity<List<MRsCategory>> responseEntity = resourcesCategoryClient.getRsCategories("", filters, "", 1, 999);// TODO: 2016/5/30 :测试数据，无需分页（尚无接口）
            List<MRsCategory> rsCategories = responseEntity.getBody();

            for (MRsCategory mRsCategory : rsCategories) {
                //查询资源注册信息
                RsCategoryTypeTreeModel rsCategoryTypeTreeModel = convertToModel(mRsCategory, RsCategoryTypeTreeModel.class);

                ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", "categoryId=" + rsCategoryTypeTreeModel.getId(), "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
                List<MRsResources> rsResources = categoryResponseEntity.getBody();

                if (rsResources.size()>0){
                    List<RsResourcesModel> resourcesModelList = (List<RsResourcesModel>) convertToModels(rsResources, new ArrayList<RsResourcesModel>(rsResources.size()), RsResourcesModel.class, null);
                    rsCategoryTypeTreeModel.setResourceIds(resourcesModelList);
                }
                rsCategoryTypeTreeModelList.add(rsCategoryTypeTreeModel);
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(rsCategoryTypeTreeModelList);

        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询资源分类失败");
        }
        return envelop;
    }
}
