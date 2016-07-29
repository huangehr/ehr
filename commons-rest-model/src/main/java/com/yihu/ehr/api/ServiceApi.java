package com.yihu.ehr.api;

/**
 * 微服务REST API. 此处定义的URL可用于服务对外提供的地址及HTTP客户端请求地址.
 * URL定义遵循健康档案平台REST规范.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
public class ServiceApi {
    public static class Caches{
        public static final String Organizations                   = "/caches/organizations";
        public static final String OrganizationsArea                   = "/caches/OrganizationsArea";
        public static final String GetOrganizationsArea                   = "/caches/organizationsArea/{orgs_code}";
        public static final String Organization                    = "/caches/organizations/{org_code}";
        public static final String Versions                        = "/caches/standards/versions";
        public static final String Version                         = "/caches/standards/versions/{version}";
    }

    public static class Adaptions{
        public static final String Cache                          = "/adaptions/{id}/cache";
        public static final String CacheGet                       = "/adaptions/{key}/cache";

        public static final String Scheme = "/adaptions/schemas/{id}";
        public static final String Schemes = "/adaptions/schemas";

        public static final String SchemaMetadataList             ="/adaptions/schema/metadata";
        public static final String SchemaMetadata                 ="/adaptions/schema/metadata/{id}";
        public static final String SchemaMetadataBatch            ="/adaptions/schema/metadata/batch";


        public static final String RsAdapterDictionaries          ="/adaptions/adapter/dictionaries";
        public static final String RsAdapterDictionariesCache    ="/adaptions/adapter/dictionaries/{schemaId}/cache";
        public static final String RsAdapterDictionary            ="/adaptions/adapter/dictionaries/{id}";


    }

    /**
     * 资源管理中心接口
     */
    public static class Resources{
        //资源查询接口(命名先按原有路径，不规范)
        public static final String ResourceViewMetadata = "/rs/query/getResourceMetadata"; //资源浏览获取结构
        public static final String ResourceViewData = "/rs/query/getResourceData"; //资源浏览获取数据
        public static final String ResourcesQuery = "/resources/query"; //资源查询接口
        public static final String ResourcesQueryTransform = "/resources/query/transform"; //资源查询接口+转译
        public static final String ResourcesRawFiles = "/resources/query/raw_files"; //非结构资源查询接口
        public static final String ResourcesRawFilesList = "/resources/query/raw_files_list"; //非结构资源List查询接口
        public static final String ResourcesMasterData = "/resources/query/master_data"; //主表资源查询接口
        public static final String ResourcesSubData = "/resources/query/sub_data"; //细表资源查询接口
        public static final String getCDAData = "/resources/query/getCDAData";//获取cda data
        public static final String ResourcesMasterStat = "/resources/query/master_stat"; //主表资源统计接口
        public static final String ResourcesSubStat = "/resources/query/sub_stat"; //细表资源查询接口
        public static final String ResourcesMysql = "/resources/query/mysql"; //mysql资源查询接口


        public static final String Resources                         = "/resources";
        public static final String Resource                          = "/resources/{id}";
        public static final String NoPageResources                   = "/NoPageResources";

        public static final String ResourceMetadataList              = "/resources/rs_metadata";
        public static final String ResourceMetadataBatch             = "/resources/rs_metadata/batch";
        public static final String ResourceMetadataBatchByResourceId = "/resources/rs_metadata/resource_ids/batch";
        public static final String ResourceMetadata                  = "/resources/rs_metadata/{id}";

        public static final String ResourceGrant                 = "/resources/grants/{id}";
        public static final String ResourceGrants                = "/resources/grants";
        public static final String ResourceGrantsNoPage          = "/resources/grants/no_paging";
        public static final String AppsGrantResources            = "/resources/apps/{appId}/grant";
        public static final String ResourceGrantApps             = "/resources/{resourceId}/grant";
        public static final String ResourceApps                  = "/resources/{resource_id}/app";

        public static final String ResourceMetadatasValid      = "/resources/metadatas/valid";
        public static final String ResourceMetadataGrants       = "/resources/metadata/grants";
        public static final String ResourceMetadataGrant        = "/resources/metadata/grants/{id}";
        public static final String ResourceMetadataGrantApp     = "/resources/app_resource/{appResourceId}/grant/metadata/{metadataId}";
        public static final String ResourceMetadataListGrantApp = "/resources/metadata/grant/{appResourceId}";
        public static final String ResourceAppMetadataGrants       = "/resources/app_resource/{app_res_id}/metadata";
        public static final String ResourceAppMetadataGrant       = "/resources/app_resource/metadata/grant";
        public static final String ResourceAppMetadataGrantExistence   = "/resources/app_resource/metadata/grant/existence";

        public static final String MetadataList                 ="/resources/metadata";
        public static final String MetadataBatch                ="/resources/metadata/batch";
        public static final String Metadata                     ="/resources/metadata/{id}";
        public static final String MetadataCache                ="/resources/metadata/cache";
        public static final String MetadataExistence            ="/resources/metadata/existence";
        public static final String MetadataStdCodeExistence    ="/resources/metadata/std_code/existence";
        public static final String MetadataIdExistence           ="/resources/metadata/id/existence";

        public static final String DictList                     ="/resources/dict";
        public static final String DictBatch                    ="/resources/dict/batch";
        public static final String Dict                         ="/resources/dict/{id}";
        public static final String DictCode                         ="/resources/dict/code";
        public static final String DictExistence                ="/resources/dict/existence";
        public static final String DictEntryBatch               ="/resources/dict/entry/batch";
        public static final String DictCodesExistence            ="/resources/dict/codes/existence";

        public static final String Params                       ="/resources/params";
        public static final String ParamsNoPage                 ="/resources/params/no_paging";
        public static final String Param                        ="/resources/param";
        public static final String ParamById                    ="/resources/param/{id}";
        public static final String ParamKeyValueExistence       ="/resources/param/key_value_existence";


        public static final String DictEntries                   ="/resources/dict_entries";
        public static final String NoPageDictEntries             ="/resources/noPage_dict_entries";
        public static final String DictEntry                     ="/resources/dict_entries/{id}";
        public static final String DictEntriesByDictCode       ="/resources/dict/code/dict_entries";
        public static final String DictEntriesExistence          ="/resources/dict_entries/existence";

        public static final String SystemDictList               ="/resources/system_dict";
        public static final String SystemDict                   ="/resources/system_dict/{id}";

        public static final String SystemDictEntries            ="/resources/system_dict_entries";
        public static final String SystemDictEntry              ="/resources/system_dict_entries/{id}";

        public static final String Interfaces                       ="/resources/interfaces";
        public static final String InterfaceById                       ="/resources/interfaces/{id}";
        public static final String Interface                       ="/resources/interface";
        public static final String InterfaceNameExistence          ="/resources/existence/name";

        public static final String Categories                       ="/resources/categories/no_paging";
        public static final String NoPageCategories                 ="/resources/categories";
        public static final String Category                         ="/resources/categories/{id}";
        public static final String CategoryByPid                    ="/resources/categories/pid";
        public static final String CategoryExitSelfAndChild        ="/resources/categories/parent";

        public static final String AdapterDicts                         ="/resources/adapter/dict";
        public static final String AdapterDictsBatch                    ="/resources/adapter/dict/batch";
        public static final String AdapterDict                          ="/resources/adapter/dict/{id}";
    }

    /**
     * 档案查询接口
     */
    public static class Profiles{
        public static final String ProfileLucene       = "/profile/lucene"; //全文检索
        public static final String ProfileInfo         = "/{demographic_id}/profile/info"; //基本信息
        public static final String ProfileHistory      = "/{demographic_id}/profile/history"; //患病史
        public static final String HealthProblem       = "/{demographic_id}/profile/health_problem"; //主要健康问题
        public static final String MedicalDisease      = "/{demographic_id}/profile/medical_disease"; //就诊过疾病
        public static final String MedicalArea         = "/{demographic_id}/profile/medical_area"; //就诊过区域
        public static final String MedicalYear         = "/{demographic_id}/profile/medical_year"; //就诊过年份
        public static final String MedicalEvents       = "/{demographic_id}/profile/medical_events"; //门诊/住院事件(时间轴)
        public static final String MedicationStat      = "/{demographic_id}/profile/medication_stat"; //患者用药清单
        public static final String MedicationUsed      = "/{demographic_id}/profile/medication_used"; //患者常用药（按次数）
        public static final String MedicalEvent        = "/{event_no}/info"; //某次住院/门诊事件信息


        public static final String MedicationMaster          = "/profile/medication/master"; //处方主表
        public static final String MedicationPrescription    = "/profile/medication/prescription"; //处方笺
        public static final String MedicationDetail          = "/profile/medication/detail/{prescription_no}"; //处方细表
        public static final String MedicationDetailChinese   = "/profile/medication/detail/chinese"; //中药处方
        public static final String MedicationDetailWestern   = "/profile/medication/detail/western"; //西药处方


        public static final String OutpatientDiagnosis = "/{demographic_id}/outpatient/diagnosis"; //门诊诊断
        public static final String OutpatientSymptom   = "/{demographic_id}/outpatient/symptom"; //门诊症状
        public static final String OutpatientCostMaster= "/{demographic_id}/outpatient/cost/master"; //门诊费用汇总
        public static final String OutpatientCostDetail= "/{demographic_id}/outpatient/cost/detail"; //门诊费用明细


        public static final String HospitalizedDiagnosis       = "/{demographic_id}/hospitalized/diagnosis"; //住院诊断
        public static final String HospitalizedSymptom         = "/{demographic_id}/hospitalized/symptom"; //住院症状
        public static final String HospitalizedCostMaster      = "/{demographic_id}/hospitalized/cost/master"; //住院费用汇总
        public static final String HospitalizedCostDetail      = "/{demographic_id}/hospitalized/cost/detail"; //住院费用明细
        public static final String HospitalizedOrdersTemporary = "/{demographic_id}/hospitalized/orders/temporary"; //住院临时医嘱
        public static final String HospitalizedOrdersLongtime  = "/{demographic_id}/hospitalized/orders/longtime"; //住院长期医嘱
        public static final String HospitalizedDeath           = "/{demographic_id}/hospitalized/death"; //住院死因链情况


        public static final String CDADocumentId               = "/profile/cda/document_id"; //cda模板（通过event_no + cda_type）
        public static final String CDAClass                    = "/{profile_id}/cda_class"; //cda分类
        public static final String CDAData                     = "/{profile_id}/cda_data"; //cda数据
        public static final String CDADocument                 =  "/{profile_id}/cda_document"; //完整CDA文档


        public static final String ExaminationReport           = "/{demographic_id}/examination"; //检查报告单
        public static final String ExaminationImg              = "/{demographic_id}/examination/img"; //检查报告单图片


        public static final String LaboratoryReport            = "/{demographic_id}/laboratory"; //检验报告单
        public static final String LaboratoryImg               = "/{demographic_id}/laboratory/img"; //检验报告单图片
        public static final String LaboratoryProject           = "/{demographic_id}/laboratory/project"; //检验报告单项目
        public static final String LaboratoryAllergy           = "/{demographic_id}/laboratory/allergy"; //检验药敏

        public static final String Surgery                     = "/{demographic_id}/surgery";//手术-手术记录

        public static final String IndicatorsClass             = "/{demographic_id}/indicators/class"; //获取某个健康问题指标
        public static final String IndicatorsData              = "/{demographic_id}/indicators/data"; //获取指标数据
    }

    public static class Families{
        public static final String Families                        ="/families";
        public static final String Family                          ="/families/{id}";

        public static final String FamiliesMembers                ="/families/members";
        public static final String FamilyMembers                 ="/families/{family_id}/members";
        public static final String FamilyMember                ="/families/{family_id}/members/{id_card_no}";
    }

    public static class Cipher{
        public static final String Encryption                   ="/cipher/encryption/{type}";
        public static final String Decryption                   ="/cipher/decryption/{type}";
    }

    public static class Apps{
        public static final String Apps                            = "/apps";
        public static final String AppsNoPage                      = "/apps/no_paging";
        public static final String App                             = "/apps/{app_id}";
        public static final String AppExistence                    = "/apps/{app_id}/existence";
        public static final String AppStatus                       = "/apps/{app_id}/status";
        public static final String AppNameExistence                = "/app/{app_name}/existence";
        public static final String FilterList                        ="/apps/filterList";
    }

    public static class AppApi{
        public static final String AppApis                            = "/appApi";
        public static final String AppApi                             = "/appApi/{id}";
        public static final String AppApisNoPage                        = "/appApiNoPage";
    }
    public static class AppFeature{
        public static final String AppFeatures                           = "/appFeature";
        public static final String FilterFeatureList                    = "/filterFeatureList";
        public static final String FilterFeatureNoPage                    = "/filterFeatureNoPage";
        public static final String AppFeature                            = "/appFeature/{id}";
    }
    public static class AppApiParameter{
        public static final String AppApiParameters                            = "/appApiParameter";
        public static final String AppApiParameter                     = "/appApiParameter/{id}";
    }
    public static class AppApiResponse{
        public static final String AppApiResponses                            = "/appApiResponse";
        public static final String AppApiResponse                    = "/appApiResponse/{id}";
    }

    public static class Dictionaries{

    }

    public static class ESB{
        public static final String SearchHosLogs                            = "/searchHosLogs";

        public static final String DeleteHosLogs                            = "/deleteHosLogs";
    }

    public static class Geography{

    }

    public static class Organizations{

    }

    public static class Packages{
        public static final String Packages                         = "/packages";
        public static final String Package                          = "/packages/{id}";
        public static final String PackageSearch                    = "/packages/search";
        public static final String PackageDownloads                 = "/packages/{id}/downloads";

        public static final String ResolveMessage                   = "/message/resolve";

        public static final String MessageTimer                     = "/message/timer";

        public static final String LegacyPackages                   = "/json_package";
    }

    public static class PackageResolve{
        public static final String Scheduler                        = "/scheduler";
    }

    public static class Patients{

        public static final String ArApplications                    = "/archive/applications";
        public static final String ArApplication                    = "/archive/applications/{id}";

        public static final String ArRelations                    = "/archive/relations";
        public static final String ArRelation                    = "/archive/relation/{id}";
        public static final String ArRelationsExistence          = "/archive/relations/existence";

        public static final String Authentications                    = "/patient/authentications";
        public static final String Authentication                    = "/patient/Authentication/{id}";
    }

    public static class Securities{
        public static final String UserKey                          = "/securities/users/{user_id}/key";
        public static final String UserKeyId                        = "/securities/users/{user_id}/key/id";
        public static final String UserPublicKey                    = "/securities/users/{user_id}/key/public";
        public static final String UserTokens                       = "/securities/users/{user_id}/tokens";
        public static final String UserToken                        = "/securities/users/{user_id}/tokens/{token_id}";
        public static final String OrganizationKey                  = "/securities/organizations/{org_code}/key";
        public static final String OrganizationPublicKey            = "/securities/organizations/{org_code}/key/public";
        public static final String ClientTokens                     = "/securities/clients/{client_id}/tokens";

        public static final String Keys                             = "/securities/keys/{id}";
        public static final String deleteOrgKey                     = "/securities/org/keys/{org_code}";
        public static final String deleteUserKey                    = "/securities/user/keys/{user_id}";
        public static final String Tokens                           = "/securities/tokens/{id}";
    }

    public static class SpecialDictionaries{

    }

    public static class Standards{
        public static final String Sources                          = "/std/sources";
        public static final String NoPageSources                    = "/std/sources/no_paging";
        public static final String Source                           = "/std/sources/{id}";
        public static final String IsSourceCodeExist                ="/std/sources/code_is_exist";

        public static final String NoPageTypes                      = "/std/types/no_paging";
        public static final String Types                            = "/std/types";

        public static final String Type                             = "/std/types/{id}";
        public static final String TypesCodeExistence               = "/std/type/code/existence";
        public static final String TypeChildren                     = "/std/types/parent_id/childrens";
        public static final String TypesChildren                    = "/std/types/childrens";
        public static final String TypeList                         = "/cda_types/code_name";
        public static final String TypeParent                       = "/std/types/parent";
        public static final String TypeOther                        = "/std/types/{id}/other";

        public static final String Versions                         = "/std/versions";
        public static final String Version                          = "/std/versions/{version}";
        public static final String VersionInStageExist              = "/std/versions/in_stage/existence";
        public static final String VersionCache                     = "/std/versions/{version}/cache";
        public static final String VersionNameExistence             = "/std/version/name/existence";
        public static final String VersionBackStage                 = "/std/versions/{version}/back";
        public static final String VersionCommit                    = "/std/versions/{version}/commit";
        public static final String VersionRevert                    = "/std/versions/{version}/revert";
        public static final String VersionLatest                    = "/std/versions/latest";
        public static final String VersionLatestExistence           = "/std/versions/{version}/latest/existence";

        public static final String Documents                        = "/std/documents";
        public static final String Document                         = "/std/documents/{id}";
        public static final String DocumentList                        = "/std/documentsList";
        public static final String DocumentFileExistence            = "/std/documents/{id}/file/existence";
        public static final String DocumentCreateFile               = "/std/documents/{id}/file/create";
        public static final String DocumentGetFile                  = "/std/documents/{id}/file";

        public static final String DocumentDataSet                  = "/std/documents/data_set";
        public static final String DocumentDataSetList                  = "/std/documents/data_set_list";
        public static final String DataSetRelationships             = "/std/documents/data_set_relationships";
        public static final String DataSetRelationship              = "/std/documents/data_set_relationships/{id}";

        public static final String Dictionaries                     = "/std/dictionaries";
        public static final String NoPageDictionaries               = "/std/dictionaries/no_paging";
        public static final String Dictionary                       = "/std/dictionaries/{id}";
        public static final String DictionaryCode                    = "/std/dictionaries/code";
        public static final String MetaDataWithDict                 = "/std/data_set/{data_set_id}/meta_datas/{meta_data_id}/dictionaries";
        public static final String DictCodeIsExist                  = "/std/dictionaries/is_exist/code";
        public static final String DictOther                        = "/std/dictionaries/{id}/other";
        public static final String DictParent                        = "/std/dictionaries/{id}/parent";
        public static final String DictCodesExistence               =  "/dict/codes/existence";
        public static final String DictEntryBatch                    ="/dict/entry/batch";

        public static final String Entry                            = "/std/dictionaries/entries/{id}";
        public static final String Entries                          = "/std/dictionaries/entries";
        public static final String EntriesWithDictionary            = "/std/dictionary/{dict_id}/entries";
        public static final String EntryCodeIsExist                 = "/std/dictionaries/entries/is_exist/code";

        public static final String DataSets                         = "/std/data_sets";
        public static final String NoPageDataSets                   = "/std/data_sets/no_paging";
        public static final String DataSet                          = "/std/data_sets/{id}";
        public static final String DataSetsName                     = "/std/data_sets/name";
        public static final String DataSetCodeIsExist               = "/std/data_set/is_exist/code";
        public static final String DataSetsIds                      = "/std/{ids}data_sets";
        public static final String DataSetsBatch                   = "/std/data_set/bacth";

        public static final String MetaDatas                        = "/std/data_sets/meta_datas";
        public static final String MetaData                         = "/std/data_sets/meta_datas/{id}";
        public static final String MetaDataCodeExistence            = "/std/data_sets/{data_set_id}/meta_data/inner_code/existence";
        public static final String MetaDataNameExistence            = "/std/data_sets/{data_set_id}/meta_data/name/existence";
        public static final String MetaDatasName                    = "/std/data_sets/meta_datas/name";
        public static final String MetaDatasWithDataSet             = "/std/data_sets/{data_set_id}/meta_datas";

        public static final String DispatchLogs                     = "/std/dispatches/logs";

        public static final String Dispatch                         = "/std/dispatches/{id}";
        public static final String Dispatches                       = "/std/dispatches";
    }

    public static class Users{
        public static final String Users                            = "/users";
        public static final String User                             = "/users/{user_name}";
        public static final String UserExistence                    = "/users/{user_name}/existence";
        public static final String UserVerification                 = "/users/verification";
        public static final String UserAdmin                        = "/users/admin/{user_id}";
        public static final String UserAdminPassword                = "/users/admin/{user_id}/password";
        public static final String UserAdminPasswordReset           = "/users/admin/{user_id}/password_reset";
        public static final String UserAdminKey                     = "/users/admin/{user_id}/key";
        public static final String UserAdminContact                 = "/users/admin/{user_id}/contact";
        public static final String UserIdCardNoExistence            = "/user/id_card_no/existence";
        public static final String UserEmailNoExistence             = "/user/email/existence";
    }

    public static class Roles{
        public static final String Role                             = "/roles/role";
        public static final String RoleId                           = "/roles/role/{id}";
        public static final String Roles                            = "/roles/roles";
        public static final String RolesNoPage                      = "/roles/roles/no_paging";
        public static final String RoleNameExistence                = "/roles/name/existence";
        public static final String RoleCodeExistence                = "/roles/code/existence";


        public static final String RoleUser                         = "/roles/role_user";
        public static final String RoleUserByUserId                 = "/roles/role_user/{user_id}";
        public static final String RoleUsers                        = "/roles/role_users";
        public static final String RoleUsersNoPage                   = "/roles/role_users/no_paging";

        public static final String RoleFeature                      = "/roles/role_feature";
        public static final String RoleFeatureId                    = "/roles/role_feature/{id}";
        public static final String RoleFeatureByRoleId              = "/roles/role_feature/role_id";
        public static final String RoleFeatures                     = "/roles/role_features";
        public static final String RoleFeaturesNoPage               = "/roles/role_features/no_paging";
        public static final String RoleFeatureExistence             = "/roles/role_feature/existence";

        public static final String RoleApp                          = "/roles/role_app";
        public static final String RoleAppId                        = "/roles/role_app/{id}";
        public static final String RoleApps                         = "/roles/role_apps";
        public static final String RoleAppsNoPage                   = "/roles/role_apps/no_paging";

        public static final String RoleApi                          = "/roles/role_api";
        public static final String RoleApiId                        = "/roles/role_api/{id}";
        public static final String RoleApiByRoleId                  = "/roles/role_api/role_id";
        public static final String RoleApis                         = "/roles/role_apis";
        public static final String RoleApisNoPage                   = "/roles/role_apis/no_paging";
        public static final String RoleApisExistence                = "/roles/role_api/existence";

    }

    public static class Redis{
        public static final String StandardVersions                 = "/std/versions";
        public static final String StandardDataSetsCount            = "/std/data_sets/count";
        public static final String StandardMetaDatasCount           = "/std/meta_datas/count";
        public static final String StandardDictionariesCount        = "/std/dictionaries/count";
        public static final String StandardDictionaryEntriesCount   = "/std/dictionary/{id}/entries/count";

        public static final String Organizations                    = "/organizations";
        public static final String Organization                     = "/organizations/{id}";

        public static final String DemographicIdToProfileIdCache    = "/Redis/DemographicIdToProfileId";
        public static final String GetDemographicIdToProfileIdCache = "/Redis/GetDemographicIdToProfileIdCache/{demographic_id}";
        public static final String TreatmentAreaCache               = "/Redis/TreatmentAreaCache";
        public static final String GetTreatmentAreaCache            = "/Redis/GetTreatmentAreaCache/{demographic_id}";
    }

    public static class HealthProfile{
        public static final String Profiles                         = "/health_profiles";
        public static final String ProfileSearch                    = "/health_profiles/search";
        public static final String Profile                          = "/health_profiles/{profile_id}";
        public static final String ProfileDocument                  = "/health_profiles/{profile_id}/documents/{document_id}";

        public static final String Diagnosis                        = "/health_profile/disease/diagnosis";
        public static final String MajorIssues                      = "/health_profile/disease/major_issues";

        public static final String Prescriptions                    = "/health_profile/prescriptions";
        public static final String Drugs                            = "/health_profile/drugs";
        public static final String Lis                              = "/health_profile/lis";
        public static final String PhysicalExam                     = "/health_profile/physical_examinations";
        public static final String PastHistories                    = "/health_profile/past_histories";
    }

    public static class ProfileTemplate{
        public static final String Templates                        = "/templates";
        public static final String TemplatesDownloads               = "/templates/downloads";
        public static final String Template                         = "/templates/{id}";
        public static final String TemplateCtn                      = "/templates/{id}/content";
        public static final String TemplateTitleExistence           = "/template/title/existence";
    }

    public static class SanofiSupport{
        public static final String PhysicSigns                      = "/sanofi/physic_signs";
    }

    public static class ArchiveSecurity
    {
        public static final String ArchivePrivate                   = "/archiveprivate/{userId}";
        public static final String ArchivePrivateRowKey            = "/archiveprivate/{userId}/{rowKey}";

        public static final String ArchiveSecuritySetting             = "/archivesecurity";
        public static final String ArchiveSecuritySettingUser           = "/archivesecurity/{user_id}";
        public static final String ArchiveSecuritySettingKeyAuthen         = "/archivesecurity/{user_id}/securitykey/authentication";

        public static final String ArchiveLogs             = "/archivelogs";

        public static final String AuthorizeApps             = "/authorizeapps";
        public static final String AuthorizeAppsId             = "/authorizeapps/{id}";

        public static final String AuthorizeAppsSubjects            = "/authorizeapps/subjects";
        public static final String AuthorizeAppsSubjectsId            = "/authorizeapps/subjects/{id}";

        public static final String AuthorizeSubjects            = "/authorizesubjects";

        public static final String AuthorizeSubjectsResources            = "/authorizesubjects/{subjectId}/resources";

        public static final String AuthorizeDoctors           = "/authorizedoctors";
        public static final String AuthorizeDoctorsId           = "/authorizedoctors/{id}";
        public static final String AuthorizeDoctorsIdAlteration           = "/authorizedoctors/{id}/alteration";
        public static final String AuthorizeDoctorsIdAuthorization           = "/authorizedoctors/{id}/authorization";

        public static final String MessageReplyTempates   = "/messagereplytemplates";

        public static final String MessageTempates   = "/messagetemplates";
        public static final String MessageTempatesCode   = "/messagetemplates/{messageTempCode}";

        public static final String MessageSend   = "/messagesend";
        public static final String MessageSendId   = "/messagesend/{id}";

        public static final String MessageReply   = "/messagereply";
    }

    public static class MedicalRecords{

        //病历
        public static final String MedicalRecordByLastOne                 = "/medicalRecords/getRecordByLastOne";
        public static final String MedicalRecord                          = "/medicalRecords/medicalRecord";
        public static final String MedicalRecordId                        = "/medicalRecords/{id}/record";
        public static final String MedicalRecordDocId                     = "/medicalRecords/{doctor_id}/record";
        public static final String MedicalRecordPatId                     = "/medicalRecords/{patient_id}/record";
        public static final String MedicalRecordCopy                      = "/medicalRecords/medicalRecordCopy";

        //病历报告
        public static final String MedicalReport                          = "/medicalRecords/{record_id}/report";
        public static final String DeleteMedicalReport                    = "/medicalRecords/medicalReport/{id}";
        public static final String MedicalReportImg                       = "/medicalRecords/{report_id}/reportImg";
        public static final String MedicalReportImgPicture                = "/medicalRecords/medicalReportImgPicture";

        //医生模板
        public static final String DoctorTemplate                         = "/medicalRecords/doctor/{doctor_id}/template";
        public static final String DcotorTemplateManage                   = "/medicalRecords/doctor/template";

        //文本草稿
        public static final String DoctorDraft                            = "/medicalRecords/{doctor_id}/doctorDraft";
        public static final String CheckDoctorDraft                       = "/medicalRecords/checkDoctorDraft";

        //对话草稿
        public static final String MedicalDraft                           = "/medicalRecords/{doctor_id}/medicalDraft";
        public static final String CheckMedicalDraft                      = "/medicalRecords/checkMedicalDraft";
        public static final String MedicalDraftPicture                    = "/medicalRecords/medicalDraftPicture";

        //医生病历关联
        public static final String DoctorMedicalRecords                   = "/medicalRecords/{doctor_id}/relation/{record_id}";
        public static final String DoctorMedicalRecord                    = "/medicalRecords/DoctorMedicalRecord/{record_id}";

        //医生信息
        public static final String AddDoctor                              = "/medicalRecords/doctor";
        public static final String DoctorInfo                             = "/medicalRecords/doctor/{doctor_id}";
        public static final String DcotorInfoManage                       = "/medicalRecords/doctor/info";
        public static final String DoctorRecords                          = "/medicalRecords/doctor/{doctor_id}/records";
        public static final String ChangeDocStatus                        = "/medicalRecords/changeDocStatus";
        public static final String PatientDiagnosis                       = "/medicalRecords/PatientDiagnosis";
        public static final String DcotorICD10                            = "/medicalRecords/doctor/{doctor_id}/icd10";
        public static final String MedicalDrug                            = "/medicalRecords/MedicalDrug/{records_id}";

        //标签
        public static final String MedicalLabels                          = "/medicalRecords/{record_id}/labels";
        public static final String DcotorLabelClassManage                 = "/medicalRecords/doctor/labelClass";
        public static final String DoctorLabelClass                       = "/medicalRecords/doctor/{doctor_id}/labelClass";
        public static final String DcotorLabelManage                      = "/medicalRecords/doctor/label";
        public static final String DoctorLabel                            = "/medicalRecords/doctor/{doctor_id}/label";
        public static final String DoctorLabelUsed                        = " /medicalRecords/doctor/{doctor_id}/labelUsed";
        public static final String getRecordIdByLabels                     ="/medicalRecords/getRecordIdByLabels";
        //诊断字典
        public static final String DiagnosisDict                          = "/medicalRecords/diagnosisDict";

        //系统字典
        public static final String SystemDict                             = "/medicalRecords/systemDict";
        public static final String SystemDictEntry                        = "/medicalRecords/systemDictEntry";

        //病人信息
        public static final String PatientInfoManage                      = "/medicalRecords/patient/info";
        public static final String PatientInfo                            = "/medicalRecords/patient/{patient_id}/info";
        public static final String PatientICD10                           = "/medicalRecords/patient/{patient_id}/icd10";
        public static final String PatientRecords                         = "/medicalRecords/patient/{patient_id}/records";
        public static final String SearchPatient                          = "/medicalRecords/SearchPatient";
        public static final String PatientExistence                       = "/medicalRecords/{app_uid}/existence/{app_patient_id}";

        //文件操作
        public static final String imgFile                                = "/medicalRecords/{id}/imgFile";
        public static final String file                                   = "/medicalRecords/{id}/file";
    }

    public static class DailyStatistics{
        //日常监测
        public static final String  StatisticsProfile                        = "/statistics/profiles";
        public static final String  StatisticsOutpatientHospital            = "/statistics/outpatientHospital";
        public static final String  StatisticsDailyReport                    = "/statistics/dailyReport";
        public static final String  StatisticsDailyReportFiles                    = "/statistics/dailyReportFiles";

        //住院
        public static final String  StatisticsHospitalization                    = "/statistics/hospitalization/{orgCode}";
        public static final String  StatisticsHospitalizationDept                    = "/statistics/hospitalization/{orgCode}/dept";
        public static final String  StatisticsHospitalizationSex                    = "/statistics/hospitalization/{orgCode}/sex";
        public static final String  StatisticsHospitalizationDisease                    = "/statistics/hospitalization/{orgCode}/disease";

        //门诊
        public static final String  StatisticsOutpatient                    = "/statistics/outpatient/{orgCode}";
        public static final String  StatisticsOutpatientDept                    = "/statistics/outpatient/{orgCode}/dept";
        public static final String  StatisticsOutpatientSex                    = "/statistics/outpatient/{orgCode}/sex";

        //入库统计
        public static final String  StatisticsProfileCreateDate                    = "/statistics/profiles/createDate";
        public static final String  StatisticsProfileEventDate                      = "/statistics/profiles/eventDate";
        public static final String  StatisticsProfileIdNotNull                       = "/statistics/profiles/idNotNull";
        public static final String  StatisticsProfileEventDateGroup                   = "/statistics/profiles/eventDateGroup";


        //素材
        public static final String DoctorText             = "/medicalRecords/doctor/{doctor_id}/text";
        public static final String DoctorImg              = "/medicalRecords/doctor/{doctor_id}/img";

    }
}
