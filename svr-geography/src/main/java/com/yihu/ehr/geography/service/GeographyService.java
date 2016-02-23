package com.yihu.ehr.geography.service;


import com.yihu.ehr.geography.dao.XGeographyDictRepository;
import com.yihu.ehr.geography.dao.XGeographyRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址管理。对于相同地址，仅保存一份。
 * @author zqb
 * @version 1.0
 * @created 30-六月-2015 19:17:43
 */
@Transactional
@Service
public class GeographyService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XGeographyRepository addressRepository;

    @Autowired
    private XGeographyDictRepository addressDictRepository;


	public List<GeographyDict> getLevelToAddr(Integer level){
        List<GeographyDict> addressDictList = addressDictRepository.getAddrDictByLevel(level);
        return addressDictList;
	}

	public List<GeographyDict> getPidToAddr(Integer pid){
        List<GeographyDict> addressDictList = addressDictRepository.getAddrDictByPid(pid);
        return addressDictList;
	}

    /**
	 * 地址检查并保存
	 * @param address
	 */
	public String saveAddress(Geography address) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = session.createCriteria(Geography.class);

        String country = address.getCountry();
        String province = address.getProvince();
        String city = address.getCity();
        String district = address.getDistrict();
        String town = address.getTown();
        String street = address.getStreet();
        String extra = address.getExtra();
        String townnew;
        String streetnew;
        String extranew;

        if (isNullAddress(address)) {
            return null;
        }
        if (country == null) {
            address.setCountry("中国");
        }
        if (province == null) {
            List<Geography> addressList = addressRepository.findAddressListByCountry(country);
            criteria.add(Restrictions.eq("country", country));
            List<Geography> addr = criteria.list();
            if (!addressList.isEmpty()) {
                address = addressList.get(0);
                return address.getId();
            } else {
                addressRepository.save(address);
                return address.getId();
            }
        }

        criteria.add(Restrictions.eq("province", province));
        criteria.add(Restrictions.eq("city", city));
        if (district != null) {
            criteria.add(Restrictions.eq("district", district));
        }

        if (town == null && street == null && extra == null) {
            criteria.add(Restrictions.isNull("town"));
            criteria.add(Restrictions.isNull("street"));
            criteria.add(Restrictions.isNull("extra"));
            List<Geography> addr = criteria.setFetchSize(1).list();
            if (!addr.isEmpty()) {
                address = addr.get(0);
                return address.getId();
            } else {
                addressRepository.save(address);
            }

        } else {
            List<Geography> addrs = criteria.list();
            for (Geography addr : addrs) {
                townnew = addr.getTown();
                streetnew = addr.getStreet();
                extranew = addr.getExtra();
                if ((townnew == null ? "" : townnew).equals(town == null ? "" : town)) {
                    if ((streetnew == null ? "" : streetnew).equals(street == null ? "" : street)) {
                        if ((extranew == null ? "" : extranew).equals(extra == null ? "" : extra)) {
                            return addr.getId();
                        }
                    }
                }
            }
            addressRepository.save(address);
            return address.getId();
        }
        return address.getId();
    }

    public boolean isNullAddress(Geography geography) {
        return geography.getProvince() == null
                && geography.getCity() == null
                && geography.getDistrict() == null
                && geography.getTown() == null
                && geography.getCountry() == null
                && geography.getStreet() == null;
    }


    public Geography getAddressById(String Id) {
        return addressRepository.findOne(Id);
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
        addressRepository.delete(address);
    }
}