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

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
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

    public Schedule findById(int id){
        return scheduleDao.findById(id);
    }

    public List<Schedule> findMatch(String carId, Date date){
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        java.sql.Time time = new java.sql.Time(date.getTime());
        String sql = "SELECT schedule FROM Schedule schedule WHERE schedule.carId = :carId AND schedule.status = :status AND schedule.date = :date AND schedule.start <= :start AND schedule.end >= :end";
        Session session = currentSession();
        Query query = session.createQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setParameter("carId", carId);
        query.setParameter("status", Schedule.Status.on);
        query.setParameter("date", date1);
        query.setParameter("start", time);
        query.setParameter("end", time);
        return query.list();
    }

    public List<Object> getLevel(int page, int size) throws ParseException{
        Session session = currentSession();
        String hql = "SELECT DATE_FORMAT(date, '%Y-%m') FROM Schedule schedule GROUP BY DATE_FORMAT(date, '%Y-%m')";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    public Integer getLevelCount() throws ParseException{
        Session session = currentSession();
        String hql = "SELECT DATE_FORMAT(date, '%Y-%m') FROM Schedule schedule GROUP BY DATE_FORMAT(date, '%Y-%m')";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list().size();
    }

    public List<java.sql.Date> getDateGroup(String date, int page, int size) throws ParseException {
        Session session = currentSession();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date1 = dateFormat.parse(date);
        java.sql.Date date11 = new java.sql.Date(date1.getTime());
        Date date2 = new Date(DateUtils.addMonths(date1, 1).getTime() - 1);
        java.sql.Date date22 = new java.sql.Date(date2.getTime());
        String hql =  "SELECT schedule.date FROM Schedule schedule WHERE schedule.date BETWEEN :date1 AND :date2 GROUP BY schedule.date ORDER BY schedule.date ASC";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("date1", date11);
        query.setDate("date2", date22);
        //query.setParameter("date", "%" + date + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    public Integer getDateGroupCount(String date) throws ParseException {
        Session session = currentSession();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date1 = dateFormat.parse(date);
        java.sql.Date date11 = new java.sql.Date(date1.getTime());
        Date date2 = new Date(DateUtils.addMonths(date1, 1).getTime() - 1);
        java.sql.Date date22 = new java.sql.Date(date2.getTime());
        String hql =  "SELECT schedule.date FROM Schedule schedule WHERE schedule.date BETWEEN :date1 AND :date2 GROUP BY schedule.date ORDER BY schedule.date ASC";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("date1", date11);
        query.setDate("date2", date22);
        return query.list().size();
    }

    public List<Schedule> getDateMatch(java.sql.Date date) {
        Session session = currentSession();
        String hql = "SELECT schedule FROM Schedule schedule WHERE schedule.date = :date1 ORDER BY schedule.date ASC";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("date1", date);
        return query.list();
    }

    /**
     * 批量导入排班信息
     * @param schedules
     * @return
     */
    public boolean addSchedulesBatch(List<Map<String, Object>> schedules) {
        Map<String, Object> map;
        try{
            for(int i = 1; i <= schedules.size(); i++){
                map = schedules.get(i - 1);
                String dutyName = map.get("dutyName").toString();
                String gender;
                //性别：0未知，1为男，2为女
                if(null != map.get("gender")) {
                    if("男".equals(map.get("gender").toString())){
                        gender = "1";
                    }else if("女".equals(map.get("gender").toString())){
                        gender = "2";
                    }else{
                        gender = "0";
                    }
                }else {
                    gender = "0";
                }
                boolean main;
                if(null != map.get("main")){
                    if("主班".equals(map .get("main").toString())){
                        main = true;
                    }else if("副班".equals(map.get("main").toString())){
                        main = false;
                    }else {
                        main = true;
                    }
                }else{
                    main = true;
                }
                String dutyNum = map .get("dutyNum").toString();
                String dutyRole = map .get("dutyRole").toString();
                String dutyPhone = map .get("dutyPhone").toString();
                String carId = map .get("carId").toString();
                String creator = map .get("creator").toString();
                Date start = DateUtil.strToDate(map.get("start").toString());
                Date end = DateUtil.strToDate(map.get("end").toString());
                int diff = DateUtil.getDifferenceOfDays(start, end);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for(int j = 0; j <= diff; j ++) {
                    Schedule schedule = new Schedule();
                    java.sql.Date today = new java.sql.Date(DateUtils.addDays(start, j).getTime());
                    java.sql.Time startTime;
                    java.sql.Time endTime;
                    if(0 == j) {
                        startTime = new java.sql.Time(start.getTime());
                        Date tomorrow = DateUtils.addDays(dateFormat.parse(today.toString()), 1);
                        endTime = new java.sql.Time(tomorrow.getTime() - 1);
                    }else if(diff == j) {
                        startTime = new java.sql.Time(dateFormat.parse(today.toString()).getTime());
                        endTime = new java.sql.Time(end.getTime());
                    }else {
                        startTime = new java.sql.Time(dateFormat.parse(today.toString()).getTime());
                        Date tomorrow = DateUtils.addDays(dateFormat.parse(today.toString()), 1);
                        endTime = new java.sql.Time(tomorrow.getTime() - 1);
                    }
                    schedule.setDate(today);
                    schedule.setStart(startTime);
                    schedule.setEnd(endTime);
                    schedule.setDutyName(dutyName);
                    schedule.setGender(gender);
                    schedule.setMain(main);
                    schedule.setDutyNum(dutyNum);
                    schedule.setDutyRole(dutyRole);
                    schedule.setDutyPhone(dutyPhone);
                    schedule.setCarId(carId);
                    schedule.setCreator(creator);
                    schedule.setStatus(Schedule.Status.on);
                    scheduleDao.save(schedule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Schedule> findByIds(String ids) throws Exception{
        String sql = "SELECT schedule FROM Schedule schedule WHERE schedule.id in ("+ids+")";
        Session session = currentSession();
        Query query = session.createQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }
}
