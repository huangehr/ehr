package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsAdapterDictionaryModel;
import com.yihu.ehr.agModel.resource.RsAdapterMetadataModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.resource.*;
import com.yihu.ehr.resource.client.*;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "adapterDictionary", description = "适配字典项服务")
public class AdapterDictionaryController extends BaseController {

    @Autowired
    private AdapterDictionaryClient adapterDictionaryClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictEntryClient;

    @Autowired
    private RsDictionaryClient rsDictionaryClient;

    @Autowired
    private RsDictionaryEntryClient dictionaryEntryClient;


    @RequestMapping(value = "/adaptions/adapter/dictionaries", method = RequestMethod.POST)
    @ApiOperation("创建适配字典项")
    public Envelop createDictionaries(
            @ApiParam(name = "json_data", value = "字典项JSON", defaultValue = "")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary mRsAdapterDictionary = adapterDictionaryClient.createDictionaries(jsonData);
            envelop.setObj(mRsAdapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/adaptions/adapter/dictionaries", method = RequestMethod.PUT)
    @ApiOperation("更新适配字典项")
    public Envelop updateDictionary(
            @ApiParam(name = "json_data", value = "字典项JSON", defaultValue = "")
            @RequestParam(value = "jsonData") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary mRsAdapterDictionary = adapterDictionaryClient.updateDictionary(jsonData);
            envelop.setObj(mRsAdapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/adaptions/adapter/dictionaries", method = RequestMethod.DELETE)
    @ApiOperation("删除适配字典项")
    public Envelop deleteDictionary(
            @ApiParam(name = "id", value = "字典项ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            adapterDictionaryClient.deleteDictionary(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }



    @RequestMapping(value = "/adaptions/adapter/dictionaries/{id}",method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配字典项")
    public Envelop getDictionaryById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAdapterDictionary mRsAdapterDictionary = adapterDictionaryClient.getDictionaryById(id);
            envelop.setObj(mRsAdapterDictionary);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/adaptions/adapter/dictionaries", method = RequestMethod.GET)
    @ApiOperation("查询适配字典项")
    public Envelop getDictionaries(
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
        try
        {
            ResponseEntity<List<MRsAdapterDictionary>> responseEntity = adapterDictionaryClient.getDictionaries(fields, filters, sorts, page, size);
            List<MRsAdapterDictionary> mRsAdapterDictionaries = responseEntity.getBody();
            List<RsAdapterDictionaryModel> rsAdapterDictionaryModels = new ArrayList<>();
            RsAdapterDictionaryModel rsAdapterDictionaryModel = null;
            for (MRsAdapterDictionary mRsAdapterDictionary: mRsAdapterDictionaries){
                rsAdapterDictionaryModel= new RsAdapterDictionaryModel();
                BeanUtils.copyProperties(mRsAdapterDictionary,rsAdapterDictionaryModel);
                     if(StringUtils.isNotBlank(rsAdapterDictionaryModel.getDictCode())){
                    ResponseEntity<List<MRsDictionary>> responseEntitys = rsDictionaryClient.searchRsDictionaries("", "code=" + rsAdapterDictionaryModel.getDictCode(), "", 1, 1);
                    List<MRsDictionary> mRsDictionaries = responseEntitys.getBody();
                    if(mRsDictionaries!=null&&mRsDictionaries.size()>0){
                        rsAdapterDictionaryModel.setDictName(mRsDictionaries.get(0).getName());
                    }
                }
                if(StringUtils.isNotBlank(rsAdapterDictionaryModel.getDictEntryCode())){
                    ResponseEntity<List<MRsDictionaryEntry>> responseEntitys = dictionaryEntryClient.searchRsDictionaryEntries("", "dictCode=" + rsAdapterDictionaryModel.getDictCode() + ";code=" + rsAdapterDictionaryModel.getDictEntryCode(), "", 1, 1);
                    List<MRsDictionaryEntry> mRsDictionaryEntries = responseEntitys.getBody();
                    if(mRsDictionaryEntries!=null&&mRsDictionaryEntries.size()>0){
                        rsAdapterDictionaryModel.setDictEntryName(mRsDictionaryEntries.get(0).getName());
                    }
                }
                rsAdapterDictionaryModels.add(rsAdapterDictionaryModel);
            }
            Envelop envelop = getResult(rsAdapterDictionaryModels, getTotalCount(responseEntity), page, size);
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
}
