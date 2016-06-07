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
        public static final String RsAdapterDictionary            ="/adaptions/adapter/dictionaries/{id}";


    }

    public static class Resources{
        public static final String Resources                         = "/resources";
        public static final String Resource                          = "/resources/{id}";

        public static final String ResourceMetadataList              = "/resources/rs_metadata";
        public static final String ResourceMetadataBatch             = "/resources/rs_metadata/batch";
        public static final String ResourceMetadataBatchByResourceId = "/resources/rs_metadata/resource_ids/batch";
        public static final String ResourceMetadata                  = "/resources/rs_metadata/{id}";

        public static final String ResourceGrant                 = "/resources/grants/{id}";
        public static final String ResourceGrants                = "/resources/grants";
        public static final String AppsGrantResources            = "/resources/apps/{appId}/grant";
        public static final String ResourceGrantApps             = "/resources/{resourceId}/grant";
        public static final String ResourceApps                  = "/resources/{resource_id}/app";

        public static final String ResourceMetadatasValid      = "/resources/metadatas/valid";
        public static final String ResourceMetadataGrants       = "/resources/metadata/grants";
        public static final String ResourceMetadataGrant        = "/resources/metadata/grants/{id}";
        public static final String ResourceMetadataGrantApp     = "/resources/app_resource/{appResourceId}/grant/metadata/{metadataId}";
        public static final String ResourceMetadataListGrantApp = "/resources/metadata/grant/{appResourceId}";

        public static final String MetadataList                 ="/resources/metadata";
        public static final String MetadataBatch                ="/resources/metadata/batch";
        public static final String Metadata                     ="/resources/metadata/{id}";
        public static final String MetadataExistence            ="/resources/metadata/existence";

        public static final String DictList                     ="/resources/dict";
        public static final String DictBatch                    ="/resources/dict/batch";
        public static final String Dict                         ="/resources/dict/{id}";
        public static final String DictExistence                ="/resources/dict/existence";

        public static final String Params                       ="/resources/param";
        public static final String Param                        ="/resources/param/{id}";


        public static final String DictEntries                   ="/resources/dict_entries";
        public static final String DictEntry                     ="/resources/dict_entries/{id}";
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
        public static final String App                             = "/apps/{app_id}";
        public static final String AppExistence                    = "/apps/{app_id}/existence";
        public static final String AppStatus                       = "/apps/{app_id}/status";
        public static final String AppNameExistence                = "/app/{app_name}/existence";
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
        public static final String DocumentFileExistence            = "/std/documents/{id}/file/existence";
        public static final String DocumentCreateFile               = "/std/documents/{id}/file/create";
        public static final String DocumentGetFile                  = "/std/documents/{id}/file";

        public static final String DataSetRelationships             = "/std/documents/data_set_relationships";
        public static final String DataSetRelationship              = "/std/documents/data_set_relationships/{id}";

        public static final String Dictionaries                     = "/std/dictionaries";
        public static final String NoPageDictionaries               = "/std/dictionaries/no_paging";
        public static final String Dictionary                       = "/std/dictionaries/{id}";
        public static final String MetaDataWithDict                 = "/std/data_set/{data_set_id}/meta_datas/{meta_data_id}/dictionaries";
        public static final String DictCodeIsExist                  = "/std/dictionaries/is_exist/code";
        public static final String DictOther                        = "/std/dictionaries/{id}/other";
        public static final String DictParent                        = "/std/dictionaries/{id}/parent";

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

    public static class Redis{
        public static final String StandardVersions                 = "/std/versions";
        public static final String StandardDataSetsCount            = "/std/data_sets/count";
        public static final String StandardMetaDatasCount           = "/std/meta_datas/count";
        public static final String StandardDictionariesCount        = "/std/dictionaries/count";
        public static final String StandardDictionaryEntriesCount   = "/std/dictionary/{id}/entries/count";

        public static final String Organizations                    = "/organizations";
        public static final String Organization                     = "/organizations/{id}";
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
}
