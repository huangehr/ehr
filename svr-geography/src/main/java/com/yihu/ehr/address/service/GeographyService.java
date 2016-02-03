package com.yihu.ehr.address.service;


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
import java.util.ArrayList;
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

	public GeographyService(){}

    @Transactional(Transactional.TxType.SUPPORTS)
    public GeographyDict createAddrDict() {
        GeographyDict addressDict = new GeographyDict();
        return addressDict;
    }

    public void deleteAddrDict(int id) {
        addressDictRepository.delAddressDict(id);
    }

    /**
     * 根据地址字典id获取地址字典
     * @param id
     * @return
     */
	public GeographyDict getAddrDict(String id){
        GeographyDict addressDict = addressDictRepository.getAddressDictById(id);
        return addressDict;
	}

    /**
     * 根据地址id获取地址
     * @param id
     * @return
     */
    public Geography getAddr(String id) {
        return addressRepository.findOne(id);
    }

	public List<GeographyDict> getLevelToAddr(Integer level){
        List<GeographyDict> addressDictList = addressDictRepository.getAddrDictByLevel(level);
        return addressDictList;
	}

	public List<GeographyDict> getPidToAddr(Integer pid){
        List<GeographyDict> addressDictList = addressDictRepository.getAddrDictByPid(pid);
        return addressDictList;
	}

    public GeographyDict saveAddrDict(GeographyDict addressDict) {
        addressDictRepository.save(addressDict);
        return addressDict;
    }

    /**
	 * 地址检查并保存
	 * @param address
	 */
    @Transactional(Transactional.TxType.REQUIRED)
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

    /**
     * 传入一个id，获取最上级到此地址的完整地址（如：传入厦门的id，返回address中国、福建省、厦门市）
     * @param id
     * @param address
     * */
    public Geography getFullAddress(Integer id, Geography address){
        if (address==null){
            address=new Geography();
        }
        GeographyDict addressDict = getAddrDict(id.toString());
        int level = addressDict.getLevel();
        switch (level) {
            case 0: address.setCountry(addressDict.getName());break;
            case 1: address.setProvince(addressDict.getName()); break;
            case 2: address.setCity(addressDict.getName()); break;
            case 3: address.setDistrict(addressDict.getName()); break;
            case 4: address.setTown(addressDict.getName()); break;
            case 5: address.setStreet(addressDict.getName()); break;
            default:
        }
        String postCode = addressDict.getPostcode();
        if(StringUtils.isEmpty(address.getPostalCode()) && !StringUtils.isEmpty(postCode)) {
            address.setPostalCode(postCode);
        }
        if (level>0){
            getFullAddress(addressDict.getPid(),address);
        }
        return address;
    }

    public boolean isNullAddress(Geography address) {
        return address.getProvince() == null
                && address.getCity() == null
                && address.getDistrict() == null
                && address.getTown() == null
                && address.getCountry() == null
                && address.getStreet() == null;
    }

    /**
     *  地址从字典导入
     */
    public String[] importDict(){
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        List<String> newAddr=new ArrayList<>();
        String hql = " from AddressDict a where a.id not in (select a.id from AddressDict b where a.id=b.pid)";
        Query query = session.createQuery(hql);
        List<GeographyDict> addressDictList =query.list();
        for (GeographyDict loop:addressDictList){
            newAddr.add(saveAddress(getFullAddress(loop.getId(), null)));
        }
        return newAddr.toArray(new String[newAddr.size()]);
    }

	public void updateAddrDict(GeographyDict addrdict){
		if (addrdict.getName()==null){
			throw new GeographyException("名称不能为空");
		}
        addressDictRepository.save(addrdict);
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




    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteAddres(Geography address) {
        addressRepository.delete(address);
    }
}