package com.yihu.ehr.profile.exception;

import java.io.IOException;

/**
 * Created by progr1mmer on 2018/5/5.
 */
public class IllegalJsonFileException extends IOException {

    public IllegalJsonFileException(String message) {
        super(message);
    }
}
