package com.yihu.ehr.patient.service.arapply;

import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.patient.dao.XArchiveRelationDao;
import com.yihu.ehr.patient.feign.PatientArchiveClient;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.poi.util.StringUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 档案申领实现类.
 *
 */
@Transactional
@Service
public class ArchiveRelationService  extends BaseJpaService<ArchiveRelation, XArchiveRelationDao> {

    @Autowired
    private XArchiveRelationDao archiveRelationDao;
    @Autowired
    private PatientArchiveClient patientArchiveClient;

    /**
     * 根据申请ID查询档案关联信息
     * @param applyId
     * @return
     * @throws Exception
     */
    public ObjectResult getArRelation(long applyId) throws Exception{
        ObjectResult re = new ObjectResult();
        ArchiveRelation result = archiveRelationDao.findByApplyId(applyId);
        if(result!=null) {
            re.setData(result);
        }
        return re;
    }

    /**
     * 个人档案列表
     */
    public ListResult archiveList(String idCardNo,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveRelation> list = archiveRelationDao.findByIdCardNo(idCardNo,pageRequest);

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 管理员--通过卡号获取未认领档案
     */
    public ListResult archiveUnbind(String cardNo,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveRelation> list = archiveRelationDao.findByCardNo(cardNo,pageRequest);

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 建立档案关联
     */
    public ArchiveRelation archiveRelation(ArchiveRelation relation) throws Exception
    {
        //根据profileId 判断是否存在
        String profileId = relation.getProfileId();
        ArchiveRelation ar =  archiveRelationDao.findByProfileId(profileId);
        if(ar != null)
        {
            relation.setId(ar.getId());
        }

        relation = archiveRelationDao.save(relation);

        return relation;
    }

    public ArchiveRelation findOne(Long id) {
        return   archiveRelationDao.findOne(id);
    }



    /**
     * 通过卡号+姓名关联HBase档案
     */
    public void relationByCardNoAndName(String cardNo,String name,String idcard) throws Exception
    {
        List<ArchiveRelation> list = archiveRelationDao.findByCardNoAndName(cardNo,name);
        if(list!=null && list.size()>0)
        {
              for(ArchiveRelation item:list)
              {
                 try {
                     patientArchiveClient.archiveRelation(item.getProfileId(), idcard);
                     item.setStatus("1");
                     item.setRelationDate(new Date());
                     item.setIdCardNo(idcard);
                 }
                 catch (Exception ex)
                 {
                     System.out.print("ProfileId:"+item.getProfileId()+" relation idcard:" + idcard +"fail!"+ex.getMessage());
                 }
              }
              archiveRelationDao.save(list);
        }
    }

    public int getIdentifyCount() {
        return archiveRelationDao.findIdentifyCount();
    }

    public int getUnIdentifyCount() {
        return archiveRelationDao.findUnIdentifyCount();
    }

    public int getArchiveCount() {
        return archiveRelationDao.findArchiveCount();
    }

    public int getInPatientCount() {
        return archiveRelationDao.findInPatientCount();
    }

    public int getOutPatientCount() {
        return archiveRelationDao.findOutPatientCount();
    }

    public int getPhysicalCount() {
        return archiveRelationDao.findPhysicalCount();
    }

    public int getInAndOutPatientCount() {
        return archiveRelationDao.findInAndOutPatientCount();
    }

    public int getTodayInWarehouseCount() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = sdf.format(date);
        int result = archiveRelationDao.findTodayInWarehouseCount(DateUtil.strToDate(curDate));
        return result;
    }

    public int getDailyAdd() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = sdf.format(date);
        int result = archiveRelationDao.FindDailyAdd(DateUtil.strToDate(curDate));
        return result;
    }

    //统计最近七天采集总数
    public List<Object> getCollectTocalCount() {
        Session session = currentSession();
        String sql = "SELECT count(1),date_format(create_date, '%Y-%c-%d') as date FROM archive_relation " +
                " where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_date) GROUP BY date_format(create_date, '%Y-%c-%d')";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }

    //统计最近七天采集门诊、住院各总数
    public List<Object> getCollectEventTypeCount(int eventType) {
        Session session = currentSession();
        String sql = "SELECT count(1),date_format(create_date, '%Y-%c-%d') as date,event_type FROM archive_relation " +
                "where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_date) and  event_type=:eventType  GROUP BY date_format(create_date, '%Y-%c-%d'),event_type;";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("eventType",eventType);
        return query.list();
    }

    //统计今天门诊、住院各总数
    public List<Object> getCollectTodayEventTypeCount() {
        Session session = currentSession();
        String sql = "SELECT count(1),event_type FROM archive_relation " +
                "where DATE_SUB(CURDATE(), INTERVAL 1 DAY) < date(create_date) GROUP BY event_type";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }
}