package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.ScheduleDao;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.time.DateUtils;
import com.yihu.ehr.util.datetime.DateUtil;
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
import java.util.Map;

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
        String sql = "SELECT schedule FROM Schedule schedule WHERE schedule.carId = :carId AND schedule.status = :status AND schedule.start <= :start AND schedule.end >= :end";
        Session session = currentSession();
        Query query = session.createQuery(sql);
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


    /**
     * 批量导入排班信息
     */
    public boolean addSchedulesBatch(List<Map<String, Object>> schedules) {
        Map<String, Object> map;
        try{
            for(int i=1; i <= schedules.size(); i++){
                map = schedules.get(i-1);
                Schedule schedule=new Schedule();
                schedule.setDutyName(map .get("dutyName").toString());
                //性别：0未知，1为男，2为女
                if(null!=map .get("gender")){
                    if("男".equals(map .get("gender").toString())){
                        schedule.setGender("1");
                    }else if("女".equals(map .get("gender").toString())){
                        schedule.setGender("2");
                    }else{
                        schedule.setGender("0");
                    }
                }else{
                    schedule.setGender("0");
                }
                if(null!=map .get("main")){
                    if("主班".equals(map .get("main").toString())){
                        schedule.setMain(true);
                    }else if("副班".equals(map .get("main").toString())){
                        schedule.setMain(false);
                    }
                }else{
                    schedule.setMain(false);
                }
                schedule.setDutyNum(map .get("dutyNum").toString());
                schedule.setDutyRole(map .get("dutyRole").toString());
                schedule.setDutyPhone(map .get("dutyPhone").toString());
                schedule.setCarId(map .get("carId").toString());
                schedule.setStart(DateUtil.strToDate(map.get("start").toString()));
                schedule.setEnd(DateUtil.strToDate(map .get("end").toString()));
                schedule.setCreator(map .get("creator").toString());
                schedule.setStatus(Schedule.Status.on);
                scheduleDao.save(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
