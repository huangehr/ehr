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
    public static class Adaptions{
    }

    public static class Apps{
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
        public static final String Packages                 = "/packages";
        public static final String Package                  = "/packages/{id}";
        public static final String PackageDownloads         = "/packages/{id}/downloads";
    }

    public static class Patients{

    }

    public static class Securities{
        public static final String UserKey                  = "/securities/users/{user_id}/key";
        public static final String UserKeyId                = "/securities/users/{user_id}/key/id";
        public static final String UserPublicKey            = "/securities/users/{user_id}/key/public";
        public static final String UserTokens               = "/securities/users/{user_id}/tokens";
        public static final String UserToken                = "/securities/users/{user_id}/tokens/{token_id}";
        public static final String OrganizationKey          = "/securities/organizations/{org_code}/key";
        public static final String OrganizationPublicKey    = "/securities/organizations/{org_code}/key/public";

        public static final String Keys                     = "/securities/keys/{id}";
        public static final String Tokens                   = "/securities/tokens/{id}";
    }

    public static class SpecialDictionaries{

    }

    public static class Standards{

    }

    public static class Users{
        public static final String Users                    = "/users";
        public static final String User                     = "/users/{user_name}";
        public static final String UserExistence            = "/users/{user_name}/existence";
        public static final String UserPassword             = "/users/{user_name}/{password}";
        public static final String UserAdmin                = "/users/admin/{user_id}";
        public static final String UserAdminPassword        = "/users/admin/{user_id}/password";
        public static final String UserAdminKey             = "/users/admin/{user_id}/key";
        public static final String UserAdminContact         = "/users/admin/{user_id}/contact";
        public static final String UserIdCardNoExistence    = "/user/id_card_no/existence";
    }
}
