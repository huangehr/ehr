package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.ScheduleDao;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Service - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class ScheduleService extends BaseJpaService<Schedule, ScheduleDao> {

    @Autowired
    private ScheduleDao scheduleDao;

    public List<Schedule> findMatch(String carId, Date date) throws Exception{
        String hql = "SELECT schedule FROM Schedule schedule WHERE schedule.carId = :carId AND schedule.status = :status AND schedule.start <= :start AND schedule.end >= :end";
        Session session = currentSession();
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setParameter("carId", carId);
        query.setParameter("status", Schedule.Status.on);
        query.setParameter("start", date);
        query.setParameter("end", date);
        return query.list();
    }

    public List<Object> getLevel(String date, int page, int size) throws Exception{
        Session session = currentSession();
        Query query = null;
        if(StringUtils.isEmpty(date)) {
            String hql = "SELECT DATE_FORMAT(start, '%Y-%m') FROM Schedule schedule GROUP BY DATE_FORMAT(start, '%Y-%m')";
            query = session.createQuery(hql);
            query.setFlushMode(FlushMode.COMMIT);
            query.setFirstResult(page - 1);
            query.setMaxResults(size);
        }else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            Date date1 = dateFormat.parse(date);
            Date date2 = new Date(DateUtils.addMonths(date1, 1).getTime() - 1);
            String hql = "SELECT schedule FROM Schedule schedule WHERE schedule.start BETWEEN :date1 AND :date2 ORDER BY schedule.start ASC";
            query = session.createQuery(hql);
            query.setFlushMode(FlushMode.COMMIT);
            query.setDate("date1", date1);
            query.setDate("date2", date2);
            //query.setParameter("date", "%" + date + "%");
            query.setFirstResult(page - 1);
            query.setMaxResults(size);
        }
        return query.list();
    }

    public Schedule findById(int id){
        return scheduleDao.findById(id);
    }

}
