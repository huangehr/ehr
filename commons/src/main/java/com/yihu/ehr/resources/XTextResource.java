package com.yihu.ehr.resources;

import com.yihu.ehr.constrant.ErrorCode;

import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 16:18
 */
public interface XTextResource {
    /**
     * 获取错误描述.
     *
     * @param errorCode 错误代码
     * @param args 错误描述可以替换的参数
     * @return
     */
    public String getErrorPhrase(final ErrorCode errorCode, final String... args);

    /**
     * 获取错误描述, 以Map形式返回.Map结构为 {"code": "error_code", "message": "the description"}
     *
     * @param errorCode 错误代码
     * @param args 错误描述可以替换的参数
     * @return
     */
    public Map<String, String> getErrorMap(final ErrorCode errorCode, final String... args);

    /**
     * 向Redis服务器注册资源.
     */
    public void registerTextResource(boolean overwrite);

    /**
     * 清除Redis服务器中的资源.
     */
    public void clearTextResource();

    /**
     * 获取资源的数量.
     *
     * @return
     */
    public int getResourceCount();
}
