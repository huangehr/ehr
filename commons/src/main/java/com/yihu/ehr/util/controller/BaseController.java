package com.yihu.ehr.util.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.log.LogService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseController extends AbstractController{

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

    public Result getSuccessResult(Boolean flg) {
        Map<String,Object> mp = new HashMap<String,Object>();
        Result result = new Result();
        result.setSuccessFlg(flg);
        return result;
    }

    public String encodeStr(String str) {
        try {
            if (str == null) {
                return null;
            }
            return new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LogService.getLogger(BaseController.class).error(ex.getMessage());

            return null;
        }
    }
}
