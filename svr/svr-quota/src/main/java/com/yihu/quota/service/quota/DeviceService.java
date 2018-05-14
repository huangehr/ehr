package com.yihu.quota.service.quota;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.services.DBQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

/**
 * @Author: zdm
 * @Date: 2018/5/10
 * @Description: 设备一览
 */
@Service
@Transactional
public class DeviceService {

    @Autowired
    DBQuery dbQuery;


    /**
     * @param year  设备购买年份
     * @param district  区县
     * @param organization  机构
     * @param page  当前页
     * @param size  分页大小
     * @return
     */
    public DataList  listMQcDeviceByYearAndDistrictAndOrg(String year,String district,String organization,int page ,int size)throws Exception{
       String districtSql= district == null? "": "AND a.administrativeDivision='"+district+"' ";
       String organizationSql=organization == null ? "":"AND (a.orgCode LIKE '%"+organization+"%' " + "OR a.orgName LIKE '%"+organization+"%') ";
       String sql="SELECT a.*,sde.value AS deviceTypeName FROM (SELECT   " +
                "d.id AS id, " +
                "o.org_code AS orgCode, " +
                "o.full_name AS orgName, " +
                "o.administrative_division AS administrativeDivision, " +
                "o.hos_type_id AS hosTypeId, " +
                "d.device_name AS deviceName, " +
                "d.device_type AS deviceType, " +
                "d.purchase_num AS purchaseNum, " +
                "CASE d.origin_place WHEN 1 THEN '进口' WHEN 2 THEN '国产/合资' END AS originPlace, " +
                "d.manufacturer_name AS manufacturerName, " +
                "d.device_model AS deviceModel, " +
                "d.purchase_time AS purchaseTime, " +
                "CASE d.is_new WHEN 1 THEN '新设备' WHEN 2 THEN '二手设备' END AS isNew, " +
                "d.device_price AS devicePrice, " +
                "d.year_limit AS yearLimit, " +
                "CASE d.status WHEN 1 THEN '启用' WHEN 2 THEN '未启用' WHEN 3 THEN '报废' END AS status, " +
                "CASE d.is_gps WHEN 1 THEN '是' WHEN  0 THEN '否'END AS isGps " +
                "FROM device d,organizations o " +
                "WHERE d.org_code=o.org_code) a " +
                "LEFT JOIN system_dict_entries sde " +
                "ON a.deviceType = sde.code WHERE sde.dict_id='181' " + districtSql + organizationSql +
                "AND LEFT(a.purchaseTime,4)='"+year+"' ORDER BY a.purchaseTime DESC";
        DataList list= dbQuery.queryBySql(sql,page,size);
        return list;
    }

}
