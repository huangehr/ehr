package com.yihu.ehr.basic.device.dao;

import com.yihu.ehr.entity.device.Device;
import com.yihu.ehr.entity.emergency.Ambulance;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
public interface DeviceDao extends PagingAndSortingRepository<Device, String> {

    Device findById(int id);

}
