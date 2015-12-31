package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constrant.ApiVersionPrefix;
import com.yihu.ehr.constrant.Controllers;
import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.lang.ServiceFactory;
import com.yihu.ehr.std.model.*;
import com.yihu.ehr.std.service.*;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by AndyCai on 2015/8/7.
 */
@RestController(Controllers.StandardManagerController)
@RequestMapping(ApiVersionPrefix.CommonVersion + "/standard")
@Api(protocols = "https", value = "Standard", description = "标准规范管理接口", tags = {"标准规范", "CDA文档", "数据集", "数据元"})
public class StdManagerRestController extends BaseRestController {

    @Autowired
    private CDAVersionManager cdaVersionManager;

    @Autowired
    private DataSetManager dataSetManager;

    @Autowired
    private DictManager dictManager;

    @Autowired
    private DictEntryManager dictEntryManager;

    @Autowired
    private MetaDataManager metaDataManager;

    @Autowired
    private StandardSourceManager xStandardSourceManager;

    @Autowired
    private CDADocumentManager xcdaDocumentManager;

    @Autowired
    private CDADatasetRelationshipManager cdaDatasetRelationshipManager;

    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDA版本列表", response = CDAVersion.class, produces = "application/json", notes = "平台中已创建并发布的版本，不含正在编辑的阶段性版本")
    public Object getCDAVersionList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        try {
            String json = "";
            CDAVersion[] cdaVersions = cdaVersionManager.getVersionList();

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            ObjectNode versionList = objectMapper.createObjectNode();

            if (cdaVersions != null) {
                for (CDAVersion cdaVersion : cdaVersions) {
                    ObjectNode version = versionList.putObject(cdaVersion.getVersion());
                    version.put("base_version", cdaVersion.getBaseVersion());
                    version.put("author", cdaVersion.getAuthor());
                    version.put("commit_time", cdaVersion.getCommitTime().toString());
                }
            }

            return succeed(versionList);
        } catch (Exception ex) {
            return failed(ErrorCode.GetCDAVersionListFailed);
        }
    }

    @RequestMapping(value = "/version/{version}", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDA版本信息", response = RestEcho.class, produces = "application/json", notes = "指定版本的信息")
    public Object getCDAVersionInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号，若为latest则返回最新版本")
            @PathVariable(value = "version") String version) {
        String json = "";
        try {
            CDAVersion cdaVersion = null;
            if (version.equals("latest")) {
                cdaVersion = cdaVersionManager.getLatestVersion();
            } else {
                cdaVersion = cdaVersionManager.getVersion(version);
            }

            if (cdaVersion != null) {
                RestEcho restEcho = new RestEcho().success();
                restEcho.putResult("version", cdaVersion.getVersion());
                restEcho.putResult("timestamp", cdaVersion.getCommitTime().toString());

                return restEcho;
            } else {
                return failed(ErrorCode.GetCDAVersionFailed);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.GetCDAVersionFailed);
        }
    }

    @RequestMapping(value = "/version", method = RequestMethod.POST)
    @ApiOperation(value = "创建一个新版本", response = RestEcho.class, produces = "application/json", notes = "创建一个新的版本，阶段性版本，需要提交，审核后才能发布")
    public Object saveCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "author", value = "作者")
            @RequestParam(value = "author", required = true) String author) {
        try {
            CDAVersion latestVersion = cdaVersionManager.getLatestVersion();
            CDAVersion newVersion = cdaVersionManager.createStageVersion(latestVersion, author);

            if (newVersion == null) {
                return failed(ErrorCode.SaveCDAVersionFailed);
            } else {
                return succeedWithMessage("新版本创建成功");
            }

        } catch (Exception ex) {
            return failed(ErrorCode.SaveCDAVersionFailed);
        }
    }

    @RequestMapping(value = "/version/{version}/cda_documents", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDA文档列表", response = RestEcho.class, produces = "application/json", notes = "指定版本下的CDA文档列表")
    public Object getCDAListByVersionAndKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version) {
        try {
            return null;
        } catch (Exception ex) {
            return failed(ErrorCode.GetCDAInfoFailed);
        }
    }

    @RequestMapping(value = "/version/{version}/cda_document/{document_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDA文档属性", response = RestEcho.class, produces = "application/json", notes = "CDA文档的属性信息")
    public Object getCDADocumentInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "document_id", value = "文档ID")
            @PathVariable(value = "document_id") String documentId) {
        String json = "";
        try {
            List<String> listIds = Arrays.asList(documentId.split(","));
            CDADocument[] xcdaDocuments = xcdaDocumentManager.getCDAInfoByVersionAndId(version, listIds);
            if (xcdaDocuments == null) {
                return failed(ErrorCode.GetCDAInfoFailed);
            }

            List<CDAPageModel> resultInfos = getCDAForInterface(xcdaDocuments);

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(resultInfos);
        } catch (Exception ex) {
            return failed(ErrorCode.GetCDAInfoFailed);
        }

        RestEcho restEcho = new RestEcho().success();
        restEcho.putResultToList(json);

        return restEcho;
    }

    /**
     * 根据CDA文档 ID 获取其下属的数据集列表
     */
    @RequestMapping(value = "/version/{version}/cda_document/{document_id}/data_sets", method = RequestMethod.GET)
    @ApiOperation(value = "获取CDA包含的数据集列表", response = RestEcho.class, produces = "application/json", notes = "CDA文档的属性信息")
    public Object getDataSetByCDAId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "document_id", value = "文档ID")
            @PathVariable(value = "document_id") String documentId) {

        String json = "";
        try {
            CDADatasetRelationship[] relationships = cdaDatasetRelationshipManager.getRelationshipByCDAId(documentId, version);
            if (relationships == null) {
                return failed(ErrorCode.GetDataSetFailed);
            }
            List<String> listDatasetIds = new ArrayList<>();
            for (CDADatasetRelationship xCDADatasetRelationship : relationships) {
                listDatasetIds.add(xCDADatasetRelationship.getDataset_id());
            }

            if (listDatasetIds.size() <= 0) {
                return succeedWithMessage("");
            }

            DataSet[] xDataSets = dataSetManager.getDataSetByIds(listDatasetIds, version);

            if (xDataSets != null) {
                DataSetForInterface[] infos = new DataSetForInterface[xDataSets.length];
                int i = 0;
                for (DataSet xDataSet : xDataSets) {
                    DataSetForInterface info = new DataSetForInterface();

                    info.setId(String.valueOf(xDataSet.getId()));
                    info.setCode(xDataSet.getCode());
                    info.setName(xDataSet.getName());
                    info.setReference(String.valueOf(xDataSet.getReference()));
                    info.setStdVersion(xDataSet.getStdVersion());
                    info.setLang(String.valueOf(xDataSet.getLang()));
                    info.setCatalog(String.valueOf(xDataSet.getCatalog()));
                    info.setHashCode(String.valueOf(xDataSet.getHashCode()));
                    info.setDocumentId(String.valueOf(xDataSet.getDocumentId()));
                    info.setSummary(xDataSet.getSummary());
                    info.setInnerVersion(xDataSet.getInnerVersionId());

                    infos[i] = info;
                    i++;
                }

                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                json = objectMapper.writeValueAsString(infos);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.GetDataSetFailed);
        }

        RestEcho restEcho = new RestEcho().success();
        restEcho.putResultToList(json);

        return restEcho;
    }

    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集属性", response = RestEcho.class, produces = "application/json", notes = "数据集的属性信息")
    public Object getDataSetInfoByIds(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "版本号")
            @PathVariable(value = "data_set_id") String dataSetId) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            DataSet xDataSet = dataSetManager.getDataSet(Long.parseLong(dataSetId), cdaVersion);

            DataSetForInterface info = new DataSetForInterface();

            info.setId(String.valueOf(xDataSet.getId()));
            info.setCode(xDataSet.getCode());
            info.setName(xDataSet.getName());
            info.setReference(String.valueOf(xDataSet.getReference()));
            info.setStdVersion(xDataSet.getStdVersion());
            info.setLang(String.valueOf(xDataSet.getLang()));
            info.setCatalog(String.valueOf(xDataSet.getCatalog()));
            info.setHashCode(String.valueOf(xDataSet.getHashCode()));
            info.setDocumentId(String.valueOf(xDataSet.getDocumentId()));
            info.setSummary(xDataSet.getSummary());
            info.setInnerVersion(xDataSet.getInnerVersionId());

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(info);
        } catch (Exception ex) {
            return failed(ErrorCode.GetDataSetFailed);
        }
        return succeed(json);
    }

    // TODO 此方法肯定调用失败
    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}", method = RequestMethod.POST)
    @ApiOperation(value = "创建数据集", response = RestEcho.class, produces = "application/json", notes = "创建一个新的数据集")
    public Object saveDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "版本号")
            @PathVariable(value = "data_set_id") String dataSetId,
            DataSetForInterface info) {
        String strResult = "";
        try {
            DataSet dataSet = new DataSet();
            long strId = info.getId() == "" ? 0 : Long.parseLong(info.getId());
            String code = info.getCode();
            String name = info.getName();
            String reference = info.getReference();
            String stdVersion = info.getStdVersion();

            CDAVersion cdaVersion = cdaVersionManager.getVersion(stdVersion);

            int iLang = info.getLang() == "" ? 0 : Integer.parseInt(info.getLang());
            int iCatalog = info.getCatalog() == "" ? 0 : Integer.parseInt(info.getCatalog());
            int iDocumentId = info.getDocumentId() == "" ? 0 : Integer.parseInt(info.getDocumentId());
            String strSummary = info.getSummary();

            dataSet.setId(strId);
            dataSet.setCode(code);
            dataSet.setName(name);
            dataSet.setReference(reference);
            dataSet.setStdVersion(stdVersion);
            dataSet.setLang(iLang);
            dataSet.setCatalog(iCatalog);
            dataSet.setDocumentId(iDocumentId);
            dataSet.setSummary(strSummary);
            dataSet.setInnerVersion(cdaVersion);

            if (dataSetManager.saveDataSet(dataSet))
                return succeedWithMessage("ok");
            else
                return failed(ErrorCode.SavedatasetFailed);
        } catch (Exception ex) {
            return failed(ErrorCode.SavedatasetFailed);
        }
    }

    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据集", response = RestEcho.class, produces = "application/json", notes = "更新的数据集")
    public Object updateDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "版本号")
            @PathVariable(value = "data_set_id") String dataSetId,
            DataSetForInterface info) {
        return null;
    }

    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集及其数据元", response = RestEcho.class, produces = "application/json", notes = "删除阶段性版本的数据集")
    public Object deleteDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "版本号")
            @PathVariable(value = "data_set_id") String dataSetId) {
        try {
            int row = metaDataManager.removeMetaDataBySetId(version, Long.parseLong(dataSetId));
            if (row < 0) {
                return failed(ErrorCode.DeleteDataSetFailed);
            }

            dataSetManager.deleteDataSet(Long.parseLong(dataSetId), version);
        } catch (Exception ex) {
            return failed(ErrorCode.DeleteDataSetFailed);
        }

        return succeedWithMessage("删除成功");
    }


    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}/meta_data", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元列表", response = RestEcho.class, produces = "application/json", notes = "指定数据集下的数据元列表")
    public Object getMetaDataList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集ID")
            @PathVariable(value = "data_set_id") String dataSetId) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //获取数据集
            DataSet xDataSet = dataSetManager.getDataSet(Long.parseLong(dataSetId), cdaVersion);

            //获取数据元
            List<MetaData> metaDatas = xDataSet.getMetaDataList();
            if (metaDatas != null) {
                MetaDataForInterface[] infos = new MetaDataForInterface[metaDatas.size()];
                for (int i = 0; i < metaDatas.size(); ++i) {
                    MetaDataForInterface info = new MetaDataForInterface();
                    MetaData metaData = metaDatas.get(i);

                    info.setDatasetId(String.valueOf(metaData.getDataSetId()));
                    info.setCode(metaData.getCode());
                    info.setInnerCode(metaData.getInnerCode());
                    info.setName(metaData.getName());
                    info.setType(metaData.getType());
                    info.setFormatType(metaData.getFormat());
                    info.setDefinition(metaData.getDefinition());
                    info.setNullable(String.valueOf(metaData.isNullable()));
                    info.setColumnType(metaData.getColumnType());
                    info.setColumnName(metaData.getColumnName());
                    info.setColumnLength(metaData.getColumnLength());
                    info.setPrimaryKey(String.valueOf(metaData.isPrimaryKey()));
                    info.setHashCode(String.valueOf(metaData.getHashCode()));
                    info.setId(String.valueOf(metaData.getId()));
                    infos[i] = info;
                    i++;
                }

                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                json = objectMapper.writeValueAsString(infos);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.GetMetaDataListFaield);
        }

        return new RestEcho().success().putResultToList(json);
    }

    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}/meta_data/{meta_data_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元属性", response = RestEcho.class, produces = "application/json", notes = "指定数据元的属性")
    public Object getMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集ID")
            @PathVariable(value = "data_set_id") String dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元ID")
            @PathVariable(value = "meta_data_id") String metaDataId) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //获取数据集
            DataSet xDataSet = dataSetManager.getDataSet(Long.parseLong(dataSetId), cdaVersion);

            //获取数据元
            MetaData metaData = xDataSet.getMetaData(Long.parseLong(metaDataId));
            MetaDataForInterface info = new MetaDataForInterface();

            info.setDatasetId(String.valueOf(metaData.getDataSetId()));
            info.setCode(metaData.getCode());
            info.setInnerCode(metaData.getInnerCode());
            info.setName(metaData.getName());
            info.setType(metaData.getType());
            info.setFormatType(metaData.getFormat());
            info.setDefinition(metaData.getDefinition());
            info.setNullable(String.valueOf(metaData.isNullable()));
            info.setColumnType(metaData.getColumnType());
            info.setColumnName(metaData.getColumnName());
            info.setColumnLength(metaData.getColumnLength());
            info.setPrimaryKey(String.valueOf(metaData.isPrimaryKey()));
            info.setHashCode(String.valueOf(metaData.getHashCode()));
            info.setId(String.valueOf(metaData.getId()));

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(info);

        } catch (Exception ex) {
            return failed(ErrorCode.GetMetaDataFailed);
        }
        return succeed(json);
    }

    // TODO 调用无法通过
    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}/meta_data", method = RequestMethod.POST)
    @ApiOperation(value = "保存数据元", response = RestEcho.class, produces = "application/json", notes = "阶段性版本的数据元")
    public Object saveMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集ID")
            @PathVariable(value = "data_set_id") String dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元ID")
            @PathVariable(value = "meta_data_id") String metaDataId,
            MetaDataForInterface info) {
        String strResult = "";
        try {

            long iId = info.getId() == "" ? 0 : Long.parseLong(info.getId());
            String strCode = info.getCode();
            String strName = info.getName();
            long iSetId = info.getDatasetId() == "" ? 0 : Long.parseLong(info.getDatasetId());

            String strInnerCode = info.getInnerCode();
            String strType = info.getType();
            String strFormat = info.getFormatType();
            Long dictId = info.getDictId() == "" ? 0 : Long.parseLong(info.getDictId());
            String strDefinition = info.getDefinition();

            boolean isNullable = info.getNullable() == "" ? false : Boolean.parseBoolean(info.getNullable());
            boolean isPrimaryKey = info.getPrimaryKey() == "" ? false : Boolean.parseBoolean(info.getPrimaryKey());

            String strColumnType = info.getColumnType();
            String strColumnName = info.getColumnName();
            String strColumnLength = info.getColumnLength();

            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strInnerCode);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            DataSet xDataSet = dataSetManager.getDataSet(iSetId, cdaVersion);

            MetaData metaData = metaDataManager.createMetaData(xDataSet);
            metaData.setId(iId);
            metaData.setCode(strCode);
            metaData.setName(strName);
            metaData.setInnerCode(strInnerCode);
            metaData.setType(strType);
            metaData.setFormat(strFormat);
            metaData.setDictId(dictId);
            metaData.setDefinition(strDefinition);
            metaData.setNullable(isNullable);
            metaData.setPrimaryKey(isPrimaryKey);
            metaData.setColumnType(strColumnType);
            metaData.setColumnName(strColumnName);
            metaData.setColumnLength(strColumnLength);

            int result = metaDataManager.saveMetaData(xDataSet, metaData);
            if (result <= 0) {
                failed(ErrorCode.SaveMetaDataFailed);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.SaveMetaDataFailed);
        }
        return succeedWithMessage("保存成功");
    }

    /**
     * 删除数据元
     */

    @RequestMapping(value = "/version/{version}/data_set/{data_set_id}/meta_data/{meta_data_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "保存数据元", response = RestEcho.class, produces = "application/json", notes = "阶段性版本的数据元")
    public Object deleteMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集ID")
            @PathVariable(value = "data_set_id") String dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元ID", required = true)
            @PathVariable(value = "meta_data_id") String metaDataId) {
        try {
            int result = metaDataManager.removeMetaData(version, Integer.parseInt(metaDataId));
            if (result >= 0)
                return succeedWithMessage("删除成功");
            else
                return failed(ErrorCode.DeleteMetaDataFailed);
        } catch (Exception ex) {
            return failed(ErrorCode.DeleteMetaDataFailed);
        }
    }


    @RequestMapping(value = "/version/{version}/dictionaries", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典列表", response = RestEcho.class, produces = "application/json", notes = "字典列表")
    public Object getDictListByCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //根据版本获取全部的字典
            Dict[] xDicts = dictManager.getDictList(0, 0, cdaVersion);
            if (xDicts != null) {
                DictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (Dict xDict : xDicts) {
                    DictForInterface info = new DictForInterface();

                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersionId());
                    infos[i] = info;
                    i++;
                }

                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                json = objectMapper.writeValueAsString(infos);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.GetDictListFaild);
        }

        return new RestEcho().success().putResultToList(json);
    }


    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典", response = RestEcho.class, produces = "application/json", notes = "字典属性")
    public Object getDictInfoByCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //根据版本获取全部的字典
            Dict xDict = dictManager.getDict(Long.parseLong(dictId), cdaVersion);
            DictForInterface info = new DictForInterface();
            info.setId(String.valueOf(xDict.getId()));
            info.setCode(xDict.getCode());
            info.setName(xDict.getName());
            info.setAuthor(xDict.getAuthor());
            info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
            info.setCreateDate(String.valueOf(xDict.getCreateDate()));
            info.setDescription(xDict.getDescription());
            info.setStdVersion(xDict.getStdVersion());

            info.setHashCode(String.valueOf(xDict.getHashCode()));
            info.setInnerVersionId(xDict.getInnerVersionId());

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(info);

        } catch (Exception ex) {
            return failed(ErrorCode.GetDictFaild);
        }
        return succeed(json);
    }

    // TODO 调用肯定无法通过
    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}", method = RequestMethod.POST)
    @ApiOperation(value = "创建字典", response = RestEcho.class, produces = "application/json", notes = "创建字典")
    public Object createDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            DictForInterface info) {
        try {
            Long id = info.getId() == null ? 0 : Long.parseLong(info.getId());
            String strCode = info.getCode();
            String strName = info.getName();
            String author = info.getAuthor();
            Long baseDict = info.getBaseDictId() == null ? 0 : Long.parseLong(info.getBaseDictId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date createTime = info.getCreateDate() == null ? new Date() : sdf.parse(info.getCreateDate());
            String strDesc = info.getDescription();
            String strStdVersion = info.getStdVersion();

            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strStdVersion);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            Dict xDict = new Dict();
            xDict.setId(id);
            xDict.setCode(strCode);
            xDict.setName(strName);
            xDict.setAuthor(author);
            xDict.setBaseDictId(baseDict);
            xDict.setCreateDate(createTime);
            xDict.setDescription(strDesc);
            xDict.setStdVersion(strStdVersion);
            xDict.setInnerVersion(cdaVersion);

            int result = dictManager.saveDict(xDict);
            if (result >= 1) {
                return succeedWithMessage("保存成功");
            } else
                return failed(ErrorCode.SaveDictFailed);
        } catch (Exception ex) {
            return failed(ErrorCode.SaveDictFailed);
        }
    }

    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "创建字典", response = RestEcho.class, produces = "application/json", notes = "创建字典")
    public Object updateDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            DictForInterface info) {
        return null;
    }

    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典", response = RestEcho.class, produces = "application/json", notes = "删除字典")
    public Object deleteDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId) {
        try {
            //先删除字典项
            int irow = dictEntryManager.DeleteEntryByDictId(version, dictId);
            if (irow < 0) {
                return failed(ErrorCode.DeleteDictFailed);
            }

            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            Dict xDict = dictManager.getDict(Long.parseLong(dictId), cdaVersion);

            if (xDict != null) {
                int result = dictManager.removeDict(xDict);
                if (result >= 1)
                    return succeedWithMessage("删除成功");
                else
                    return failed(ErrorCode.DeleteDictFailed);
            } else
                return failed(ErrorCode.DeleteDictFailed);

        } catch (Exception ex) {
            return failed(ErrorCode.DeleteDictFailed);
        }
    }


    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}/entries", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项列表", response = RestEcho.class, produces = "application/json", notes = "获取字典项列表")
    public Object getDictEntryListByCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId) {
        String json = "";
        try {
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //根据版本获取全部的字典
            Dict xDict = dictManager.getDict(Long.parseLong(dictId), cdaVersion);
            DictEntry[] xDictEntries = xDict.getDictEntries();
            if (xDictEntries != null) {
                DictEntryForInterface[] infos = new DictEntryForInterface[xDictEntries.length];
                int i = 0;
                for (DictEntry xDictEntry : xDictEntries) {
                    DictEntryForInterface info = new DictEntryForInterface();

                    info.setId(String.valueOf(xDictEntry.getId()));
                    info.setCode(xDictEntry.getCode());
                    info.setValue(xDictEntry.getValue());
                    info.setDictId(String.valueOf(xDictEntry.getDictId()));
                    info.setDesc(xDictEntry.getDesc());
                    infos[i] = info;
                    i++;
                }
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                json = objectMapper.writeValueAsString(infos);
            }

        } catch (Exception ex) {
            return failed(ErrorCode.GetDictEntryListFailed, ex.getMessage());
        }

        RestEcho restEcho = new RestEcho().success();
        restEcho.putResultToList(json);
        return restEcho;
    }


    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}/entry/{entry_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项属性", response = RestEcho.class, produces = "application/json", notes = "字典项属性")
    public Object getDictEntryListByCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            @ApiParam(name = "entry_id", value = "字典项ID", required = true)
            @PathVariable(value = "entry_id") String dictEntryId) {
        String json = "";
        try {
            //根据版本编号获取版本属性
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);

            //根据版本获取全部的字典
            Dict xDict = dictManager.getDict(Long.parseLong(dictId), cdaVersion);

            List<Integer> listIds = new ArrayList<>();
            listIds.add(Integer.parseInt(dictEntryId));
            DictEntry[] xDictEntries = xDict.getDictEntries(listIds);
            if (xDictEntries != null) {
                DictEntryForInterface[] infos = new DictEntryForInterface[xDictEntries.length];
                int i = 0;
                for (DictEntry xDictEntry : xDictEntries) {
                    DictEntryForInterface info = new DictEntryForInterface();

                    info.setId(String.valueOf(xDictEntry.getId()));
                    info.setCode(xDictEntry.getCode());
                    info.setValue(xDictEntry.getValue());
                    info.setDictId(String.valueOf(xDictEntry.getDictId()));
                    info.setDesc(xDictEntry.getDesc());
                    infos[i] = info;
                    i++;
                }
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                json = objectMapper.writeValueAsString(infos);
            }


        } catch (Exception ex) {
            return failed(ErrorCode.GetDictEntryFailed, ex.getMessage());
        }

        RestEcho restEcho = new RestEcho().success();
        restEcho.putResultToList(json);
        return restEcho;
    }

    // TODO 调用肯定无法通过
    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}/entry/{entry_id}", method = RequestMethod.POST)
    @ApiOperation(value = "创建字典项", response = RestEcho.class, produces = "application/json", notes = "创建字典项")
    public Object createDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            @ApiParam(name = "entry_id", value = "字典项ID", required = true)
            @PathVariable(value = "entry_id") String dictEntryId,
            DictEntryForInterface info) {
        try {
            Long id = info.getId() == "" ? 0 : Long.parseLong(info.getId());

            String stdVersion = info.getStdVersion();
            CDAVersion cdaVersion = cdaVersionManager.getVersion(stdVersion);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            Long dict = info.getDictId() == "" ? 0 : Long.parseLong(info.getDictId());
            Dict xDict = dictManager.getDict(dict, cdaVersion);

            String strCode = info.getCode();
            String strValue = info.getValue();
            String strDesc = info.getDesc();

            DictEntry xDictEntry = new DictEntry();
            xDictEntry.setId(id);
            xDictEntry.setDict(xDict);
            xDictEntry.setCode(strCode);
            xDictEntry.setValue(strValue);
            xDictEntry.setDesc(strDesc);

            int result = dictEntryManager.saveEntry(xDictEntry);
            if (result >= 1) {
                return succeedWithMessage("保存成功");
            } else {
                return failed(ErrorCode.saveDictEntryFailed);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.saveDictEntryFailed);
        }
    }

    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}/entry/{entry_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "创建字典项", response = RestEcho.class, produces = "application/json", notes = "创建字典项")
    public Object updateDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            @ApiParam(name = "entry_id", value = "字典项ID", required = true)
            @PathVariable(value = "entry_id") String dictEntryId,
            String info) {
        return null;
    }

    @RequestMapping(value = "/version/{version}/dictionary/{dictionary_id}/entry/{entry_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项", response = RestEcho.class, produces = "application/json", notes = "删除字典项信息")
    public Object deleteDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @PathVariable(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dictionary_id") String dictId,
            @ApiParam(name = "entry_id", value = "字典项ID", required = true)
            @PathVariable(value = "entry_id") String dictEntryId) {
        try {
            CDAVersion cdaVersion = cdaVersionManager.getVersion(version);
            if (cdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);

            Dict xDict = dictManager.getDict(Long.parseLong(dictId), cdaVersion);

            DictEntry xDictEntry = dictEntryManager.getEntries(Long.parseLong(dictEntryId), xDict);

            int result = dictEntryManager.deleteEntry(xDictEntry);
            if (result >= 1) {
                return succeedWithMessage("删除成功");
            } else {
                return failed(ErrorCode.DeleteDictEntryFailed);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.DeleteDictEntryFailed);
        }
    }

    @RequestMapping(value = "/standard_sources", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源列表", response = RestEcho.class, produces = "application/json", notes = "标准来源列表")
    public Object getStandardSourceByKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", required = true)
            @RequestParam(value = "version", required = true) String version,
            @ApiParam(name = "document_id", value = "CDA文档ID", required = true)
            @RequestParam(value = "document_id", required = true) String documentId) {
        try {
            String json = "";
            StandardSource[] xStandardSources = xStandardSourceManager.getSourceByKey(documentId);
            if (xStandardSources == null) {
                return failed(ErrorCode.GetStandardSourceFailed);
            }
            List<StandardSourceForInterface> resultInfos = getStandardSourceForInterface(xStandardSources);

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(resultInfos);

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResultToList(json);

            return restEcho;
        } catch (Exception ex) {
            return failed(ErrorCode.GetStandardSourceFailed);
        }
    }

    @RequestMapping(value = "/standard_source/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源属性", response = RestEcho.class, produces = "application/json", notes = "标准来源的属性")
    public Object getStandardSourceByIds(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "标准ID", required = true)
            @PathVariable(value = "id") String sourceId) {
        try {
            String json = "";
            List<String> listIds = Arrays.asList(sourceId.split(","));

            StandardSource[] xStandardSources = xStandardSourceManager.getSourceById(listIds);

            if (xStandardSources == null) {
                return failed(ErrorCode.GetStandardSourceFailed);
            }
            List<StandardSourceForInterface> resultInfos = getStandardSourceForInterface(xStandardSources);

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            json = objectMapper.writeValueAsString(resultInfos);

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResultToList(json);

            return restEcho;
        } catch (Exception ex) {
            return failed(ErrorCode.GetStandardSourceFailed);
        }
    }

    public List<StandardSourceForInterface> getStandardSourceForInterface(StandardSource[] xStandardSources) {
        List<StandardSourceForInterface> results = new ArrayList<>();
        for (StandardSource xStandardSource : xStandardSources) {
            StandardSourceForInterface info = new StandardSourceForInterface();
            info.setId(xStandardSource.getId());
            info.setCode(xStandardSource.getCode());
            info.setName(xStandardSource.getName());
            info.setSourceType(xStandardSource.getSourceType());
            info.setDescription(xStandardSource.getDescription());
            results.add(info);
        }
        return results;
    }

    public List<CDAPageModel> getCDAForInterface(CDADocument[] xcdaDocuments) {
        List<CDAPageModel> infos = new ArrayList<>();
        for (CDADocument xcdaDocument : xcdaDocuments) {
            CDAPageModel info = new CDAPageModel();
            info.setId(xcdaDocument.getId());
            info.setCode(xcdaDocument.getCode());
            info.setName(xcdaDocument.getName());
            info.setDescription(xcdaDocument.getDescription());
            info.setPrintOut(xcdaDocument.getPrintOut());
            info.setSourceId(xcdaDocument.getSource_id());
            info.setSchema(xcdaDocument.getSchema());
            info.setVersionCode(xcdaDocument.getVersionCode());
            infos.add(info);
        }
        return infos;
    }
}
