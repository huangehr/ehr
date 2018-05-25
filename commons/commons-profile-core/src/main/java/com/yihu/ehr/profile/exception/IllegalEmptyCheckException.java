package com.yihu.ehr.profile.exception;

import java.io.IOException;

/**
 * @Author: zhengwei
 * @Date: 2018/5/25 17:49
 * @Description:质控非空异常
 */
public class IllegalEmptyCheckException extends IOException {

    public IllegalEmptyCheckException(String messgae) {
        super(messgae);
    }
}
