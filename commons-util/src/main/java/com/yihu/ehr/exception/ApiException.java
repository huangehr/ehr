package com.yihu.ehr.exception;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.StringBuilderUtil;
import org.springframework.core.env.Environment;

/**
 * API 异常。使用错误代码初始化，并可接收用于补充错误消息的参数。
 * 用于描述错误代码的信息配置在各服务配置文件中，并由服务配置中心统一管理。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:05
 */
public class ApiException extends RuntimeException {
    private ErrorCode errorCode;
    private String[] args;

    public ApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String[] getErrorArgs() {
        return args;
    }

    public void setErrorArgs(String[] args) {
        this.args = args;
    }

    @Override
    public String toString(){
        Environment environment = SpringContext.getService(Environment.class);
        String description = environment.getProperty(errorCode.toString());

        if (null != description && null != args && args.length > 0){
            StringBuilderUtil util = new StringBuilderUtil(description);
            for (int i = 0; i < args.length; ++i){
                util.replace("{" + i + "}", args[i]);
            }

            description = util.toString();
        }

        return description;
    }
}
