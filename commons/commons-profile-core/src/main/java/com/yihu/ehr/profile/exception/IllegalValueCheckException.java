package com.yihu.ehr.profile.exception;

import java.io.IOException;

/**
 * @Author: zhengwei
 * @Date: 2018/5/25 17:49
 * @Description: 质控值域异常
 */
public class IllegalValueCheckException extends IOException {

    public IllegalValueCheckException(String message) {
        super(message);
    }
}
