package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Random;

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
public class EnvelopRestEndPoint extends BaseRestEndPoint {

    @Value("${deploy.region}")
    protected Short deployRegion = 3502;

    /**
     * 返回一个信封对象。信封对象的返回场景参见 Envelop.
     *
     * @param modelList
     * @param totalCount
     * @return
     */
    protected Envelop getResult(List modelList, int totalCount) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(modelList);
        envelop.setTotalCount(totalCount);
        return envelop;
    }

    protected Envelop getPageResult(List detailModelList, int totalCount, int currPage, int rows) {
        Envelop result = new Envelop();
        result.setSuccessFlg(true);
        result.setDetailModelList(detailModelList);
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

    protected Envelop failed(String errMsg){
        return failed(errMsg, ErrorCode.REQUEST_NOT_COMPLETED.value());
    }

    protected Envelop failed(String errMsg, int errorCode){
        return failed(errMsg, errorCode, null);
    }

    protected Envelop failed(String errMsg, Object object){
        return failed(errMsg, ErrorCode.REQUEST_NOT_COMPLETED.value(), object);
    }

    protected Envelop failed(String errMsg, int errorCode, Object object){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg(errMsg);
        envelop.setErrorCode(errorCode);
        envelop.setObj(object);
        return envelop;
    }

    protected Envelop success(Object object){
        return success(object, null);
    }

    protected Envelop success(List list){
        return success(null, list);
    }

    protected Envelop success(Object object, List list){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(object);
        envelop.setDetailModelList(list);
        return envelop;
    }

    protected String getObjectId(BizObject bizObject){
        return new ObjectId(deployRegion, bizObject).toString();
    }

    /**
     * 获取指定长度的随机字符串
     * @param length
     * @return
     */
    protected String getRandomString(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length() - 1);//0~61
            buffer.append(str.charAt(number));
        }
        return buffer.toString();
    }
}
