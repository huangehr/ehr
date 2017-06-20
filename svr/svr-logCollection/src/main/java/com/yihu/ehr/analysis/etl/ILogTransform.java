package com.yihu.ehr.analysis.etl;

import com.yihu.ehr.analysis.entity.UserPortrait;
import org.json.JSONObject;

import java.util.List;

/**
 * 日志信息提取分析
 * <p>
 * Created by lyr-pc on 2017/2/17.
 */
public interface ILogTransform {

    /**
     * 日志信息提取分析
     *
     * @param log
     */
    List<UserPortrait> transform(JSONObject log) throws Exception;

    /**
     * 获取日志类型
     *
     * @return
     */
    int getLogType();

    /**
     * 获取日志类型名称
     *
     * @return
     */
    String getLogTypeName();
}
