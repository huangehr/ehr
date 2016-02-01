package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

/**
 * 组合条件，例如：姓名 + 手机号 + 出生地 + 生日 等非关键键数据用于检索
 * @author Sand
 * @version 1.0
 * @created 16-6月-2015 16:48:27
 */
public class CombinedInfoStragety extends AbstractSearchStragety  {

	public CombinedInfoStragety(){

	}

	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(DemographicInfo.class);
		return criteria;
	}
	@Override
	public  String addColumnList(String inList){
		//用类字段表示
		inList ="'name','gender','telphoneNo','birthday','birthPlace','nativePlace','martialStatus','nation','homeAddress','email'";
		return inList;
	}

	@Override
	public String getTableName(){
		//人口学的搜索
		return "'DemographicInfo'";
	}

	@Override
	public String getAddrList(String addrList){
		//人口学相关地址,用类字段表示
		addrList="birthPlace,workAddress,homeAddress";
		return addrList;
	}

	@Override
	public Criteria checkAddr(Criteria criteria,String column,String keyVal){
		//地址检查，人口学相关地址
		String addr=null;
		MAddress address=null;
		List<DemographicInfo> demographicInfoList = criteria.list();
		for (int j=demographicInfoList.size() -1, i = j; i>=0; i--) {
			if (column.equals("birthPlace")){
				//// TODO: 2016/1/21
//				address= (MAddress) demographicInfoList.get(i).getBirthPlace();
//				addr=address.getCanonicalAddress();
			} else if (column.equals("workAddress")){
//				address= (Address) demographicInfoList.get(i).getWorkAddress();
//				addr=address.getCanonicalAddress();
			} else if (column.equals("homeAddress")){
//				address= (Address) demographicInfoList.get(i).getWorkAddress();
//				addr=address.getCanonicalAddress();
			}else{
				continue;
			}

			if (!addr.contains(keyVal)) {
				criteria.add(Restrictions.not(Restrictions.eq(column, address)));
			}
		}
		return criteria;
	}

	@Override
	public Criteria generateQuery(Session session, Map<String, Object> args) {
		return super.generateQuery(session,args);
	}

	public void finalize() throws Throwable {
		super.finalize();
	}


}