package com.yihu.ehr.constants;

/**
 * @author linaz
 * @created 2016.06.03 15:57
 */
public enum UrlScope {

    Public(0),              // fastdfs
    Private(1);             // http

    private int url;

    UrlScope(int url) {
        this.url = url;
    }

}
