package com.yihu.ehr.resources;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RedisNamespace;
import com.yihu.ehr.redis.XRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 资源包装类.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 16:16
 */
@Service
public class TextResource {
    @Autowired
    Environment environment;

    @Autowired
    XRedisClient redisClient;

    static String makeKey(String subKey) {
        return RedisNamespace.TextResource + subKey;
    }

    
    public String getErrorPhrase(final ErrorCode errorCode, final String... args) {
        String description = redisClient.get(makeKey(errorCode.getErrorCode()));

        return replaceArgs(description, args);
    }

    
    public Map<String, String> getErrorMap(final ErrorCode errorCode, final String... args) {
        String description = redisClient.get(makeKey(errorCode.getErrorCode()));

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("code", errorCode.getErrorCode());
        errorMap.put("message", replaceArgs(description, args));

        return errorMap;
    }

    private String replaceArgs(String description, final String... args) {
        if (description == null) {
            description = "";

            for (int i = 0; i < args.length; ++i) {
                description += args[i];
            }
        } else if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                description = description.replace("{" + i + "}", args[i]);
            }
        }

        return description;
    }

    public void registerTextResource(boolean overwrite) {
        /*try {
            // 向REDIS数据加加载预配置的数据及资源, 若已存在则忽略
            XEnvironmentOption environmentConfig = ServiceFactory.getService(Services.EnvironmentOption);

            String classPath = TextResource.class.getResource("/").getPath();
            String[] files = environmentConfig.getOption(EnvironmentOptions.TextResource).split(";");
            for (String file : files) {
                file = file.replace('\n', ' ');

                Properties properties = PropertyLoader.loadFile(classPath + file.trim());
                for (Object code : properties.keySet()) {
                    String key = makeKey((String) code);
                    if(redisClient.hasKey(key.toString()) && !overwrite) continue;

                    String value = (String) properties.get(code);
                    redisClient.set(key, value);
                }
            }
        } catch (Exception ex) {
            LogService.getLogger(TextResource.class).error("资源初始化: " + ex.getMessage());
        }*/
    }

    
    public void clearTextResource() {
        Set<String> keys = redisClient.keys(makeKey("*"));
        keys.forEach(redisClient::delete);
    }

    
    public int getResourceCount() {
        return redisClient.keys(makeKey("*")).size();
    }
}
