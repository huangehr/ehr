package com.yihu.ehr.constants;

/**
 * 微服务REST API. 此处定义的URL可用于服务对外提供的地址及HTTP客户端请求地址.
 * URL定义遵循健康档案平台REST规范.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
public class ServiceApi {

    public static class GateWay {
        public static final String admin = "/admin";
    }

    public static class Adaptions {
        //public static final String Cache = "/adaptions/{id}/cache";
        //public static final String CacheGet = "/adaptions/{key}/cache";
        public static final String Scheme = "/adaptions/schemas/{id}";
        public static final String Schemes = "/adaptions/schemas";
        public static final String SchemaMetadataList = "/adaptions/schema/metadata";
        public static final String SchemaMetadata = "/adaptions/schema/metadata/{id}";
        public static final String SchemaMetadataBatch = "/adaptions/schema/metadata/batch";
        public static final String RsAdapterDictionaries = "/adaptions/adapter/dictionaries";
        //public static final String RsAdapterDictionariesCache = "/adaptions/adapter/dictionaries/{schemaId}/cache";
        public static final String RsAdapterDictionary = "/adaptions/adapter/dictionaries/{id}";
    }

    /**
     * 资源管理中心接口
     */
    public static class Resources {

        //资源查询接口(命名先按原有路径，不规范)
        public static final String ResourceViewMetadata = "/resources/query/getResourceMetadata"; //资源浏览获取结构
        public static final String ResourceViewData = "/resources/query/getResourceData"; //资源浏览获取数据
        public static final String ResourcesQuery = "/resources/query"; //资源查询接口
        public static final String ResourcesSubQuery = "/resources/sub_query"; //详细资源查询接口
        public static final String ResourcesQueryTransform = "/resources/query/transform"; //资源查询接口+转译
        public static final String ResourcesRawFiles = "/resources/query/raw_files"; //非结构资源查询接口
        public static final String ResourcesRawFilesList = "/resources/query/raw_files_list"; //非结构资源List查询接口
        public static final String ResourcesMasterData = "/resources/query/master_data"; //主表资源查询接口
        public static final String ResourcesSubData = "/resources/query/sub_data"; //细表资源查询接口
        public static final String getCDAData = "/resources/query/getCDAData";//获取cda data
        public static final String ResourcesMasterStat = "/resources/query/master_stat"; //主表资源统计接口
        public static final String ResourcesSubStat = "/resources/query/sub_stat"; //细表资源查询接口
        public static final String ResourcesMysql = "/resources/query/mysql"; //mysql资源查询接口

        //资源浏览
        public static final String ResourceBrowseCategories = "/resources/ResourceBrowses/categories";
        public static final String ResourceBrowseResourceData = "/resources/ResourceBrowses/getResourceData";
        public static final String ResourceBrowseQuotaResourceData = "/resources/ResourceBrowses/getQuotaResourceData";
        public static final String ResourceBrowseQuotaResourceParam = "/resources/ResourceBrowses/getQuotaResourceParam";
        public static final String ResourceBrowseResourceMetadata = "/resources/ResourceBrowses/getResourceMetadata";
        public static final String ResourceBrowseTree = "/resourceBrowseTree";
        public static final String ResourceBrowseGetRsByCategoryId = "/getResourceByCategoryId";

        //资源视图
        public static final String Resources = "/resources";
        public static final String Resource = "/resources/{id}";
        public static final String ResourceByCode = "/resources/byCode";
        public static final String ResourceTree = "/resources/tree";
        public static final String ResourcePage = "/resources/page";
        public static final String NoPageResources = "/NoPageResources";
        public static final String IsExistName = "/resources/isExistName";
        public static final String IsExistCode = "/resources/isExistCode/{code}";
        public static final String GetQuotaList = "/resources/getQuotaList";
        public static final String GetRsQuotaPreview = "/resources/getRsQuotaPreview";

        public static final String SearchInfo = "/resourceQuota/searchInfo";
        public static final String BatchAddResourceQuota = "/resourceQuota/batchAddResourceQuota";
        public static final String SearchByQuotaId = "/resourceQuota/searchByQuotaId";
        public static final String GetByResourceId = "/resourceQuota/getByResourceId";
        public static final String GetQuotaChartByQuotaId = "/resourceQuota/getQuotaChartByQuotaId";
        public static final String GetRQNameByResourceId = "/resourceQuota/getRQNameByResourceId";
        public static final String DelRQNameByResourceId = "/resourceQuota/delRQNameByResourceId";

        public static final String ResourceMetadataList = "/resources/rs_metadata";
        public static final String ResourceMetadataBatch = "/resources/rs_metadata/batch";
        public static final String ResourceMetadataBatchByResourceId = "/resources/rs_metadata/resource_ids/batch";
        public static final String ResourceMetadata = "/resources/rs_metadata/{id}";

        public static final String ResourceGrant = "/resources/grants/{id}";
        public static final String ResourceGrants = "/resources/grants";
        public static final String ResourceGrantsNoPage = "/resources/grants/no_paging";
        public static final String AppsGrantResources = "/resources/apps/{appId}/grant";
        public static final String ResourceGrantApps = "/resources/{resourceId}/grant";

        public static final String ResourceMetadatasValid = "/resources/metadatas/valid";
        public static final String ResourceMetadataGrants = "/resources/metadata/grants";
        public static final String ResourceMetadataGrant = "/resources/metadata/grants/{id}";
        public static final String ResourceMetadataGrantApp = "/resources/app_resource/{appResourceId}/grant/metadata/{metadataId}";
        public static final String ResourceMetadataListGrantApp = "/resources/metadata/grant/{appResourceId}";
        public static final String ResourceAppMetadataGrants = "/resources/app_resource/{app_res_id}/metadata";
        public static final String ResourceAppMetadataGrant = "/resources/app_resource/metadata/grant";
        public static final String ResourceAppMetadataGrantExistence = "/resources/app_resource/metadata/grant/existence";

        public static final String MetadataList = "/resources/metadata";
        public static final String MetadataBatch = "/resources/metadata/batch";
        public static final String Metadata = "/resources/metadata/{id}";
        public static final String MetadataExistence = "/resources/metadata/existence";
        public static final String MetadataStdCodeExistence = "/resources/metadata/std_code/existence";
        public static final String MetadataIdExistence = "/resources/metadata/id/existence";
        public static final String MetadataMaxId = "/resources/metadata/metadataMaxId";

        public static final String DictList = "/resources/dict";
        public static final String DictBatch = "/resources/dict/batch";
        public static final String Dict = "/resources/dict/{id}";
        public static final String DictCode = "/resources/dict/code";
        public static final String DictExistence = "/resources/dict/existence";
        public static final String DictEntryBatch = "/resources/dict/entry/batch";
        public static final String DictCodesExistence = "/resources/dict/codes/existence";

        public static final String Params = "/resources/params";
        public static final String ParamsNoPage = "/resources/params/no_paging";
        public static final String Param = "/resources/param";
        public static final String ParamById = "/resources/param/{id}";
        public static final String ParamKeyValueExistence = "/resources/param/key_value_existence";

        //固化视图筛选条件
        public static final String QueryByResourceId = "/resources/query/QueryByResourceId";

        public static final String DictEntries = "/resources/dict_entries";
        public static final String NoPageDictEntries = "/resources/noPage_dict_entries";
        public static final String DictEntry = "/resources/dict_entries/{id}";
        public static final String DictEntriesByDictCode = "/resources/dict/code/dict_entries";
        public static final String DictEntriesExistence = "/resources/dict_entries/existence";

        public static final String SystemDictList = "/resources/system_dict";
        public static final String SystemDict = "/resources/system_dict/{id}";

        public static final String SystemDictEntries = "/resources/system_dict_entries";
        public static final String SystemDictEntry = "/resources/system_dict_entries/{id}";

        //资源接口
        public static final String Interfaces = "/resources/interfaces";
        public static final String InterfaceById = "/resources/interfaces/{id}";
        public static final String Interface = "/resources/interface";
        public static final String InterfaceNameExistence = "/resources/existence/name";

        //资源分类
        public static final String CategoryUpdate = "/resources/category/update";
        public static final String Category = "/resources/category/{id}";
        public static final String CategoriesByPid = "/resources/categories/pid";
        public static final String CategoriesByCodeAndPid = "/resources/categories/codeAndPid";
        public static final String CategoryTree = "/resources/categories/tree";
        public static final String CategoryExitSelfAndParent = "/resources/categories/selfAndParent";
        public static final String CategoriesAll = "/resources/categories/all";
        public static final String CategoriesSearch = "/resources/categories/search";

        //综合查询服务
        public static final String IntMetadataList = "/resources/integrated/metadata_list";
        public static final String IntMetadataData = "/resources/integrated/metadata_data";
        public static final String IntQuotaList = "/resources/integrated/quota_list";
        public static final String IntQuotaData = "/resources/integrated/quota_data";
        public static final String IntQuotaParam = "/resources/integrated/quota_param";
        public static final String IntResourceUpdate = "/resources/integrated/resource_update";
        public static final String IntResourceQueryUpdate = "/resources/integrated/resource_query_update";

        //貌似没用了
        public static final String AdapterDicts = "/resources/adapter/dict";
        public static final String AdapterDictsBatch = "/resources/adapter/dict/batch";
        public static final String AdapterDict = "/resources/adapter/dict/{id}";

        //应用角色资源授权
        public static final String ResourceRolesGrants = "/resources/rolesGrants";
        public static final String ResourceRolesGrant = "/resources/rolesGrants/{id}";
        public static final String ResourceRolesMetadatasValid = "/resources/relosMetadatas/valid";
        public static final String ResourceRolesMetadataGrants = "/resources/relosMetadatas/grants";
        public static final String RolesGrantResources = "/resources/roles/{rolesId}/grant";
        public static final String GetRolesGrantResources = "/resources/getRolesGrantResources";
        //public static final String ResourceGrantApps = "/resources/{resourceId}/grant";
        public static final String ResourceRolesMetadataGrant = "/resources/rolesMetadata/grants/{id}";
        public static final String ResourceRolesGrantsNoPage = "/resources/rolesGrants/no_paging";
        public static final String ResourceRolesResMetadataGrants = "/resources/roles_resource/{roles_res_id}/metadata";

        /**
         * 机构-资源授权
         */
        public static final String ResourceOrgGrants = "/resources/OrgGrants";
        public static final String ResourceOrgGrant = "/resources/OrgGrants/{id}";
        public static final String ResourceOrgMetadatasValid = "/resources/OrgMetadatas/valid";
        public static final String ResourceOrgMetadataGrants = "/resources/OrgMetadatas/grants";
        public static final String OrgGrantResources = "/resources/Org/{orgCode}/grant";
        //public static final String ResourceGrantApps = "/resources/{resourceId}/grant";
        public static final String ResourceOrgMetadataGrant = "/resources/OrgMetadata/grants/{id}";
        public static final String ResourceOrgGrantsNoPage = "/resources/OrgGrants/no_paging";
        public static final String ResourceOrgResMetadataGrants = "/resources/Org_resource/{Org_res_id}/metadata";
        public static final String ResourceOrgRsMetadataGrant = "/resources/Org_resource/metadata/grant";

        /**
         * 资源报表分类
         */
        public static final String RsReportCategoryPrefix = "/resources/reportCategory/";
        public static final String RsReportCategory = "/resources/reportCategory/{id}";
        public static final String RsReportCategoryChildrenByPid = "/resources/reportCategory/getChildrenByPid";
        public static final String RsReportCategoryTree = "/resources/reportCategoryTree";
        public static final String RsReportCategoryComboTree = "/resources/reportCategoryComboTree";
        public static final String RsReportCategories = "/resources/reportCategories";
        public static final String RsReportCategorySave = "/resources/reportCategory/save";
        public static final String RsReportCategoryDelete = "/resources/reportCategory/delete";
        public static final String RsReportCategoryIsUniqueCode = "/resources/reportCategory/isUniqueCode";
        public static final String RsReportCategoryIsUniqueName = "/resources/reportCategory/isUniqueName";
        public static final String RsReportCategoryNoPageCategories = "/resources/reportCategory/getAllCategories";
        public static final String RsReportCategoryByApp = "/resources/reportCategory/getAllCategoryByApp";
        public static final String RsReportCategoryByIds = "/resources/reportCategory/getAllCategoryByIds";

        /**
         * 资源报表分类和应用
         */
        public static final String RsReportCategoryAppDelete = "/resources/reportCategory/deleteCategoryApp";
        public static final String RsReportCategoryAppSave = "/resources/reportCategory/saveCategoryApp";
        public static final String GetRsReportCategoryApps = "/resources/reportCategory/getRsReportCategoryApps";

        /**
         * 资源报表监测分类
         */
        public static final String RsReportMonitorType = "/resources/rsReportMonitorType/{id}";
        public static final String RsReportMonitorTypeSave = "/resources/rsReportMonitorType/save";
        public static final String RsReportMonitorTypeDelete = "/resources/rsReportMonitorType/delete";
        public static final String RsReportMonitorTypeIsUniqueName = "/resources/rsReportMonitorType/isUniqueName";
        public static final String RsReportMonitorTypes = "/resources/rsReportMonitorType/getRsReportMonitorTypePage";
        public static final String RsReportMonitorTypesNoPage = "/resources/rsReportMonitorType/getRsReportMonitorTypeNoPage";
        public static final String RsReportMonitorTypesById = "/resources/rsReportMonitorType/getRsReportMonitorTypeById";
        public static final String RsReportByMonitorTypeId = "/resources/rsReportMonitorType/getRsReportByMonitorTypeId";

        /**
         * 资源报表监测类型配置报表
         */
        public static final String RsMonitorTypeReport = "/resources/monitorTypeReport";
        public static final String RsMonitorTypeReportByUserId = "/resources/monitorTypeReport/{user_id}";
        public static final String RsMonitorTypeReports = "/resources/monitorTypeReport";
        public static final String RsMonitorTypeReportsNoPage = "/resources/monitorTypeReport/noPage";

        /**
         * 资源报表
         */
        public static final String RsReportPrefix = "/resources/report/";
        public static final String RsReport = "/resources/report/{id}";
        public static final String RsReportFindByCode = "/resources/report/findByCode";
        public static final String RsReports = "/resources/reports";
        public static final String RsReportViewsTreeData = "/resources/report/viewsTreeData";
        public static final String RsReportSelectedViews = "/resources/report/selectedViews";
        public static final String RsReportSave = "/resources/report/save";
        public static final String RsReportDelete = "/resources/report/delete";
        public static final String RsReportIsUniqueCode = "/resources/report/isUniqueCode";
        public static final String RsReportIsUniqueName = "/resources/report/isUniqueName";
        public static final String RsReportNoPage = "/resources/report/getReportNoPage";
        public static final String RsReportTemplateContent = "/resources/report/getTemplateContent";
        public static final String RsReportIsCategoryApplied = "/resources/report/isCategoryApplied";
        public static final String RsReportByCategoryId = "/resources/report/getByCategoryId";

        /**
         * 资源报表视图配置
         */
        public static final String RsReportViews = "/resources/reportViews";
        public static final String RsReportViewSave = "/resources/reportView/save";
        public static final String RsReportViewExist = "/resources/reportView/exist";
        public static final String RsReportViewExistByResourceId = "/resources/reportView/existByResourceId";
        public static final String RsReportViewExistReport = "/resources/reportView/existReport";


    }

    /**
     * 档案查询接口
     */
    public static class Profiles {
        public static final String ProfileLucene = "/profile/lucene"; //全文检索
        public static final String ProfileInfo = "/{demographic_id}/profile/info"; //基本信息
        public static final String ProfileHistory = "/{demographic_id}/profile/history"; //患病史
        public static final String HealthProblem = "/{demographic_id}/profile/health_problem"; //主要健康问题
        public static final String HealthProblemSub = "/{last_visit_record}/profile/health_problem_sub"; //主要健康问题诊断情况
        public static final String MedicalDisease = "/{demographic_id}/profile/medical_disease"; //就诊过疾病
        public static final String MedicalArea = "/{demographic_id}/profile/medical_area"; //就诊过区域
        public static final String MedicalYear = "/{demographic_id}/profile/medical_year"; //就诊过年份
        public static final String MedicalEvents = "/{demographic_id}/profile/medical_events"; //门诊/住院事件(时间轴)
        public static final String MedicationStat = "/{demographic_id}/profile/medication_stat"; //患者用药清单
        public static final String MedicationUsed = "/{demographic_id}/profile/medication_used"; //患者常用药（按次数）
        public static final String MedicalEvent = "/{event_no}/info"; //某次住院/门诊事件信息


        public static final String MedicationMaster = "/profile/medication/master"; //处方主表
        public static final String MedicationPrescription = "/profile/medication/prescription"; //处方笺
        public static final String MedicationDetail = "/profile/medication/detail/{prescription_no}"; //处方细表
        public static final String MedicationDetailChinese = "/profile/medication/detail/chinese"; //中药处方
        public static final String MedicationDetailWestern = "/profile/medication/detail/western"; //西药处方


        public static final String OutpatientDiagnosis = "/{demographic_id}/outpatient/diagnosis"; //门诊诊断
        public static final String OutpatientSymptom = "/{demographic_id}/outpatient/symptom"; //门诊症状
        public static final String OutpatientCostMaster = "/{demographic_id}/outpatient/cost/master"; //门诊费用汇总
        public static final String OutpatientCostDetail = "/{demographic_id}/outpatient/cost/detail"; //门诊费用明细


        public static final String HospitalizedDiagnosis = "/{demographic_id}/hospitalized/diagnosis"; //住院诊断
        public static final String HospitalizedSymptom = "/{demographic_id}/hospitalized/symptom"; //住院症状
        public static final String HospitalizedCostMaster = "/{demographic_id}/hospitalized/cost/master"; //住院费用汇总
        public static final String HospitalizedCostDetail = "/{demographic_id}/hospitalized/cost/detail"; //住院费用明细
        public static final String HospitalizedOrdersTemporary = "/{demographic_id}/hospitalized/orders/temporary"; //住院临时医嘱
        public static final String HospitalizedOrdersLongtime = "/{demographic_id}/hospitalized/orders/longtime"; //住院长期医嘱
        public static final String HospitalizedDeath = "/{demographic_id}/hospitalized/death"; //住院死因链情况


        public static final String CDADocumentId = "/profile/cda/document_id"; //cda模板（通过event_no + cda_type）
        public static final String CDAClass = "/{profile_id}/cda_class"; //cda分类
        public static final String CDAData = "/{profile_id}/cda_data"; //cda数据
        public static final String CDADocument = "/{profile_id}/cda_document"; //完整CDA文档


        public static final String ExaminationReport = "/{demographic_id}/examination"; //检查报告单
        public static final String ExaminationImg = "/{demographic_id}/examination/img"; //检查报告单图片


        public static final String LaboratoryReport = "/{demographic_id}/laboratory"; //检验报告单
        public static final String LaboratoryImg = "/{demographic_id}/laboratory/img"; //检验报告单图片
        public static final String LaboratoryProject = "/{demographic_id}/laboratory/project"; //检验报告单项目
        public static final String LaboratoryAllergy = "/{demographic_id}/laboratory/allergy"; //检验药敏

        public static final String Surgery = "/{demographic_id}/surgery";//手术-手术记录

        public static final String IndicatorsClass = "/{demographic_id}/indicators/class"; //获取某个健康问题指标
        public static final String IndicatorsData = "/{demographic_id}/indicators/data"; //获取指标数据
    }

    public static class Families {
        public static final String Families = "/families";
        public static final String Family = "/families/{id}";

        public static final String FamiliesMembers = "/families/members";
        public static final String FamilyMembers = "/families/{families_id}/members";
        public static final String FamilyMember = "/families/{families_id}/members/{id_card_no}";
    }

    public static class Cipher {
        public static final String Encryption = "/cipher/encryption/{type}";
        public static final String Decryption = "/cipher/decryption/{type}";
    }

    public static class Apps {
        public static final String Apps = "/apps";
        public static final String AppsNoPage = "/apps/no_paging";
        public static final String App = "/apps/{app_id}";
        public static final String AppExistence = "/apps/{app_id}/existence";
        public static final String AppStatus = "/apps/{app_id}/status";
        public static final String AppNameExistence = "/app/{app_name}/existence";
        public static final String FilterList = "/apps/filterList";
        public static final String getAppTypeAndApps = "/getAppTypeAndApps";
        public static final String getApps = "/getApps";
    }

    public static class AppApi {
        public static final String AppApis = "/appApi";
        public static final String AppApi = "/appApi/{id}";
        public static final String AppApisNoPage = "/appApiNoPage";
        public static final String AppApiSearch = "/appApi/search";
        public static final String AppApiAuth = "/appApiAuth";

    }

    public static class AppFeature {
        public static final String AppFeatures = "/appFeature";
        public static final String FilterFeatureList = "/filterFeatureList";
        public static final String FilterFeatureNoPage = "/filterFeatureNoPage";
        public static final String FilterFeatureNoPageSorts = "/filterFeatureNoPageSorts";
        public static final String AppFeature = "/appFeature/{id}";
        public static final String FindAppMenus = "/appFeature/findAppMenus";
    }

    public static class AppApiParameter {
        public static final String AppApiParameters = "/appApiParameter";
        public static final String AppApiParameter = "/appApiParameter/{id}";
    }

    public static class AppApiResponse {
        public static final String AppApiResponses = "/appApiResponse";
        public static final String AppApiResponse = "/appApiResponse/{id}";
    }

    public static class UserApp {
        public static final String UserAppList = "/userAppList";
        public static final String UserAppShow = "/userApp/show";
        public static final String GetUserAppListById = "/userApp/getUserAppByAppId";
    }



    public static class Packages {
        public static final String Packages = "/packages";
        public static final String AcquirePackage = "/packages/acquire";
        public static final String Package = "/packages/{id}";
        public static final String PackageResolve = "/packages/resolve";
        public static final String PackageSearch = "/packages/search";
        public static final String PackageDownloads = "/packages/{id}/downloads";

        public static final String ResolveMessage = "/message/resolve";

        public static final String MessageTimer = "/message/timer";

        public static final String LegacyPackages = "/json_package";

        public static final String Prescription = "/prescription"; //处方笺维护
        public static final String  ArchiveRelation = "/archiveRelation"; //档案关联
    }

    //非病人维度
    public static class DatasetPackages {
        public static final String Packages = "/datasetPackages";
        public static final String AcquirePackage = "/datasetPackages/acquire";
        public static final String Package = "/datasetPackages/{id}";
        public static final String PackageResolve = "/datasetPackages/resolve";
        public static final String PackageSearch = "/datasetPackages/search";
        public static final String PackageDownloads = "/datasetPackages/{id}/downloads";

        public static final String ResolveMessage = "/datasetPackages/message/resolve";
        public static final String MessageTimer = "/datasetPackages/message/timer";

    }

    public static class PackageResolve {
        public static final String Scheduler = "/scheduler";
    }


    public static class Patients {
        public static final String GetUserCards = "/getUserCards";// 就诊卡列表
        public static final String CardApply = "/patientCards/apply";	 //卡认证申请
        public static final String CardList = "/patientCards/list";  //	个人账号列表
        public static final String CardApplyListManager = "/patientCards/manager/applyList";//	管理员--卡认证列表
        public static final String CardVerifyManager = "/patientCards/manager/verify";//	管理员--卡认证审核操作
        public static final String CardBindManager = "/patientCards/manager/bind";//	管理员--后台绑卡操作
        public static final String ArchiveRelationManager = "/patientCards/manager/archiveRelation";//	管理员--卡档案关联审核

        public static final String ArchiveApply = "/patientArchive/apply";//	档案认领申请（临时卡）
        public static final String ArchiveApplyList = "/patientArchive/applyList";//	个人档案认领列表
        public static final String ArchiveList = "/patientArchive/list";//个人档案列表
        public static final String ArchiveUnbind = "/patientArchive/unbind";//	管理员--通过卡号获取未认领档案
        public static final String ArchiveApplyListManager = "/patientArchive/manager/applyList";//	管理员--档案认领列表
        public static final String ArchiveVerifyManager = "/patientArchive/manager/verify";//	管理员--档案认领审核操作
        public static final String ArchiveRelation = "/patientArchive/archiveRelation";//新建档案关联
        public static final String GetArchiveList = "/patientArchive/getApplyList";//根据查询条件查询个人档案申请列表
        public static final String GetArchiveRelationList = "/patientArchive/getArRelationList";//根据查询条件查询个人档案申请列表
        public static final String GetArchiveRelation = "/patientArchive/{applyId}/getArRelation";//根据查询条件查询个人档案申请列表


        public static final String FindArchiveRelation = "/patient/findArchiveRelation";
        public static final String UpdateArchiveRelation = "/patient/updateArchiveRelation";
        public static final String DelArchiveRelation = "/patient/delArchiveRelation";

        public static final String MCardSave = "/medicalCards/save";// 就诊卡新增&保存
        public static final String MCardDel = "/medicalCards/del";//	就诊卡删除
        public static final String GetMCard = "/medicalCards/get";//就诊卡详情
        public static final String GetMCards = "/getMedicalCards";//	就诊卡列表
        public static final String MCardCheckCardNo = "/medicalCards/checkCardNo";//	校验卡是否唯一
        public static final String MCardGetMutiCardNo = "/medicalCards/getMutiCard";//	获取重复的卡列表
        public static final String MCarddataBatch = "/medicalCards/batch";//	批量添加

        public static final String Authentications = "/patient/authentications";
        public static final String Authentication = "/patient/Authentication/{id}";






        /******************************* 旧接口 **********************************************************/
        public static final String ArApplications = "/archive/applications";
        public static final String ArApplication = "/archive/applications/{id}";

        public static final String ArRelations = "/archive/relations";
        public static final String ArRelation = "/archive/relation/{id}";
        public static final String ArRelationsExistence = "/archive/relations/existence";
        /******************************* 旧接口 **********************************************************/
    }

    public static class Authentication {
        public static final String AccessToken = "/oauth/accessToken";
        public static final String ValidToken = "/oauth/validToken";
        public static final String RefreshToken = "/oauth/refreshToken";
    }

    public static class PortalNotices {
        public static final String PortalNotices = "/portalNotices";
        public static final String PortalNoticesTop = "/portalNoticesTop";
        public static final String PortalNoticeAdmin = "/portalNotices/admin/{portalNotice_id}";
    }

    public static class MessageRemind{
        public static final String MessageRemind                            = "/messageRemind";
        public static final String MessageRemindTop                            = "/messageRemindTop";
        public static final String MessageRemindCount                         = "/messageRemindCount";
        public static final String MessageRemindAdmin                        = "/messageRemind/admin/{messageRemind_id}";
        public static final String MessageRemindReaded                         = "/messageRemind/readed/{remindId}";
    }

    public static class PortalResources{
        public static final String PortalResources                            = "/portalResources";
        public static final String PortalResourcesTop                         = "/portalResourcesTop";
        public static final String PortalResourcesAdmin                       = "/portalResources/admin/{portalResources_id}";
    }

    public static class PortalFeedback {
        public static final String PortalFeedback = "/portalFeedback";
        public static final String PortalFeedbackAdmin = "/portalFeedback/admin/{portalFeedback_id}";
    }

    public static class PortalLogin {
        public static final String PortalLogin = "/login";
    }


    public static class PortalSetting {
        public static final String PortalSetting = "/portalSetting";
        public static final String PortalSettingTop  = "/portalSettingTop";
        public static final String PortalSettingAdmin  = "/portalSetting/admin/{portalSetting_id}";
    }

    public static class PortalStandards {
        public static final String PortalStandards = "/portalStandards";
        public static final String PortalStandardsAdmin = "/portalStandards/admin/{portalStandard_id}";
    }

    public static class Securities {
        public static final String UserKey = "/securities/users/{user_id}/key";
        public static final String UserKeyId = "/securities/users/{user_id}/key/id";
        public static final String UserPublicKey = "/securities/users/{user_id}/key/public";
        public static final String UserTokens = "/securities/users/{user_id}/tokens";
        public static final String UserToken = "/securities/users/{user_id}/tokens/{token_id}";
        public static final String OrganizationKey = "/securities/organizations/{org_code}/key";
        public static final String OrganizationPublicKey = "/securities/organizations/{org_code}/key/public";
        public static final String ClientTokens = "/securities/clients/{client_id}/tokens";

        public static final String Keys = "/securities/keys/{id}";
        public static final String deleteOrgKey = "/securities/org/keys/{org_code}";
        public static final String deleteUserKey = "/securities/user/keys/{user_id}";
        public static final String Tokens = "/securities/tokens/{id}";
    }


    public static class Standards {
        public static final String Caches = "/std/caches";
        public static final String Cache = "/std/caches/{version}";
        public static final String Sources = "/std/sources";
        public static final String NoPageSources = "/std/sources/no_paging";
        public static final String Source = "/std/sources/{id}";
        public static final String IsSourceCodeExist = "/std/sources/code_is_exist";

        public static final String NoPageTypes = "/std/types/no_paging";
        public static final String Types = "/std/types";

        public static final String Type = "/std/types/{id}";
        public static final String TypesCodeExistence = "/std/type/code/existence";
        public static final String TypeChildren = "/std/types/parent_id/childrens";
        public static final String TypesChildren = "/std/types/childrens";
        public static final String TypeList = "/cda_types/code_name";
        public static final String TypeParent = "/std/types/parent";
        public static final String TypeOther = "/std/types/{id}/other";

        public static final String Versions = "/std/versions";
        public static final String Version = "/std/versions/{version}";
        public static final String VersionInStageExist = "/std/versions/in_stage/existence";
        public static final String VersionCache = "/std/versions/{version}/cache";
        public static final String VersionNameExistence = "/std/version/name/existence";
        public static final String VersionBackStage = "/std/versions/{version}/back";
        public static final String VersionCommit = "/std/versions/{version}/commit";
        public static final String VersionRevert = "/std/versions/{version}/revert";
        public static final String VersionLatest = "/std/versions/latest";
        public static final String VersionLatestExistence = "/std/versions/{version}/latest/existence";

        public static final String Documents = "/std/documents";
        public static final String Document = "/std/documents/{id}";
        public static final String DocumentList = "/std/documentsList";
        public static final String DocumentFileExistence = "/std/documents/{id}/file/existence";
        public static final String DocumentCreateFile = "/std/documents/{id}/file/create";
        public static final String DocumentGetFile = "/std/documents/{id}/file";

        public static final String DocumentDataSet = "/std/documents/data_set";
        public static final String DocumentDataSetList = "/std/documents/data_set_list";
        public static final String DataSetRelationships = "/std/documents/data_set_relationships";
        public static final String DataSetRelationship = "/std/documents/data_set_relationships/{id}";

        public static final String Dictionaries = "/std/dictionaries";
        public static final String NoPageDictionaries = "/std/dictionaries/no_paging";
        public static final String Dictionary = "/std/dictionaries/{id}";
        public static final String DictionaryCode = "/std/dictionaries/code";
        public static final String MetaDataWithDict = "/std/data_set/{data_set_id}/meta_datas/{meta_data_id}/dictionaries";
        public static final String DictCodeIsExist = "/std/dictionaries/is_exist/code";
        public static final String DictOther = "/std/dictionaries/{id}/other";
        public static final String DictParent = "/std/dictionaries/{id}/parent";
        public static final String DictCodesExistence = "/dict/codes/existence";
        public static final String DictEntryBatch = "/dict/entry/batch";

        public static final String Entry = "/std/dictionaries/entries/{id}";
        public static final String Entries = "/std/dictionaries/entries";
        public static final String EntriesWithDictionary = "/std/dictionary/{dict_id}/entries";
        public static final String EntryCodeIsExist = "/std/dictionaries/entries/is_exist/code";

        public static final String DataSets = "/std/data_sets";
        public static final String NoPageDataSets = "/std/data_sets/no_paging";
        public static final String DataSet = "/std/data_sets/{id}";
        public static final String DataSetsName = "/std/data_sets/name";
        public static final String DataSetCodeIsExist = "/std/data_set/is_exist/code";
        public static final String DataSetsIds = "/std/{ids}data_sets";
        public static final String DataSetsBatch = "/std/data_set/bacth";

        public static final String MetaDatas = "/std/data_sets/meta_datas";
        public static final String MetaData = "/std/data_sets/meta_datas/{id}";
        public static final String MetaDataCodeExistence = "/std/data_sets/{data_set_id}/meta_data/inner_code/existence";
        public static final String MetaDataNameExistence = "/std/data_sets/{data_set_id}/meta_data/name/existence";
        public static final String MetaDatasName = "/std/data_sets/meta_datas/name";
        public static final String MetaDatasWithDataSet = "/std/data_sets/{data_set_id}/meta_datas";

        public static final String DispatchLogs = "/std/dispatches/logs";

        public static final String Dispatch = "/std/dispatches/{id}";
        public static final String Dispatches = "/std/dispatches";
    }

    public static class Doctors {
        public static final String Doctors = "/doctors";
        public static final String DoctorsExistence = "/doctors/{doctor_code}/existence";
        public static final String DoctorAdmin = "/doctors/admin/{doctor_id}";
        public static final String DoctorPhoneExistence = "/doctor/phone/existence";
        public static final String DoctorBatch = "/doctor/batch";
        public static final String DoctorOnePhoneExistence = "/doctor/onePhone/existence";
        public static final String DoctorEmailExistence = "/doctor/email/existence";
        public static final String DoctorsIdCardNoExistence = "/doctors/{doctor_idCardNo}/CardNoExist";
        public static final String DoctoridCardNoExistence = "/doctor/idCardNo/existence";




    }

    public static class Users {
        public static final String Users = "/users";
        public static final String User = "/users/{user_name}";
        public static final String UserExistence = "/users/{user_name}/existence";
        public static final String UserVerification = "/users/verification";
        public static final String UserAdmin = "/users/admin/{user_id}";
        public static final String UserAdminPassword = "/users/admin/{user_id}/password";
        public static final String UserAdminPasswordReset = "/users/admin/{user_id}/password_reset";
        public static final String UserAdminKey = "/users/admin/{user_id}/key";
        public static final String UserAdminContact = "/users/admin/{user_id}/contact";
        public static final String UserIdCardNoExistence = "/user/id_card_no/existence";
        public static final String UserEmailNoExistence = "/user/email/existence";
        public static final String UserTelephoneNoExistence = "/user/telephone/existence";
        public static final String UserPhoneExistence = "/user/phone/existence";
        public static final String UserOnePhoneExistence = "/user/onePhone/existence";
        public static final String UserEmailExistence = "/user/email/existence";
        public static final String UseridCardNoExistence = "/user/idCardNo/existence";
        public static final String UserByIdCardNo = "/user/idCardNo/userByIdCardNo";

    }

    public static class Roles {
        public static final String Role = "/roles/role";
        public static final String RoleId = "/roles/role/{id}";
        public static final String Roles = "/roles/roles";
        public static final String RolesNoPage = "/roles/roles/no_page";
        public static final String RoleNameExistence = "/roles/name/existence";
        public static final String RoleCodeExistence = "/roles/code/existence";
        public static final String RoleBatchAdd = "/roles/roleBatchAdd";


        public static final String RoleUser = "/roles/role_user";
        public static final String RoleUserByUserId = "/roles/role_user/{user_id}";
        public static final String RoleUsers = "/roles/role_users";
        public static final String RoleUsersNoPage = "/roles/role_users/no_page";
        public static final String NoPageCategoriesAndReport = "/roles/report/getCategoryAndReportNoPage";

        public static final String RoleOrg = "/roles/role_org";
        public static final String RoleOrgs = "/roles/role_orgs";
        public static final String RoleOrgsNoPage = "/roles/role_orgs/no_page";

        public static final String RoleFeature = "/roles/role_feature";
        public static final String RoleFeatureId = "/roles/role_feature/{id}";
        public static final String RoleFeatureByRoleId = "/roles/role_feature/role_id";
        public static final String RoleFeatures = "/roles/role_features";
        public static final String RoleFeaturesNoPage = "/roles/role_features/no_page";
        public static final String RoleFeatureExistence = "/roles/role_feature/existence";
        public static final String HasPermission = "/roles/role_feature/hasPermission";

        public static final String RoleApp = "/roles/role_app";
        public static final String RoleAppId = "/roles/role_app/{id}";
        public static final String RoleApps = "/roles/role_apps";
        public static final String RoleAppsNoPage = "/roles/role_apps/no_paging";

        public static final String RoleApi = "/roles/role_api";
        public static final String RoleApiId = "/roles/role_api/{id}";
        public static final String RoleApiByRoleId = "/roles/role_api/role_id";
        public static final String RoleApis = "/roles/role_apis";
        public static final String RoleApisNoPage = "/roles/role_apis/no_paging";
        public static final String RoleApisExistence = "/roles/role_api/existence";

        public static final String BatchAddRoleReportRelation = "/roles/role_report/batchAddRoleReportRelation";
        public static final String DeleteRoleReportRelationByRoleId = "/roles/role_report/deleteByRoleId";
        public static final String SearchRoleReportRelation = "/roles/role_report/search";
        public static final String SearchRoleReportRelationNoPage = "/roles/role_report/searchNoPage";
        public static final String SearchRoleReportRelationIsReportAccredited = "/roles/role_report/isReportAccredited";

    }


    public static class HealthProfile {
        public static final String Profiles = "/health_profiles";
        public static final String ProfileSearch = "/health_profiles/search";
        public static final String Profile = "/health_profiles/{profile_id}";
        public static final String ProfileDocument = "/health_profiles/{profile_id}/documents/{document_id}";

        public static final String Diagnosis = "/health_profile/disease/diagnosis";
        public static final String MajorIssues = "/health_profile/disease/major_issues";

        public static final String Prescriptions = "/health_profile/prescriptions";
        public static final String Drugs = "/health_profile/drugs";
        public static final String Lis = "/health_profile/lis";
        public static final String PhysicalExam = "/health_profile/physical_examinations";
        public static final String PastHistories = "/health_profile/past_histories";
    }

    public static class ProfileTemplate {
        public static final String Templates = "/templates";
        public static final String TemplatesDownloads = "/templates/downloads";
        public static final String Template = "/templates/{id}";
        public static final String TemplateCtn = "/templates/{id}/content";
        public static final String TemplateTitleExistence = "/template/title/existence";
    }

    public static class SanofiSupport {
        public static final String PhysicSigns = "/sanofi/physic_signs";
    }

    public static class ArchiveSecurity {
        public static final String ArchivePrivate = "/archiveprivate/{userId}";
        public static final String ArchivePrivateRowKey = "/archiveprivate/{userId}/{rowKey}";

        public static final String ArchiveSecuritySetting = "/archivesecurity";
        public static final String ArchiveSecuritySettingUser = "/archivesecurity/{user_id}";
        public static final String ArchiveSecuritySettingKeyAuthen = "/archivesecurity/{user_id}/securitykey/authentication";

        public static final String ArchiveLogs = "/archivelogs";

        public static final String AuthorizeApps = "/authorizeapps";
        public static final String AuthorizeAppsId = "/authorizeapps/{id}";

        public static final String AuthorizeAppsSubjects = "/authorizeapps/subjects";
        public static final String AuthorizeAppsSubjectsId = "/authorizeapps/subjects/{id}";

        public static final String AuthorizeSubjects = "/authorizesubjects";

        public static final String AuthorizeSubjectsResources = "/authorizesubjects/{subjectId}/resources";

        public static final String AuthorizeDoctors = "/authorizedoctors";
        public static final String AuthorizeDoctorsId = "/authorizedoctors/{id}";
        public static final String AuthorizeDoctorsIdAlteration = "/authorizedoctors/{id}/alteration";
        public static final String AuthorizeDoctorsIdAuthorization = "/authorizedoctors/{id}/authorization";

        public static final String MessageReplyTempates = "/messagereplytemplates";

        public static final String MessageTempates = "/messagetemplates";
        public static final String MessageTempatesCode = "/messagetemplates/{messageTempCode}";

        public static final String MessageSend = "/messagesend";
        public static final String MessageSendId = "/messagesend/{id}";

        public static final String MessageReply = "/messagereply";
    }

    /**
     * 病历夹接口
     */
    public static class MedicalRecords {

        //公用接口
        public static final String SystemAccess = "/medicalRecords/systemAccess"; //系统接入接口，通过单点登录获取最新病历
        public static final String SystemDict = "/medicalRecords/systemDict";
        public static final String SystemDictEntry = "/medicalRecords/systemDictEntry"; //获取系统字典项
        public static final String ICD10Dict = "/medicalRecords/icd10Dict"; //获取ICD10字典
        public static final String ImgList = "/medicalRecords/imgList"; //图片列表

        //病历接口
        public static final String AddRecord = "/medicalRecords/record/addRecord"; //新增病历
        public static final String MedicalRecordRelated = "/medicalRecords/record/related"; //	病历关联记录
        public static final String MedicalRecord = "/medicalRecords/record/{record_id}"; //	病历信息管理
        public static final String MedicalInfo = "/medicalRecords/record/{record_id}/medicalInfo"; //	病情信息管理
        public static final String MedicalLabel = "/medicalRecords/record/{record_id}/label"; //获取病历标签/批量保存病历标签
        public static final String MedicalReport = "/medicalRecords/record/{record_id}/report/{report_id}"; //获取某病历某报告
        public static final String MedicalReports = "/medicalRecords/record/{record_id}/reports"; //获取某病历某报告
        public static final String MedicalReportManage = "/medicalRecords/record/{record_id}/report"; //新增/修改/导入报告/删除报告
        public static final String ImportMedicalPrescription = "/medicalRecords/record/{record_id}/drug/import"; //导入处方
        public static final String MedicalDrug = "/medicalRecords/record/{record_id}/drug"; //新增/修改病历用药记录/删除病历用药记录/获取病历用药记录
        public static final String MedicalShare = "/medicalRecords/record/{record_id}/share"; //病历分享
        public static final String MedicalDiagnosis = "/medicalRecords/record/{record_id}/diagnosis";//病历诊断

        //医生接口
        public static final String DoctorInfo = "/medicalRecords/doctor/{doctor_id}"; //获取医生信息
        public static final String DoctorLabelClass = "/medicalRecords/doctor/{doctor_id}/labelClass"; //获取全部医生标签类别
        public static final String DoctorLabel = "/medicalRecords/doctor/{doctor_id}/label"; //获取医生标签
        public static final String DoctorTemplate = "/medicalRecords/doctor/{doctor_id}/template"; //获取医生模板/批量保存医生模板
        public static final String DoctorRecords = "/medicalRecords/doctor/{doctor_id}/records"; //获取医生病历
        public static final String DoctorText = "/medicalRecords/doctor/{doctor_id}/text"; //上传医生文本素材/获取医生文本素材
        public static final String DoctorTextDialog = "/medicalRecords/doctor/{doctor_id}/textDialog"; //获取医生文本对话
        public static final String DoctorImg = "/medicalRecords/doctor/{doctor_id}/img"; //上传医生图片素材/获取医生图片素材
        public static final String DoctorICD10 = "/medicalRecords/doctor/{doctor_id}/icd10"; //	获取医生ICD10
        public static final String DoctorInfoManage = "/medicalRecords/doctor/info"; //	新增医生信息/修改医生信息
        public static final String DoctorLabelClassManage = "/medicalRecords/doctor/labelClass"; //	新增医生标签类别/修改医生标签类别/删除医生标签类别


        //患者接口
        public static final String PatientInfo = "/medicalRecords/patient/{patient_id}/info"; //	获取患者基本信息
        public static final String PatientInfoManage = "/medicalRecords/patient/info"; //	新增患者信息/修改患者信息
        public static final String PatientRecords = "/medicalRecords/patient/{patient_id}/records"; //	获取患者病历
        public static final String PatientICD10 = "/medicalRecords/patient/{patient_id}/icd10"; //	获取患者ICD10


    }

    public static class DailyStatistics {
        //日常监测
        public static final String StatisticsProfile = "/statistics/profiles";
        public static final String StatisticsOutpatientHospital = "/statistics/outpatientHospital";
        public static final String StatisticsDailyReport = "/statistics/dailyReport";
        public static final String StatisticsDailyReportFiles = "/statistics/dailyReportFiles";

        //住院
        public static final String StatisticsHospitalization = "/statistics/hospitalization/{orgCode}";
        public static final String StatisticsHospitalizationDept = "/statistics/hospitalization/{orgCode}/dept";
        public static final String StatisticsHospitalizationSex = "/statistics/hospitalization/{orgCode}/sex";
        public static final String StatisticsHospitalizationDisease = "/statistics/hospitalization/{orgCode}/disease";

        //门诊
        public static final String StatisticsOutpatient = "/statistics/outpatient/{orgCode}";
        public static final String StatisticsOutpatientDept = "/statistics/outpatient/{orgCode}/dept";
        public static final String StatisticsOutpatientSex = "/statistics/outpatient/{orgCode}/sex";

        //入库统计
        public static final String StatisticsProfileCreateDate = "/statistics/profiles/createDate";
        public static final String StatisticsProfileEventDate = "/statistics/profiles/eventDate";
        public static final String StatisticsProfileIdNotNull = "/statistics/profiles/idNotNull";
        public static final String StatisticsProfileEventDateGroup = "/statistics/profiles/eventDateGroup";


    }

    /**
     * Redis服务
     */
    public static class Redis {

        //初始化缓存
        public static final String InitAddress = "/redis/init/address";
        public static final String InitHealthProblem = "/redis/init/healthProblem";
        public static final String InitIcd10HpR = "/redis/init/icd10HpR";
        public static final String InitIcd10 = "/redis/init/icd10";
        public static final String InitIndicatorsDict = "/redis/init/indicatorsDict";
        public static final String InitOrgName = "/redis/init/orgName";
        public static final String InitOrgArea = "/redis/init/orgArea";
        public static final String InitOrgSaasArea = "/redis/init/orgSaasArea";
        public static final String InitOrgSaasOrg = "/redis/init/orgSaasOrg";
        public static final String InitVersions = "/redis/init/versions";
        public static final String InitRsAdapterDict = "/redis/init/rsAdapterDict/{id}";
        public static final String InitRsAdapterMeta = "/redis/init/rsAdapterMeta/{id}";
        public static final String InitRsMetadata = "/redis/init/rsMetadata";

        //清除缓存
        public static final String Delete = "/redis/delete";

        //更新缓存
        public static final String UpdateOrgName = "/redis/update/orgName";
        public static final String UpdateOrgArea = "/redis/update/orgArea";
        public static final String UpdateOrgSaasArea = "/redis/update/orgSaasArea";
        public static final String UpdateOrgSaasOrg = "/redis/update/orgSaasOrg";

        //获取缓存数据
        public static final String Address = "/redis/address";
        public static final String HealthProblem = "/redis/healthProblem";
        public static final String Icd10HpR = "/redis/icd10HpRelation";
        public static final String Icd10Name = "/redis/icd10Name";
        public static final String Icd10HpCode = "/redis/icd10HpCode";
        public static final String IndicatorsDict = "/redis/indicatorsDict";
        public static final String OrgName = "/redis/orgName";
        public static final String OrgArea = "/redis/orgArea";
        public static final String OrgSaasArea = "/redis/orgSaasArea";
        public static final String OrgSaasOrg = "/redis/orgSaasOrg";

        //App前端Redis
        public static final String AppGetRedisValue = "/redis/getAppClientValue";
        public static final String AppSetRedisValue = "/redis/setAppClientValue";
        public static final String AppSetRedisJsonValue = "/redis/setAppClientJsonValue";
        public static final String AppDeleteRedisValue = "/redis/deleteAppClientValue";

        //资源化相关Redis
        public static final String RsAdapterDict = "/redis/rsAdapterDict";
        public static final String RsAdapterMetadata = "/redis/rsAdapterMetaData";
        public static final String RsMetadata = "/redis/rsMetadata";
        //标准相关Redis
        public static final String StdVersion = "/redis/stdVersion";
        public static final String StdDataSetCode = "/redis/stdDataSetCode";
        public static final String StdDataSetName = "/redis/stdDataSetName";
        public static final String StdDataSetNameByCode = "/redis/stdDataSetNameByCode";
        public static final String StdDataSetMultiRecord = "/redis/stdDataSetMultiRecord";
        public static final String StdMetadataType = "/redis/stdMetaDataType";
        public static final String StdMetadataDict = "/redis/stdMetaDataDict";
        public static final String StdDictEntryValue = "/redis/stdDictEntryValue";

        // Redis消息队列
        public static class MqChannel {
            public static final String Prefix = "/redis/mq/channel/";
            public static final String GetById = "/redis/mq/channel/{id}";
            public static final String Search = "/redis/mq/channel/search";
            public static final String Save = "/redis/mq/channel/save";
            public static final String Delete = "/redis/mq/channel/delete";
            public static final String IsUniqueChannel = "/redis/mq/channel/isUniqueChannel";
            public static final String IsUniqueChannelName = "/redis/mq/channel/isUniqueChannelName";
            public static final String SendMessage = "/redis/mq/channel/sendMessage";
        }
        // Redis消息订阅者
        public static class MqSubscriber {
            public static final String Prefix = "/redis/mq/subscriber/";
            public static final String GetById = "/redis/mq/subscriber/{id}";
            public static final String Search = "/redis/mq/subscriber/search";
            public static final String Save = "/redis/mq/subscriber/save";
            public static final String Delete = "/redis/mq/subscriber/delete";
            public static final String IsUniqueAppId = "/redis/mq/subscriber/isUniqueAppId";
            public static final String IsUniqueSubscribedUrl = "/redis/mq/subscriber/isUniqueSubscribedUrl";
        }
        // Redis消息发布者
        public static class MqPublisher {
            public static final String Prefix = "/redis/mq/publisher/";
            public static final String GetById = "/redis/mq/publisher/{id}";
            public static final String Search = "/redis/mq/publisher/search";
            public static final String Save = "/redis/mq/publisher/save";
            public static final String Delete = "/redis/mq/publisher/delete";
            public static final String IsUniqueAppId = "/redis/mq/publisher/isUniqueAppId";
        }
        // 缓存分类
        public static class CacheCategory {
            public static final String Prefix = "/redis/cache/category/";
            public static final String GetById = "/redis/cache/category/{id}";
            public static final String Search = "/redis/cache/category/search";
            public static final String SearchNoPage = "/redis/cache/category/searchNoPage";
            public static final String Save = "/redis/cache/category/save";
            public static final String Delete = "/redis/cache/category/delete";
            public static final String IsUniqueName = "/redis/cache/category/isUniqueName";
            public static final String IsUniqueCode = "/redis/cache/category/isUniqueCode";
        }
        // 缓存授权
        public static class CacheAuthorization {
            public static final String Prefix = "/redis/cache/authorization/";
            public static final String GetById = "/redis/cache/authorization/{id}";
            public static final String Search = "/redis/cache/authorization/search";
            public static final String Save = "/redis/cache/authorization/save";
            public static final String Delete = "/redis/cache/authorization/delete";
            public static final String IsUniqueAppId = "/redis/cache/authorization/isUniqueAppId";
        }
        // 缓存Key规则
        public static class CacheKeyRule {
            public static final String Prefix = "/redis/cache/keyRule/";
            public static final String GetById = "/redis/cache/keyRule/{id}";
            public static final String Search = "/redis/cache/keyRule/search";
            public static final String Save = "/redis/cache/keyRule/save";
            public static final String Delete = "/redis/cache/keyRule/delete";
            public static final String IsUniqueName = "/redis/cache/keyRule/isUniqueName";
            public static final String IsUniqueCode = "/redis/cache/keyRule/isUniqueCode";
        }
        // 缓存操作
        public static class CacheOperation {
            public static final String Get = "/redis/cache/operation/get";
            public static final String Set = "/redis/cache/operation/set";
            public static final String Remove = "/redis/cache/operation/remove";
        }
        // 缓存操作
        public static class CacheStatistics {
            public static final String GetCategoryKeys = "/redis/cache/statistics/getCategoryKeys";
        }
        // 接收订阅消息
        public static class SubscribeMessage {
            public static final String ReceiveResponseTime = "/redis/subscribeMessage/receiveResponseTime";
        }

    }

    /**
     * 行政区划服务
     */
    public static class Geography {
        public static final String Address = "/geographies/{id}";//根据地址编号查询地址
        public static final String AddressCanonical = "geographies/{id}/canonical";//根据地址编号获取地址中文字符串全拼
        public static final String Geographies = "/geographies"; //地址增改查
        public static final String GeographiesDelete = "geographies/{id}";
        public static final String GeographiesNull = "/geographies/is_null";//判断是否是个空地址
        public static final String AddressDictByLevel = "/geography_entries/level/{level}";//根据等级查询行政区划地址
        public static final String AddressDictByPid = "/geography_entries/pid/{pid}";//根据上级编号查询行政区划地址
        public static final String AddressDict = "/geography_entries/{id}";//根据id查询行政区划地址
        public static final String AddressDictList = "/geography_entries_list";  //获取多条行政区划地址
        public static final String AddressDictAll = "/geography_entries/all";    //获取全部行政区划地址
        public static final String AddressDictByFields = "/geography_entries/getAddressDict";     //根据地址中文名 查询地址编号
        public static final String GetAddressNameByCode = "/geography/GetAddressNameByCode";     //根据地址中文名 查询地址编号
    }


    public static class Report {

        public static final String GetQcDailyReportList = "/report/getQcDailyReportList";
        public static final String QcDailyReport = "/report/qcDailyReport";
        public static final String GetEventDataReport = "/report/getEventDataReport";
        public static final String AddQcDailyReportDetailList = "/report/addQcDailyReportDetailList";
        public static final String AddOrUpdateQcDailyReportDetail = "/report/addorUpdateQcDailyReportDetail";
        public static final String GetQcDailyReportDetail = "/report/getQcDailyReportDetail";
        public static final String GetQcDailyReportPageList = "/report/getQcDailyReportPageList";

        public static final String GetJsonArchives = "/report/getGetJsonArchives";


        public static final String GetQcDailyReportDatasetsList = "/report/getQcDailyReportDatasetsList";
        public static final String QcDailyReportDatasets = "/report/qcDailyReportDatasets";

        public static final String GetQcDailyReportDatasetList = "/report/getQcDailyReportDatasetList";
        public static final String QcDailyReportDataset = "/report/qcDailyReportDataset";
        public static final String AddQcDailyDatasetDetailList = "/report/addQcDailyDatasetDetailList";

        public static final String GetQcDailyReportMetadataList = "/report/getQcDailyReportMetadataList";
        public static final String QcDailyReportMetadata = "/report/qcDailyReportMetadata";
        public static final String AddQcDailyMetadataDetailList = "/report/addQcDailyMetadataDetailList";


        public static final String GetQcQuotaDictList = "/report/getQcQuotaDictList";
        public static final String QcQuotaDict = "/report/qcQuotaDict";

        public static final String GetQcQuotaResultList = "/report/getQcQuotaResultList";
        public static final String QcQuotaResult = "/report/qcQuotaResult";

        public static final String QcDailyReportReolve = "/report/qcDailyReportReolve";
        public static final String GetQcQuotaOrgIntegrity = "/report/getQcQuotaOrgIntegrity";
        public static final String GetQcQuotaIntegrity = "/report/getQcQuotaIntegrity";

        public static final String QcQuotaList = "/report/qcQuotaList";
        public static final String GetQcDailyIntegrity = "/report/getQcDailyIntegrity";
        public static final String GetQcOverAllIntegrity = "/report/getQcOverAllIntegrity";
        public static final String GetQcOverAllOrgIntegrity = "/report/getQcOverAllOrgIntegrity";
        public static final String GetQcQuotaDailyIntegrity = "/report/getQcQuotaDailyIntegrity";
        public static final String GetQcQuotaByLocation = "/report/getQcQuotaByLocation";


        public static final String QcDailyStatisticsStorage = "/report/qcDailyStatisticsStorage";
        public static final String QcDailyStatisticsStorageByDate = "/report/qcDailyStatisticsStorageByDate";
        public static final String QcDailyStatisticsIdentify = "/report/qcDailyStatisticsIdentify";


    }

    public static class TJ {


        public static final String GetTjDimensionMainList = "/tj/getTjDimensionMainList";
        public static final String TjDimensionMain = "/tj/tjDimensionMain";
        public static final String TjDimensionMainId = "/tj/tjDimensionMainId/{id}";
        public static final String TjDimensionMainCode= "/tj/tjDimensionMainCode";
        public static final String TjDimensionMainName= "/tj/tjDimensionMainName";
        public static final String GetTjDimensionMainInfoList= "/tj/getTjDimensionMainInfoList";

        public static final String GetTjDimensionSlaveList = "/tj/getTjDimensionSlaveList";
        public static final String TjDimensionSlaveId = "/tj/tjDimensionSlaveId/{id}";
        public static final String TjDimensionSlave = "/tj/tjDimensionSlave";
        public static final String TjDimensionSlaveCode = "/tj/tjDimensionSlaveCode";
        public static final String TjDimensionSlaveName= "/tj/tjDimensionSlaveName";
        public static final String GetTjDimensionSlaveInfoList= "/tj/getTjDimensionSlaveInfoList";
        public static final String GetTjDimensionSlaveByCode= "/tj/getTjDimensionSlaveByCode";



        public static final String GetTjQuotaDimensionMainList = "/tj/getTjQuotaDimensionMainList";
        public static final String TjQuotaDimensionMain = "/tj/tjQuotaDimensionMain";
        public static final String AddTjQuotaDimensionMain = "/tj/addTjQuotaDimensionMain";
        public static final String GetTjQuotaDimensionMainAll = "/tj/getTjQuotaDimensionMainAll";

        public static final String GetTjQuotaDimensionSlaveList = "/tj/getTjQuotaDimensionSlaveList";
        public static final String TjQuotaDimensionSlave = "/tj/tjQuotaDimensionSlave";
        public static final String AddTjQuotaDimensionSlave = "/tj/addTjQuotaDimensionSlave";
        public static final String GetTjQuotaDimensionSlaveAll = "/tj/getTjQuotaDimensionSlaveAll";
        public static final String DeleteSlaveByQuotaCode = "/tj/deleteSlaveByQuotaCode";
        public static final String GetDimensionSlaveByQuotaCode = "/tj/getDimensionSlaveByQuotaCode";


        public static final String GetTjQuotaSynthesiseDimension = "/tj/getTjQuotaSynthesiseDimension";
        public static final String GetTjQuotaSynthesiseDimensionKeyVal = "/tj/getTjQuotaSynthesiseDimensionKeyVal";
        public static final String GetTjQuotaChartList = "/tj/getTjQuotaChartList";

        public static final String GetTjQuotaLogList = "/tj/getTjQuotaLogList";
        public static final String GetTjQuotaLogRecentRecord = "/tj/getTjQuotaLogRecentRecord";


        public static final String GetTjDataSaveList = "/tj/getTjDataSaveList";
        public static final String AddTjDataSave = "/tj/addTjDataSave";
        public static final String DeleteTjDataSave = "/tj/deleteTjDataSave";
        public static final String GetTjDataSaveById = "/tj/getTjDataSaveById/{id}";

        public static final String GetTjDataSourceList = "/tj/getTjDataSourceList";
        public static final String AddTjDataSource = "/tj/addTjDataSource";
        public static final String DeleteTjDataSource = "/tj/deletetTjDataSource";
        public static final String GetTjDataSourceById = "/tj/getTjDataSourceById/{id}";
        public static final String TjDataSourceExistsName = "/tj/tjDataSourceExistsName/{name}";
        public static final String TjDataSourceExistsCode = "/tj/tjDataSourceExistsCode/{code}";

        public static final String GetTjQuotaDataSaveList = "tj/getTjQuotaDataSaveList";
        public static final String AddTjQuotaDataSave = "tj/addTjQuotaDataSave";
        public static final String DeleteTjQuotaDataSave = "tj/deleteTjQuotaDataSave";

        public static final String GetTjQuotaDataSourceList = "tj/getTjQuotaDataSourceList";
        public static final String AddTjQuotaDataSource = "tj/addTjQuotaDataSource";
        public static final String DeleteTjQuotaDataSource = "tj/deleteTjQuotaDataSource";

        public static final String GetTjQuotaList = "tj/getTjQuotaList";
        public static final String AddTjQuota = "tj/addTjQuota";
        public static final String UpdateTjQuota = "tj/updateTjQuota";
        public static final String DeleteTjQuota = "tj/deleteTjQuota";
        public static final String GetTjQuotaById = "tj/getTjQuotaById/{id}";
        public static final String TjQuotaExistsName = "/tj/tjQuotaExistsName/{name}";
        public static final String TjQuotaExistsCode = "/tj/tjQuotaExistsCode/{code}";
        public static final String GetTjQuotaByCode = "/tj/getTjQuotaByCode";
        public static final String TjQuotaConfigInfo = "/tj/quotaConfigInfo";
        public static final String TjHasConfigDimension = "/tj/hasConfigDimension";


        public static final String TjGetQuotaResult = "/tj/tjGetQuotaResult";
        public static final String TjQuotaExecute = "/job/execuJob";


        public static final String GetTjQuotaWarn = "tj/getTjQuotaWarn";
        public static final String GetQuotaReport = "tj/getQuotaReport";
        public static final String GetQuotaGraphicReportPreview = "tj/getQuotaGraphicReportPreview";
        public static final String GetMoreQuotaGraphicReportPreviews = "tj/getMoreQuotaGraphicReportPreviews";
        public static final String GetQuotaTotalCount = "tj/getQuotaTotalCount";
        public static final String GetQuotaGroupBy = "tj/getQuotaGroupBy";



        public static final String TjQuotaChart = "/tj/tjQuotaChart";
        public static final String BatchTjQuotaChart = "/tj/batchTjQuotaChart";
        public static final String TjQuotaChartId = "/tj/tjQuotaChart/{id}";
        public static final String GetAllTjQuotaChart = "/tj/getAllTjQuotaChart";



    }

    public static class StasticReport {
        public static final String GetStatisticsElectronicMedicalCount="/stasticReport/getStatisticsElectronicMedicalCount";
        public static final String GetStatisticsMedicalEventTypeCount = "/stasticReport/getStatisticsMedicalEventTypeCount";
        public static final String GetStatisticsDemographicsAgeCount = "/stasticReport/getStatisticsDemographicsAgeCount";
        public static final String GetArchiveReportInfo = "/stasticReport/getArchiveReportInfo";
        public static final String GetArchiveIdentifyReportInfo = "/stasticReport/getArchiveIdentifyReportInfo";
        public static final String GetArchiveHospitalReportInfo = "/stasticReport/getArchiveHospitalReportInfo";
        public static final String GetArchiveStatisticalReportInfo = "/stasticReport/getArchiveStatisticalReportInfo";
        public static final String GetArchiveTotalVisitReportInfo = "stasticReport/getArchiveTotalVisitReportInfo";

        public static final String getStatisticsUserCards = "/tj/getStatisticsUserCards";
        public static final String getStatisticsUserAgeByIdCardNo = "/tj/getStatisticsUserAgeByIdCardNo";
        public static final String getStatisticsDoctorByRoleType = "/tj/getStatisticsDoctorByRoleType";
        public static final String getStatisticsCityDoctorByRoleType = "/tj/getStatisticsCityDoctorByRoleType";
    }

    public static class Org {
        public static final String getUserOrglistByUserId="/org/getUserOrglistByUserId/";
        public static final String getUserOrgSaasByUserOrgCode="/org/getUserOrgSaasByUserOrgCode/";
        public static final String GetOrgDeptsDate="/org/getOrgDeptsDate";
        public static final String GetOrgDeptInfoList = "/org/userId/getOrgDeptInfoList";
        public static final String getseaOrgsByOrgCode="/organizations/seaOrgsByOrgCode";
    }

    public static class GetInfo {
        public static final String GetAppIdsByUserId = "/BasicInfo/getAppIdsByUserId";
        public static final String GetIdCardNoByOrgCode = "/BasicInfo/getIdCardNoByOrgCode";
    }

    public static class Government {
        public static final String SearchGovernmentMenu = "/government/searchGovernmentMenu";
        public static final String GovernmentMenuCheckName = "/government/checkName";
        public static final String GovernmentMenuCheckCode = "/government/checkCode";
        public static final String GovernmentMenuById = "/government/detailById";
        public static final String AddGovernmentMenu = "/government/save";
        public static final String UpdateGovernmentMenu = "/government/update";

        public static final String AddGovernmentBrowseLog = "/governmentBrowseLog/save";
        public static final String searchGovernmentBrowseLog = "/governmentBrowseLog/searchByUserId";

        public static final String GovernmentMenuReportMonitorTypeSave = "/governmentMenuReportMonitorType/save";
        public static final String GovernmentMenuReportMonitorTypeDelete = "/governmentMenuReportMonitorType/delete";
        public static final String MonitorTypeList = "/governmentMenuReportMonitorType/MonitorTypeList";

        public static final String GetReportByMenuId = "/governmentMenuReportMonitorType/getReportByMenuId";
    }

    public static class SystemDict {
        public static final String getDictEntryByDictId = "/systemDict/getDictEntryByDictId/{dictId}";
    }

    /**
     * 应急指挥中心
     */
    public static class Emergency {
        // 救护车
        public static final String Ambulance = "/ambulance/{id}";
        public static final String AmbulanceList = "/ambulance/list";
        public static final String AmbulanceSearch = "/ambulance/search";
        public static final String AmbulanceUpdateStatus = "/ambulance/updateStatus";
        public static final String AmbulanceSave = "/ambulance/save";
        public static final String AmbulanceUpdate = "/ambulance/update";
        public static final String AmbulanceDelete = "/ambulance/delete";
        public static final String AmbulanceIdOrPhoneExistence = "/ambulance/IdOrPhoneExistence";
        public static final String AmbulancesBatch = "/ambulances/batch";

        // 出勤记录
        public static final String AttendanceSave = "/attendance/save";
        public static final String AttendanceUpdate = "/attendance/update";
        public static final String AttendanceEdit = "/attendance/edit";
        public static final String AttendanceList = "/attendance/list";
        public static final String AttendanceDelete = "/attendance/delete";
        public static final String AttendanceDetail= "/attendance/detail";
        // 排班
        public static final String ScheduleList = "/schedule/list";
        public static final String ScheduleLevel = "/schedule/level";
        public static final String ScheduleSave = "/schedule/save";
        public static final String ScheduleUpdate = "/schedule/update";
        public static final String ScheduleBatch = "/schedules/batch";

        // 待命地点
        public static final String LocationList = "/location/list";
        public static final String LocationSave = "/location/save";
        public static final String LocationUpdate = "/location/update";
        public static final String LocationDelete = "/location/delete";
    }

}
