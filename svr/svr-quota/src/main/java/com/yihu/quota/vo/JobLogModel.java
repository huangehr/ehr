package com.yihu.quota.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenweida on 2017/6/2.
 */
public class JobLogModel {
    private List<ErrModel> ErrorModels =new ArrayList<>();

    public List<ErrModel> getErrorModels() {
        return ErrorModels;
    }

    public void setErrorModels(List<ErrModel> errorModels) {
        ErrorModels = errorModels;
    }
}
