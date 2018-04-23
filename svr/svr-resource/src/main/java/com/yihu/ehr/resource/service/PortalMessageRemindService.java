package com.yihu.ehr.resource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.services.DBQuery;
import com.yihu.ehr.resource.dao.PortalMessageRemindDao;
import com.yihu.ehr.resource.model.ProtalMessageRemind;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 提醒消息接口实现类.
 * 2017-02-04 add by zdm
 */
@Service
@Transactional
public class PortalMessageRemindService extends BaseJpaService<ProtalMessageRemind, PortalMessageRemindDao> {

}