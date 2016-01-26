//package com.yihu.ehr.adaption.service;
//
//
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.data.sql.SQLGeneralDAO;
//import com.yihu.ha.dict.model.common.AdapterType;
//import com.yihu.ha.dict.model.common.XConventionalDictEntry;
//import com.yihu.ha.std.model.XCDAVersion;
//import com.yihu.ha.std.model.XCDAVersionManager;
//import com.yihu.ha.util.operator.StringUtil;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.transaction.Transactional;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author AndyCai
// * @version 1.0
// * @created 26-十月-2015 17:12:12
// */
//@Service(Services.OrgAdapterPlanManager)
//public class OrgAdapterPlanManager  extends SQLGeneralDAO implements XOrgAdapterPlanManager {
//	@Resource(name = Services.ConventionalDictEntry)
//    XConventionalDictEntry conventionalDictEntry;
//
//	@Resource(name = Services.AdapterDataSetManager)
//	XAdapterDataSetManager adapterDataSetManager;
//
//	@Resource(name = Services.AdapterDictManager)
//	XAdapterDictManager adapterDictManager;
//
//	@Resource(name = Services.AdapterOrgManager)
//	XAdapterOrgManager adapterOrgManager;
//
//	@Resource(name = Services.CDAVersionManager)
//	private XCDAVersionManager xcdaVersionManager;
//
//	public OrgAdapterPlanManager(){
//
//	}
//
//	public void finalize() throws Throwable {
//
//	}
//
//	/**
//	 * 新增方案信息
//	 *
//	 * @param orgAdapterPlan
//	 */
//	public boolean addOrgAdapterPlan(XOrgAdapterPlan orgAdapterPlan){
//		try {
//			String org= orgAdapterPlan.getOrg();
//			if (orgAdapterPlan.getVersion()==null) {
//				throw new IllegalArgumentException("版本号不能为空");
//			}
//			if (orgAdapterPlan.getCode()==null) {
//				throw new IllegalArgumentException("代码不能为空");
//			}
//			if (orgAdapterPlan.getType()==null) {
//				throw new IllegalArgumentException("类型不能为空");
//			}
//			if (org==null) {
//				throw new IllegalArgumentException("映射机构不能为空");
//			}
//			saveEntity(orgAdapterPlan);
//			//拷贝父级方案映射
//			Long parentId = orgAdapterPlan.getParentId();
//			if (parentId!=null){
//				XOrgAdapterPlan parentAdapterPlan = getOrgAdapterPlan(parentId);
//				if (parentAdapterPlan!=null){
//					String parentOrg = parentAdapterPlan.getOrg();
//					//是否拷贝采集标准
//					if (!org.equals(parentOrg)){
//						if (adapterOrgManager.isExistData(org)){
//							adapterOrgManager.deleteData(org);//清除数据
//						}
//						adapterOrgManager.copy(org,parentOrg);
//					}
//					//拷贝映射方案
//					copyPlan(orgAdapterPlan.getId(),parentId);
//				}
//			};
//
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 删除方案信息
//	 *
//	 * @param orgAdapterPlan
//	 */
//	public boolean deleteOrgAdapterPlan(XOrgAdapterPlan orgAdapterPlan){
//		try {
//			//要先删除数据集映射
//			List<Long> adapterMetaDatas = adapterDataSetManager.getAdapterMetaDataIds(orgAdapterPlan.getId());
//			adapterDataSetManager.deleteAdapterDataSet(adapterMetaDatas.toArray(new Long[adapterMetaDatas.size()]));
//			deleteEntity(orgAdapterPlan);
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//
//	/**
//	 * 批量删除方案信息
//	 *
//	 * @param ids
//	 */
//	public int deleteOrgAdapterPlan(Long[] ids){
//		if (ids==null || ids.length==0) {
//			return 0;
//		}
//		//要先删除数据集映射
//		for(Long id:ids) {
//			List<Long> adapterMetaDatas = adapterDataSetManager.getAdapterMetaDataIds(id);
//			adapterDataSetManager.deleteAdapterDataSet(adapterMetaDatas.toArray(new Long[adapterMetaDatas.size()]));
//		}
//		List<Long> lst=new ArrayList<>();
//		lst =  Arrays.asList(ids);
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		Query query = session.createQuery("delete from OrgAdapterPlan where id in (:ids)");
//		query.setParameterList("ids", lst);
//		return query.executeUpdate();
//	}
//
//	/**
//	 * 根据ID获取方案信息
//	 *
//	 * @param id
//	 */
//	@Transactional(Transactional.TxType.SUPPORTS)
//	public XOrgAdapterPlan getOrgAdapterPlan(Long id){
//		XOrgAdapterPlan orgAdapterPlan = getEntity(OrgAdapterPlan.class,id);
//		return orgAdapterPlan;
//	}
//
//	/**
//	 * 根据方案对象获取前台方案展示信息
//	 *
//	 * @param orgAdapterPlan
//	 */
//	public AdapterPlanModel getOrgAdapterPlan(XOrgAdapterPlan orgAdapterPlan){
//		AdapterPlanModel adapterPlanModel = new AdapterPlanModel();
//		adapterPlanModel.setId(orgAdapterPlan.getId());
//		Long parentId=orgAdapterPlan.getParentId();
//		if (parentId!=null){
//			adapterPlanModel.setParentId(parentId);
//			XOrgAdapterPlan adapterPlan = getOrgAdapterPlan(parentId);
//			if (adapterPlan!=null){
//				adapterPlanModel.setParentName(adapterPlan.getName());
//			}
//		}
//		adapterPlanModel.setCode(orgAdapterPlan.getCode());
//		adapterPlanModel.setName(orgAdapterPlan.getName());
//		adapterPlanModel.setType(orgAdapterPlan.getType().getCode());
//		adapterPlanModel.setTypeValue(orgAdapterPlan.getType().getValue());
//		adapterPlanModel.setDescription(orgAdapterPlan.getDescription());
//		adapterPlanModel.setVersion(orgAdapterPlan.getVersion().getVersion());
//		String org=orgAdapterPlan.getOrg();
//		if (org!=null && !org.equals("")){
//			adapterPlanModel.setOrg(org);
//			XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(org);
//			if (adapterOrg!=null){
//				adapterPlanModel.setOrgValue(adapterOrg.getName());
//			}
//		}
//		return adapterPlanModel;
//	}
//
//	/**
//	 * 搜索方案
//	 *
//	 * @param args
//	 */
//	public List<XOrgAdapterPlan> searchOrgAdapterPlan(Map<String, Object> args){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		String code = (String)args.get("code");
//		String name = (String) args.get("name");
//		List<AdapterType> typeLs = (List<AdapterType>) args.get("typeLs");
//		String org = (String) args.get("org");
//		Integer page = (Integer) args.get("page");
//		Integer pageSize = (Integer) args.get("pageSize");
//		String hql=" from OrgAdapterPlan where (code like :code or name like :name) ";
//		if (!StringUtil.isEmpty(org)){
//			hql+=" and org =:org";
//		}
//		if(typeLs!=null){
//			for(int i=0;i<typeLs.size();i++){
//				if(i==0){
//					if(typeLs.size()==1) {
//						hql += " and type =:type"+i+" ";
//					} else {
//						hql += " and (type =:type"+i+" ";
//					}
//				} else if(i==typeLs.size()-1) {
//					hql += " or type =:type"+i+") ";
//				} else {
//					hql += "or type =:type"+i+" ";
//				}
//			}
//		}
//
//		Query query = session.createQuery(hql);
//		query.setString("code", "%"+code+"%");
//		query.setString("name", "%"+name+"%");
//		if (!StringUtil.isEmpty(org)){
//			query.setString("org", org);
//		}
//		if(typeLs!=null){
//			for(int i=0;i<typeLs.size();i++){
//				query.setParameter("type"+i, typeLs.get(i));
//			}
//		}
//		query.setMaxResults(pageSize);
//		query.setFirstResult((page - 1) * pageSize);
//		List<XOrgAdapterPlan> orgAdapterPlans = query.list();
//
//		return orgAdapterPlans;
//	}
//
//	/**
//	 * 搜索方案前台展示
//	 *
//	 * @param args
//	 */
//	public List<AdapterPlanBrowserModel> searchAdapterPlanBrowser(Map<String, Object> args){
//		List<XOrgAdapterPlan> orgAdapterPlans = searchOrgAdapterPlan(args);
//		List<AdapterPlanBrowserModel> plans = new ArrayList<>();
//		Integer order = 1;
//		Long parentId;
//		String org;
//
//		for(XOrgAdapterPlan orgAdapterPlan:orgAdapterPlans){
//			AdapterPlanBrowserModel adapterPlan = new AdapterPlanBrowserModel();
//			adapterPlan.setOrder(order++);
//			adapterPlan.setId(orgAdapterPlan.getId());
//			adapterPlan.setCode(orgAdapterPlan.getCode());
//			adapterPlan.setName(orgAdapterPlan.getName());
//			parentId =orgAdapterPlan.getParentId();
//			if (parentId!=null&&getOrgAdapterPlan(parentId)!=null) {
//				adapterPlan.setParentCode(getOrgAdapterPlan(parentId).getCode());
//				adapterPlan.setParentName(getOrgAdapterPlan(parentId).getName());
//			}
//			adapterPlan.setType(orgAdapterPlan.getType().getCode());
//			adapterPlan.setTypeValue(orgAdapterPlan.getType().getValue());
//			org=orgAdapterPlan.getOrg();
//			if (org!=null&&!org.equals("")){
//				XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(org);
//				if (adapterOrg!=null) {
//					adapterPlan.setOrg(org);
//					adapterPlan.setOrgValue(adapterOrg.getName());
//				}
//			}
//			adapterPlan.setVersion(orgAdapterPlan.getVersion().getVersion());
//			adapterPlan.setVersionName(orgAdapterPlan.getVersion().getVersionName());
//			plans.add(adapterPlan);
//		}
//		return plans;
//	}
//
//	/**
//	 * 搜索方案数量
//	 *
//	 * @param args
//	 */
//	public int searchOrgAdapterPlanInt(Map<String, Object> args){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		String code = (String)args.get("code");
//		String name = (String) args.get("name");
//
//		List<AdapterType> typeLs = (List<AdapterType>) args.get("typeLs");
//		String org = (String) args.get("org");
//		String hql=" from OrgAdapterPlan where (code like :code or name like :name) ";
//		if (!StringUtil.isEmpty(org)){
//			hql+=" and org =:org";
//		}
//		if(typeLs!=null){
//			for(int i=0;i<typeLs.size();i++){
//				if(i==0){
//					if(typeLs.size()==1) {
//						hql += " and type =:type"+i+" ";
//					} else {
//						hql += " and (type =:type"+i+" ";
//					}
//				} else if(i==typeLs.size()-1) {
//					hql += " or type =:type"+i+") ";
//				} else {
//					hql += "or type =:type"+i+" ";
//				}
//			}
//		}
//
//		Query query = session.createQuery(hql);
//		query.setString("code", "%"+code+"%");
//		query.setString("name", "%"+name+"%");
//		if (!StringUtil.isEmpty(org)){
//			query.setString("org", org);
//		}
//		if(typeLs!=null){
//			for(int i=0;i<typeLs.size();i++){
//				query.setParameter("type"+i, typeLs.get(i));
//			}
//		}
//
//		return query.list().size();
//	}
//
//	public boolean isAdapterCodeExist(String code){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//
//		Query query = session.createQuery("select 1 from OrgAdapterPlan where code = :code");
//		query.setString("code", code);
//
//		return query.list().size()>0?true:false;
//	}
//
//	/**
//	 * 更新方案信息
//	 *
//	 * @param orgAdapterPlan
//	 */
//	public boolean updateOrgAdapterPlan(XOrgAdapterPlan orgAdapterPlan){
//		try {
//			if (orgAdapterPlan.getVersion()==null) {
//				throw new IllegalArgumentException("版本号不能为空");
//			}
//			if (orgAdapterPlan.getCode()==null) {
//				throw new IllegalArgumentException("代码不能为空");
//			}
//			if (orgAdapterPlan.getType()==null) {
//				throw new IllegalArgumentException("类型不能为空");
//			}
//			updateEntity(orgAdapterPlan);
//		}catch (Exception e){
//			return false;
//		}
//
//		return true;
//	}
//
//	/**
//	 * 根据类型获取方案信息
//	 *
//	 * @param type
//	 */
//	public List<XOrgAdapterPlan> getOrgAdapterPlan(String type) {
//		AdapterType adapterType = conventionalDictEntry.getAdapterType(type);
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		Query query = null;
//		if (adapterType==null || adapterType.getCode().equals("2")){
//			//医院，父级方案没有限制
//			query = session.createQuery("from OrgAdapterPlan");
//		}else if (adapterType.getCode().equals("3")) {
//			//区域,父级方案只能选择厂商或区域选择
//			query = session.createQuery("from OrgAdapterPlan where (type=:factory or type=:area)");
//			query.setParameter("factory", conventionalDictEntry.getAdapterType("1"));
//			query.setParameter("area", conventionalDictEntry.getAdapterType("3"));
//		}
//
//		List<XOrgAdapterPlan> orgAdapterPlans = query.list();
//		return orgAdapterPlans;
//	}
//
//	/**
//	 * 获取已定制标准数据集
//	 *
//	 * @param planId
//	 */
//	public List<Long> getAdapterDataSet(Long planId){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		Query query = session.createQuery("select dataSetId from AdapterDataSet where adapterPlanId = :planId group by dataSetId");
//		query.setParameter("planId", planId);
//		return query.list();
//	}
//
//	/**
//	 * 获取已定制标准数据元
//	 *
//	 * @param planId
//	 */
//	public List<XAdapterDataSet> getAdapterMetaData(Long planId){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		Query query = session.createQuery("from AdapterDataSet where adapterPlanId = :planId");
//		query.setParameter("planId", planId);
//		return query.list();
//	}
//
//	/**
//	 * 标准定制
//	 *
//	 * @param planId
//	 * @param adapterCustomizes
//	 */
//	public boolean adapterDataSet(Long planId,List<AdapterCustomize> adapterCustomizes){
//		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//		List<XAdapterDataSet> adapterMetaDataList = getAdapterMetaData(planId);
//		List<Long> lst=new ArrayList<>();
//		Long metaDataId;
//		boolean adapterFlag=true;
//		//先增加
//		for(AdapterCustomize adapter:adapterCustomizes){
//			//不删除的数据元ID
//			lst.add(Long.valueOf(adapter.getId()));
//			if(adapter.getPid().equals("0")||adapter.getPid().equals("-1")) {
//				//没有数据元的数据集
//				adapterFlag=false;
//			}else {
//				for(XAdapterDataSet adapterDataSet:adapterMetaDataList){
//					//数据元，若存在不定制，退出循环
//					adapterFlag = !adapter.getId().equals(adapterDataSet.getMetaDataId().toString());
//					if (!adapterFlag){
//						break;
//					}
//				}
//			}
//			if (adapterFlag) {
//				XAdapterDataSet dataSet = new AdapterDataSet();
//				dataSet.setAdapterPlanId(planId);
//				dataSet.setDataSetId(Long.valueOf(adapter.getPid()));
//				metaDataId = Long.valueOf(adapter.getId());
//				if (metaDataId!=null){
//					dataSet.setMetaDataId(metaDataId);
//				}
//				adapterDataSetManager.addAdapterDataSet(dataSet);
//			}
//			adapterFlag=true;
//		}
//
//		//删除取消的数据集、数据元
//		String hql = "delete from AdapterDataSet where adapterPlanId = :planId ";
//		if(lst.size()>0){
//			hql += " and metaDataId not in (:ids)";
//		}
//		Query query = session.createQuery(hql);
//		query.setParameter("planId", planId);
//		if(lst.size()>0){
//			query.setParameterList("ids", lst);
//		}
//
//		int row = query.executeUpdate();
//
//		return true;
//	}
//
//	/**
//	 * 方案拷贝
//	 *
//	 * @param planId
//	 * @param parentPlanId
//	 */
//	public boolean copyPlan(Long planId,Long parentPlanId){
//		//数据集映射拷贝
//		List<XAdapterDataSet> adapterDataSets = adapterDataSetManager.getAdapterMetaDataByPlan(parentPlanId);
//		for(XAdapterDataSet adapterDataSet:adapterDataSets){
//			XAdapterDataSet newAdapter = new AdapterDataSet();
//			newAdapter.setNewObject(adapterDataSet);
//			newAdapter.setAdapterPlanId(planId);
//			saveEntity(newAdapter);
//		}
//		//字典映射拷贝
//		List<XAdapterDict> adapterDicts = adapterDictManager.getAdapterDictByPlan(parentPlanId);
//		for(XAdapterDict adapterDict:adapterDicts){
//			XAdapterDict newAdapter = new AdapterDict();
//			newAdapter.setNewObject(adapterDict);
//			newAdapter.setAdapterPlanId(planId);
//			saveEntity(newAdapter);
//		}
//		return true;
//	}
//
//	/**
//	 * 搜索方案
//	 *
//	 * @param args
//	 */
//	public List<XOrgAdapterPlan> getOrgAdapterPlanByOrgCodeAndVersionCode(Map<String, Object> args){
//
//		List<XOrgAdapterPlan> orgAdapterPlans=null;
//		try {
//			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//			String versioncode = (String) args.get("versioncode");
//			String orgcode = (String) args.get("orgcode");
//
//			XCDAVersion xcdaVersion = xcdaVersionManager.getVersionById(versioncode);
//
//			Query query = session.createQuery("from OrgAdapterPlan where version = :version and org like :org");
//			query.setParameter("version", xcdaVersion);
//			query.setString("org", orgcode);
//			orgAdapterPlans = query.list();
//		}
//		catch (Exception ex)
//		{
//
//		}
//		return orgAdapterPlans;
//	}
//
//	public List<XOrgAdapterPlan> getOrgAdapterPlanByOrgCode(Map<String, Object> args){
//
//		List<XOrgAdapterPlan> orgAdapterPlans=null;
//		try {
//			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//			String orgcode = (String) args.get("orgcode");
//
//			Query query = session.createQuery("from OrgAdapterPlan where org = :org and status=1 order by version desc");
//			query.setString("org", orgcode);
//			orgAdapterPlans = query.list();
//		}
//		catch (Exception ex)
//		{
//
//		}
//		return orgAdapterPlans;
//	}
//}