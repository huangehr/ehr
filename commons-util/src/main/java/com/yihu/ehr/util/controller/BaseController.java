package com.yihu.ehr.util.controller;

import com.yihu.ehr.constrant.Result;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class BaseController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    public Result getResult(List detaiModelList, int totalCount, int currPage, int rows) {

        Result result = new Result();
        result.setSuccessFlg(true);
        result.setDetailModelList(detaiModelList);
        result.setTotalCount(totalCount);
        result.setCurrPage(currPage);
        result.setPageSize(rows);
        if(result.getTotalCount()%result.getPageSize()>0){
            result.setTotalPage((result.getTotalCount()/result.getPageSize())+1);
        }else {
            result.setTotalPage(result.getTotalCount()/result.getPageSize());
        }

        return result;
    }

}
