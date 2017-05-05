package com.yihu.ehr.geography.service;


import com.yihu.ehr.entity.geography.Geography;
import com.yihu.ehr.geography.dao.XGeographyRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 地址管理。对于相同地址，仅保存一份。
 * @author zqb
 * @version 1.0
 * @created 30-六月-2015 19:17:43
 */
@Transactional
@Service
public class GeographyService extends BaseJpaService<Geography,XGeographyRepository>{

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XGeographyRepository geographyRepository;


    /**
	 * 地址检查并保存
     * 返回新地址id
	 * @param geography
	 */
    public String saveAddress(Geography geography) {
        return geographyRepository.save(geography).getId();
    }

    public boolean isNullAddress(Geography geography) {
        return
                StringUtils.isEmpty(geography.getProvince())
                && StringUtils.isEmpty(geography.getCity())
                && StringUtils.isEmpty(geography.getDistrict())
                && StringUtils.isEmpty(geography.getTown())
                && StringUtils.isEmpty(geography.getCountry())
                && StringUtils.isEmpty(geography.getStreet());
    }


    public Geography getAddressById(String Id) {
        return geographyRepository.findOne(Id);
    }

    public String getCanonicalAddress(Geography address) {
        String addressStr = "";
        String province = address.getProvince();
        String city = address.getCity();
        String district = address.getDistrict();
        String town = address.getTown();
        String street = address.getStreet();
        String extra = address.getExtra();
        if (!StringUtils.isEmpty(province)){
            addressStr += province;
            if (!"".equals(city)) {
                if (!province.equals(city)){
                    addressStr += city;
                }
            }
        }
        if (!StringUtils.isEmpty(district)){
            addressStr += district;
        }
        if (!StringUtils.isEmpty(town)){
            addressStr += town;
        }
        if (!StringUtils.isEmpty(street)){
            addressStr += street;
        }
        if (!StringUtils.isEmpty(extra)){
            addressStr += extra;
        }
        return addressStr;
    }

    public List<String> search(String province, String city, String district) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String hql =
                "SELECT " +
                "addresses.id " +
                "FROM " +
                "addresses " +
                "WHERE 1=1 ";
        if (!StringUtils.isEmpty(province)) {
            hql += " AND addresses.province = '"+province+"'";
        }
        if (!StringUtils.isEmpty(city)) {
            hql += " AND addresses.city = '"+city+"'";
        }
        if (!StringUtils.isEmpty(district)) {
            hql += " AND addresses.district = '"+district+"'";
        }

        Query query = session.createSQLQuery(hql);
        List<String> idList = query.list();
        return idList;

    }

    public void deleteAddress(Geography address) {
        geographyRepository.delete(address);
    }

    public List<Geography> isGeographyExist(Geography geography) throws Exception {
        String hql = PoUtil.getHql(geography,"id","postalCode");
        Session session = currentSession();
        Query query = session.createQuery(hql);
        List<Geography> list = query.list();
        return list;
    }
}