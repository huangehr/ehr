package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.AttendanceDao;
import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AttendanceService extends BaseJpaService<Attendance, AttendanceDao> {

    @Autowired
    private AttendanceDao attendanceDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Attendance findByCarIdAndStatus(String carId, List<Attendance.Status> statuses) {
        return attendanceDao.findByCarIdAndStatus(carId, statuses);
    }

    public Attendance findByCreateDateAndCarId(Date startTime, String carId) {
        List<Attendance> attendanceList = attendanceDao.findByCreateDateAndCarId(startTime, carId);
        if (attendanceList != null && attendanceList.size() > 0) {
            return attendanceList.get(0);
        }else {
            return null;
        }
    }

    public Attendance findById(int id) {
        return attendanceDao.findById(id);
    }

    public List<Map<String,Object>> queryChart1(String flag){
        String sql = "";
        if("1".equals(flag)){
            sql="select b.init_address name,count(b.init_address) value from eme_attendance a ,eme_location b,eme_ambulance c " +
                    " where c.location = b.id  and a.car_id = c.id and date_format(a.create_date,'%Y-%m')=date_format(now(),'%Y-%m') " +
                    " group by b.init_address order by b.init_address";
        }else if("2".equals(flag)){
            sql="select b.init_address name,count(b.init_address) value from eme_attendance a ,eme_location b,eme_ambulance c " +
                    " where c.location = b.id  and a.car_id = c.id and QUARTER(a.create_date)=QUARTER(now())" +
                    " group by b.init_address order by b.init_address";
        }else{
            sql="select b.init_address name,count(b.init_address) value from eme_attendance a ,eme_location b,eme_ambulance c " +
                    " where c.location = b.id  and a.car_id = c.id and YEAR(a.create_date)=YEAR(now())" +
                    " group by b.init_address order by b.init_address";
        }
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String,Object>> queryChart2(String startDate,String endDate){
        String sql = "select b.init_address name,count(b.init_address) value,a.create_date from" +
                " eme_attendance a ,eme_location b,eme_ambulance c where" +
                " c.location = b.id  and a.car_id = c.id and a.create_date>='"+startDate+"' and date_sub(a.create_date,interval 1 day)<='"+endDate+"'" +
                " group by b.init_address,a.create_date";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String,Object>> queryChart3(){
        String sql = "select longitude, latitude,count(longitude||latitude) value from eme_attendance group by longitude,latitude";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String,Object>> queryChart4(){
        String sql = "select b.init_address name,count(b.init_address) value from eme_attendance a ,eme_location b,eme_ambulance c" +
                " where c.location = b.id  and a.car_id = c.id group by b.init_address";
        return jdbcTemplate.queryForList(sql);
    }
    public List<Map<String,Object>> queryChart5(){
        String sql = "select a.create_date,a.car_id,b.init_address from" +
                " eme_attendance a ,eme_location b,eme_ambulance c where" +
                "  c.location = b.id  and a.car_id = c.id ";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String,Object>> queryChart7(){
        String sql = "select chief_complaint name,count(chief_complaint) value from eme_attendance group by chief_complaint";
        return jdbcTemplate.queryForList(sql);
    }
}
