package com.yihu.ehr.paient.paientIdx.model;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

/**
 * 搜索策略抽象类.
 * @author Sand
 * @version 1.0
 * @updated 16-6月-2015 16:39:58
 */
public abstract class AbstractSearchStragety {

	private AbstractSearchStragety next;
	private AbstractSearchStragety previous;

	public AbstractSearchStragety(){}

    public Criteria createCriteria(Session session){
        Criteria criteria=null;
        return criteria;
    }
    public String addColumnList(String inList){
        //添加特定条件，用类字段表示
        inList +="";
        return inList;
    }
    public String getTableName(){
        //获取表对应类名，用做区分用
        return "";
    }
    public String getAddrList(String addrList){
        //获取地址相关列名,用类字段表示
        addrList +="";
        return addrList;
    }
    public String getOrgrList(String org){
        //获取机构相关列名,用类字段表示
        org +="";
        return org;
    }

    public Criteria checkAddr(Criteria criteria,String column,String keyVal){
        return criteria;
    }
    public Criteria checkOrg(Criteria criteria,String column,String keyVal){
        return criteria;
    }
    
    public Criteria addNotNull(Session session,Criteria criteria){
        return criteria;
    }

    public Criteria generateQuery(Session session, Map<String, Object> args) {
        Criteria criteria = null;
        int k=0;
        String hql=null;
        String column=null;
        String keyLast3=null;
        String keyLast5=null;
        String keyName=null;
        String keyName1=null;
        Object keyVal=null;
        Object keyValTemp=null;
        Object datestart=null;
        Object dateend=null;
        String searchType=null;
        String tableName=null;
        String inList = "'number','ownerName','createDate','type'";//卡搜索策略公有的信息字段,用类字段表示
        inList = addColumnList(inList);
        String addrList= getAddrList("");
        String orgList=getOrgrList("");

        criteria = createCriteria(session);
        if (criteria == null) return null;
        tableName = getTableName();
        if (tableName == null) return null;
        hql = " from MappingColumn where tableName= " + tableName + " and columnName in " + "(" + inList + ")";
        Query query = session.createQuery(hql);
        List keyList = query.list();
        MappingColumn mp=new MappingColumn();
        if (!keyList.isEmpty()) {
            for (int j=keyList.size() -1,i = j; i>=0; i--) {
                mp=(MappingColumn)keyList.get(i);
                keyName=mp.getMapKey();
                column=mp.getColumnName();
                keyVal = args.get(keyName);

                if (keyVal != null) {
                    if ((","+addrList+",").contains("," + column + ",")){
                        continue;
                    }
                    if ((","+orgList+",").contains("," + column + ",")){
                        continue;
                    }
                    searchType=mp.getSearchType();
                    if (searchType.equals("between")) {
                        //日期类型，只能Start，End结尾 如createDateStart，createDateEnd
                        keyLast3 = keyName.substring(keyName.length() - 3);
                        keyLast5 = keyName.substring(keyName.length() - 5);
                        if (keyLast5.contains("Start")) {
                            datestart =  keyVal;
                            keyName1 = keyName.substring(0,keyName.length() - 5) + "End";
                            keyValTemp = args.get(keyName1);
                            dateend =  keyValTemp;
                        } else if (keyLast3.contains("End")) {
                            dateend =  keyVal;
                            keyName1 = keyName.substring(0,keyName.length() - 3) + "Start";
                            keyValTemp = args.get(keyName1);
                            datestart = keyValTemp;
                        } else {
                            datestart=null;
                            dateend=null;
                        }
                        criteria.add(Restrictions.between(column, datestart, dateend));
                    } else if (searchType.equals("or")) {
                        criteria.add(Restrictions.or(Restrictions.eq(column, keyVal)));
                    } else if (searchType.equals("like")) {
                        criteria.add(Restrictions.like(column, "%"+keyVal+"%"));
                    } else {
                        criteria.add(Restrictions.eq(column,keyVal));
                    }
                }else{
                    k++;
                    if (k==j){
                        criteria=null;
                    }
                }
                keyList.remove(i);
            }
            if (criteria!=null) {
                for (int i = 0; i < keyList.size(); i++) {
                    mp=(MappingColumn)keyList.get(i);
                    keyName=mp.getMapKey();
                    column=mp.getColumnName();
                    keyVal = args.get(keyName);//根据指定key取值
                    criteria = checkAddr(criteria, column, (String) keyVal);
                    criteria = checkOrg(criteria, column, (String) keyVal);
                }
            }
        }
        criteria = addNotNull(session,criteria);
        return criteria;
    }
	public AbstractSearchStragety getNextStragety(){
		return next;
	}

	public AbstractSearchStragety getPreviousStragey(){
		return previous;
	}

	public AbstractSearchStragety setNextStragety(AbstractSearchStragety stragety){
        if(stragety == this) return this;

        this.next = stragety;
        return this.next;
	}

	public AbstractSearchStragety setPreviousStragety(AbstractSearchStragety stragety){
        if(stragety == this) return this;

        this.previous = stragety;
        return this.previous;
	}
}
