package com.yihu.ehr.api;

/**
 * REST API URL. 此处定义的URL可用于服务对外提供的地址及HTTP客户端请求地址.
 * URL定义遵循健康档案平台REST规范.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
public class RestApi {
    public static class Caches{
        public static final String Organizations                   = "/caches/organizations";
        public static final String Organization                    = "/caches/organizations/{org_code}";
        public static final String Versions                        = "/caches/standards/versions";
        public static final String Version                         = "/caches/standards/versions/{version}";
    }

    public static class Adaptions{
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

    }

    public static class Geography{

    }

    public static class Organizations{

    }

    public static class Packages{
        public static final String Packages                         = "/packages";
        public static final String Package                          = "/packages/{id}";
        public static final String PackageDownloads                 = "/packages/{id}/downloads";

        public static final String LegacyPackages                   = "/json_package";
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
        public static final String UserAdminPasswordReset          = "/users/admin/{user_id}/password_reset";
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
}
