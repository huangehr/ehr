package com.yihu.ehr.util.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.exception.ApiException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST风格控控制器基类。此控制器用于对API进行校验，并处理平台根层级的业务，如API参数校验，错误及返回码设定等。
 * <p>
 * 根层级的校验，如果是正确的，直接返回HTTP代码200，若出错，则会将HTTP返回代码设置为1X或2X，并在HTTP响应体中包含响应的信息。
 * HTTP响应体格式为JSON。
 * + 成功：会根据各业务逻辑自行决定要返回的数据，各业务模块的返回结构不同。
 * + 失败：{"code":"错误代码", "message":"错误原因"}
 *
 * @author zhiyong
 * @author Sand
 */
public class BaseRestController extends AbstractController{
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    /**
     * 通用：请求失败.
     *
     * 请求成功后, 返回的内容为: { code: "ha.archive.upload.failed", message: "档案上传失败"}
     * 其中 code 必须为特定的错误代码, message 必须为错误描述. 没有其他字段。
     *
     * @param errorCode
     * @param args
     * @return
     */
    public static void failed(ErrorCode errorCode, String errorDescription, String...args){
        throw new ApiException(errorCode, errorDescription);
    }


    protected Result failed(String errMsg){
        Result rs = getSuccessResult(false);
        rs.setErrorMsg(errMsg);
        return rs;
    }

    protected Result getResult(List detaiModelList, int totalCount, int currPage, int rows) {
        Result result = new Result();
        result.setSuccessFlg(true);
        result.setDetailModelList(detaiModelList);
        result.setTotalCount(totalCount);
        result.setCurrPage(currPage);
        result.setPageSize(rows);
        if(result.getPageSize()==0)
            return result;
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
