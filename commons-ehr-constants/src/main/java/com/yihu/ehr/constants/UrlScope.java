package com.yihu.ehr.constants;

/**
 * @author linaz
 * @created 2016.06.03 15:57
 */
public enum UrlScope {

    Public(0),              // fastdfs
    Private(1);             // http

    private int url;

    UrlScope(int url) {    //    必须是private的，否则编译错误
        this.url = url;
    }

    public static UrlScope valueOf(int url) {
        switch (url) {
            case 0:
                return Public;
            case 1:
                return Private;
            default:
                return null;
        }
    }

    public int value() {
        return this.url;
    }

}
