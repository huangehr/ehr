package com.yihu.quota.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2018/5/9.
 */
public class FilterModel {
    private List<Map<String, Object>>  dataList;//可能是list 也可能是map 或者实体对象
    private List<ErrModel> ErrorModels =new ArrayList<>();//

    public FilterModel(List<Map<String, Object>>  dataList, List<ErrModel> errorModels) {
        this.dataList = dataList;
        ErrorModels = errorModels;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

    public List<ErrModel> getErrorModels() {
        return ErrorModels;
    }

    public void setErrorModels(List<ErrModel> ErrorModels) {
        this.ErrorModels = ErrorModels;
    }

}
