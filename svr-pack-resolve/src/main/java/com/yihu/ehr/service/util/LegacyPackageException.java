package com.yihu.ehr.service.util;

/**
 * 历史档案包异常。当解析EHR 0.1版本采集的数据时会出现此异常。
 *
 * @author Sand
 * @created 2016.05.27 16:14
 */
public class LegacyPackageException extends RuntimeException {
    /**
     * Constructs an LegacyPackageException
     *
     * @param message Message explaining the exception condition
     */
    public LegacyPackageException(String message) {
        super( message );
    }

    /**
     * Constructs an LegacyPackageException
     *
     * @param message Message explaining the exception condition
     * @param cause The underlying cause
     */
    public LegacyPackageException(String message, Throwable cause) {
        super( message, cause );
    }
}
