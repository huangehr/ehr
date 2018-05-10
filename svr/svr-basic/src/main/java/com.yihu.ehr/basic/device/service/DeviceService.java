package com.yihu.ehr.basic.device.service;

import com.yihu.ehr.basic.device.dao.DeviceDao;
import com.yihu.ehr.entity.device.Device;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
@Service
@Transactional
public class DeviceService extends BaseJpaService<Device, DeviceDao> {

    @Autowired
    private DeviceDao deviceDao;

    public Device findById(int carId){
        return deviceDao.findById(carId);
    }

    /**
     * 批量导入救护车
     */
    public boolean addBatch(List<Map<String, Object>> list) {
        Map<String, Object> map;
        try{
            for(int i=1; i <= list.size(); i++){
                map = list.get(i-1);
//                Ambulance ambulance=new Ambulance();
//                ambulance.setId(map .get("id").toString());
//                /**
//                if(null!=map .get("initLongitude")){
//                    ambulance.setInitLongitude(Double.valueOf(map .get("initLongitude").toString()));
//                }else{
//                    ambulance.setInitLongitude(0.0);
//                }
//                if(null!=map .get("initLatitude")){
//                    ambulance.setInitLatitude(Double.valueOf(map .get("initLatitude").toString()));
//                }else{
//                    ambulance.setInitLatitude(0.0);
//                }
//                ambulance.setDistrict(map .get("district").toString());
//                */
//                //ambulance.setOrgCode(map .get("orgCode").toString());
//                ambulance.setOrgName(map .get("orgName").toString());
//                ambulance.setPhone(map .get("phone").toString());
//                ambulance.setStatus(Ambulance.Status.wait);
//                ambulance.setEntityName(map .get("entityName").toString());
//                ambulance.setCreator(map .get("creator").toString());
//                deviceDao.save(ambulance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
