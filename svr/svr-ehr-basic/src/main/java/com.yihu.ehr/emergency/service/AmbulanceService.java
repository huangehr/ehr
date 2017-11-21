package com.yihu.ehr.emergency.service;

import com.yihu.ehr.emergency.dao.AmbulanceDao;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Service - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@Service
@Transactional
public class AmbulanceService extends BaseJpaService<Ambulance, AmbulanceDao> {

    @Autowired
    private AmbulanceDao ambulanceDao;

    public Ambulance findById(String carId){
        return ambulanceDao.findById(carId);
    }

    public Ambulance findByPhone(String phone) {
        return ambulanceDao.findByPhone(phone);
    }

    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List idExist(String type,String[] values)
    {
        String sql ="";
        if(type.equals("id")){
           sql= "SELECT id FROM eme_ambulance WHERE id in(:values)";
        }else if(type.equals("phone")){
            sql= "SELECT phone FROM eme_ambulance WHERE phone in(:values)";
        }
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("values", values);
        return sqlQuery.list();
    }

    /**
     * 批量导入救护车
     */
    public boolean addAmbulancesBatch(List<Map<String, Object>> ambulances) {
        Map<String, Object> map;
        try{
            for(int i=1; i <= ambulances.size(); i++){
                map = ambulances.get(i-1);
                Ambulance ambulance=new Ambulance();
                ambulance.setId(map .get("id").toString());
                if(null!=map .get("initLongitude")){
                    ambulance.setInitLongitude(Double.valueOf(map .get("initLongitude").toString()));
                }else{
                    ambulance.setInitLongitude(0.0);
                }
                if(null!=map .get("initLatitude")){
                    ambulance.setInitLatitude(Double.valueOf(map .get("initLatitude").toString()));
                }else{
                    ambulance.setInitLatitude(0.0);
                }
                ambulance.setDistrict(map .get("district").toString());
                ambulance.setOrgCode(map .get("orgCode").toString());
                ambulance.setOrgName(map .get("orgName").toString());
                ambulance.setPhone(map .get("phone").toString());
                ambulance.setStatus(Ambulance.Status.wait);
                ambulance.setEntityName(map .get("entityName").toString());
                ambulance.setCreator(map .get("creator").toString());
                ambulanceDao.save(ambulance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
