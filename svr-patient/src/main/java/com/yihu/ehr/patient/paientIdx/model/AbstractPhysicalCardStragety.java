package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.AbstractPhysicalCard;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;


/**
 * Created by zqb on 2015/6/29.
 */
public abstract class AbstractPhysicalCardStragety extends AbstractCardStragety{
    @Override
    public  String addColumnList(String inList){
        //添加特定条件，用类字段表示,要检索的内容
        inList +=",'releaseOrg','releaseDate','local'";
        return inList;
    }
    @Override
    public String getTableName(){
        return "'AbstractPhysicalCard'";
    }
    @Override
    public String getAddrList(String addrList){
        //卡相关地址,用类字段表示
        addrList="local";
        return addrList;
    }
    @Override
    public String getOrgrList(String org){
        //卡相关机构,用类字段表示
        org="releaseOrg";
        return org;
    }
    @Override
    public Criteria checkAddr(Criteria criteria,String column,String keyVal){
        //地址检查,默认卡相关地址
        String addr=null;
        List<AbstractPhysicalCard> cards = criteria.list();
        for (int j=cards.size() -1,i = j; i>=0; i--) {
            if (column.equals("local")) {
                //// TODO: 2016/1/21 调用地址服务 
                //addr = cards.get(i).getLocal().getCanonicalAddress();//取出当前记录的地址信息
                //判断地址是否有匹配的
                if (!addr.contains(keyVal)) {
                    //地址不包含的移除记录
                    criteria.add(Restrictions.not(Restrictions.eq(column, cards.get(i).getLocal())));
                }
            }else{
                continue;
            }
        }
        return criteria;
    }
    @Override
    public Criteria checkOrg(Criteria criteria,String column,String keyVal){
        //机构检查,默认卡发布机构
        String org=null;
        List<AbstractPhysicalCard> cards = criteria.list();
        for (int j=cards.size() -1,i = j; i>=0; i--) {
            if (column.equals("releaseOrg")) {
                if (cards.get(i).getReleaseOrg()!=null) {
                    //// TODO: 2016/1/21  调用机构服务
                    //org = cards.get(i).getReleaseOrg().getFullName();//取出当前记录的机构名
                    if (!org.contains(keyVal)) {
                        //机构不包含的移除记录
                        criteria.add(Restrictions.not(Restrictions.eq(column,cards.get(i).getReleaseOrg())));
                    }
                }
            }else{
                continue;
            }
        }
        return criteria;
    }

}
