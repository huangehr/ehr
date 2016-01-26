package com.yihu.ehr.util.controller;

import com.yihu.ehr.constrant.Result;

import java.util.List;

/**
 * Created by Lincl on 2016/1/26.
 */
public class BaseController {

    protected Result getResult(List detaiModelList, int totalCount, int currPage, int rows) {
        Result result = new Result();
        result.setSuccessFlg(true);
        result.setDetailModelList(detaiModelList);
        result.setTotalCount(totalCount);
        result.setCurrPage(currPage);
        result.setPageSize(rows);
        if (result.getTotalCount() % result.getPageSize() > 0) {
            result.setTotalPage((result.getTotalCount() / result.getPageSize()) + 1);
        } else {
            result.setTotalPage(result.getTotalCount() / result.getPageSize());
        }
        return result;
    }

    protected Result getSuccessResult(Boolean flg) {
        Result result = new Result();
        result.setSuccessFlg(flg);
        return result;
    }

}
