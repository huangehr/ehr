package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.*;
import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.model.AppApi;
import com.yihu.ehr.basic.apps.model.AppsRelation;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.user.dao.RoleApiRelationDao;
import com.yihu.ehr.basic.user.dao.RoleAppRelationDao;
import com.yihu.ehr.basic.user.dao.RoleReportRelationDao;
import com.yihu.ehr.basic.user.dao.RolesDao;
import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Sand
 * @version 1.0
 * @created 03-8月-2015 16:53:06
 */
@Service
@Transactional
public class AppsRelationService extends BaseJpaService<AppsRelation, AppsRelationDao> {


}