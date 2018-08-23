package com.yihu.ehr.basic.device.service;

import com.yihu.ehr.basic.device.dao.DeviceDao;
import com.yihu.ehr.entity.device.Device;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
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
                Device device = new Device();
                device.setDeviceName(map.get("deviceName").toString());
                device.setOrgCode(map.get("orgCode").toString());
                device.setOrgName(map.get("orgName").toString());
                device.setDeviceType(map.get("deviceType").toString());
                if(null!=map.get("purchaseNum")){
                    device.setPurchaseNum(Integer.valueOf(map .get("purchaseNum").toString()));
                }
                if("进口".equals(map.get("originPlace").toString())){
                    device.setOriginPlace("1");
                }else if("国产/合资".equals(map.get("originPlace").toString())){
                    device.setOriginPlace("2");
                }

                device.setManufacturerName(map.get("manufacturerName").toString());
                device.setDeviceModel(map.get("deviceModel").toString());
                device.setPurchaseTime(DateUtil.strToDate(map.get("purchaseTime").toString(),"yyyy-MM-dd"));
                if("新设备".equals(map.get("isNew").toString())){
                    device.setIsNew("1");
                }else if("二手设备".equals(map.get("isNew").toString())){
                    device.setIsNew("2");
                }

                if(null!=map.get("devicePrice")){
                    device.setDevicePrice(Double.valueOf(map .get("devicePrice").toString()));
                }
                if(null!=map.get("yearLimit")){
                    device.setYearLimit(Integer.valueOf(map .get("yearLimit").toString()));
                }

                if("启用".equals(map.get("status").toString())){
                    device.setStatus("1");
                }else if("未启用".equals(map.get("status").toString())){
                    device.setStatus("2");
                }else if("报废".equals(map.get("status").toString())){
                    device.setStatus("3");
                }

                if("是".equals(map.get("isGps").toString())){
                    device.setIsGps("1");
                }else if("否".equals(map.get("isGps").toString())){
                    device.setIsGps("0");
                }

                device.setCreator(map.get("creator").toString());
                deviceDao.save(device);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
