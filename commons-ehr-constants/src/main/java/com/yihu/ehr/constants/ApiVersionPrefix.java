package com.yihu.ehr.constants;

/**
 * API版本前缀。用于适配Rest控制器的版本。定义好适用的版本之后，可以方便API版本编写。
 *
 * API版本格式为：v大版本.小版本。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.11.05 11:29
 */
public class ApiVersionPrefix {
    //public static final String CommonVersion = "/rest/{api_version:^v[0-9]\\d*\\.\\d*$}";   // 通用

    public static final String Version1_0 = "/rest/v1.0";      // v1.0 版本
}
