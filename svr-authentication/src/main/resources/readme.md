授权码模式 authorization_code
1.GET http://localhost:10260/oauth/authorize?response_type=code&client_id=uzs5G0HgTp&state=sxy&scope=read&redirect_uri=http://192.168.1.221:8010/ehr/browser/common/login/signin&pk=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjgKCe2zFmYSjHZz93cO17A0Hj6H5XdfwP2I5Qu1UJbmhGG/wPu409r9TABXblcxC9uPVR3PJ5dPrWPLuQ/r7tq16vSa5GF4iCcSlLyx/IDA5bq8ZnafS/RjPiKDtdZAx5uCNLog9GkVHNZIhK9cS9MI4QNuOJzOXaAVwnP2wIDAQAB

    HTTP/1.1 302 Found
    Location: http://localhost:9000/info?code=Ar0mp5&state=sxy
2.POST  http://localhost:10260/oauth/accessToken
    grant_type=authorization_code&client_id=uzs5G0HgTp&code=Ar0mp5&redirect_uri=http://192.168.1.221:8010/ehr/browser/common/login/signin

    HTTP/1.1 200 OK
    Content-Type: application/json;charset=UTF-8
    Cache-Control: no-store
    Pragma: no-cache
    {
      "accessToken":"4c5c7f4f-158a-4f73-9c14-ecbe78671872",
      "tokenType":"bearer",
      "expiresIn":3600,
      "refreshToken":"4c5c7f4f-158a-4f73-9c14-ecbe78671873",
      "state":"example_value"
    }

简化模式 implicit
1.GET http://localhost:10260/oauth/authorize?response_type=token&client_id=uzs5G0HgTp&state=sxy&scope=read&redirect_uri=http://192.168.1.221:8010/ehr/browser/common/login/signin&pk=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjgKCe2zFmYSjHZz93cO17A0Hj6H5XdfwP2I5Qu1UJbmhGG/wPu409r9TABXblcxC9uPVR3PJ5dPrWPLuQ/r7tq16vSa5GF4iCcSlLyx/IDA5bq8ZnafS/RjPiKDtdZAx5uCNLog9GkVHNZIhK9cS9MI4QNuOJzOXaAVwnP2wIDAQAB
    HTTP/1.1 302 Found
    Location: https://www.baidu.com/#access_token=4c5c7f4f-158a-4f73-9c14-ecbe78671872&token_type=bearer&state=sxy&expires_in=2418755
   
密码模式 password
1.POST http://localhost:10260/oauth/accessToken
    grant_type=password&username=admin&password=123456789&scope=read&client_id=uzs5G0HgTp

    HTTP/1.1 200 OK
    Content-Type: application/json;charset=UTF-8
    Cache-Control: no-store
    Pragma: no-cache
    {
      "accessToken":"8ce77011-87c9-48f3-8e4f-5cf945f047c7",
      "tokenType":"example",
      "expiresIn":3600,
      "refreshToken":"6b010e33-8bc3-4f3f-8123-66df589dc82f",
      "state":"example_value",
      "user":"admin"
     }

更新令牌 refresh_token
1.POST http://localhost:10260/oauth/accessToken
    grant_type=refresh_token&refresh_token=6b010e33-8bc3-4f3f-8123-66df589dc82f&client_id=uzs5G0HgTp

单点登陆 sso
1.GET http://localhost:10260/oauth/sso?response_type=token&client_id=uzs5G0HgTp&state=sxy&scope=read&redirect_uri=http://192.168.1.221:8010/ehr/browser/common/login/signin&user=admin