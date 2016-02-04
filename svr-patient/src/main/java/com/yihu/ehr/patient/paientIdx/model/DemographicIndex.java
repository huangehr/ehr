package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.patient.feign.AddressClient;
import com.yihu.ehr.patient.feign.ConventionalDictClient;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.PatientBrowseModel;
import com.yihu.ehr.patient.service.demographic.PatientModel;
import com.yihu.ehr.util.encode.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 人口学索引实现类.
 *
 * @author Sand
 * @version 1.0
 * @created 04-6月-2015 19:53:04
 */
@Transactional
@Service
public class DemographicIndex{

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XDemographicInfoRepository demographicInfoRepository;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private AddressClient addressClient;

    @Autowired
    private DemographicIndexStragety stragety;

    public DemographicIndex() {
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isRegistered(DemographicId id) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String hql = "select count(*) from DemographicInfo where id.idCardNo=:id";
        Query query = session.createQuery(hql);
        query.setString("id", id.idCardNo);

        return Integer.parseInt(query.uniqueResult().toString()) > 0;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public DemographicInfo getDemographicInfo(DemographicId id) {
        DemographicInfo demInfo = demographicInfoRepository.findOne(id);
        return demInfo;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PatientModel getPatient(DemographicInfo demoinfo) {
        PatientModel patientModel = new PatientModel();

        patientModel.setName(demoinfo.getName());
        patientModel.setIdCardNo(demoinfo.getIdCardNo().toString());
        if (demoinfo.getGender() != null) {
            patientModel.setGender(demoinfo.getGender());
        }
        if (demoinfo.getNation() != null) {
            patientModel.setNation(demoinfo.getNation());
        }
        patientModel.setNativePlace(demoinfo.getNativePlace());
        if (demoinfo.getMartialStatus() != null) {
            patientModel.setMartialStatus(demoinfo.getMartialStatus());
        }
        if (demoinfo.getBirthday() != null) {
            patientModel.setBirthday((new SimpleDateFormat("yyyy-MM-dd")).format(demoinfo.getBirthday()));
        }
        if(demoinfo.getBirthPlace()!=null){
            try{
                patientModel.setBirthPlace(addressClient.getAddressById(demoinfo.getBirthPlace()));
                patientModel.setBirthPlaceFull(addressClient.getCanonicalAddress(demoinfo.getBirthPlace()));
            }catch (Exception e){
                System.out.println(e);
            }

        }
        if(demoinfo.getHomeAddress()!=null){
            try{
                patientModel.setHomeAddress(addressClient.getAddressById(demoinfo.getHomeAddress()));
                patientModel.setHomeAddressFull(addressClient.getCanonicalAddress(demoinfo.getHomeAddress()));
            }catch (Exception e){
                System.out.println(e);
            }

        }
        if(demoinfo.getWorkAddress()!=null){
            try{
                patientModel.setWorkAddress(addressClient.getAddressById(demoinfo.getWorkAddress()));
                patientModel.setWorkAddressFull(addressClient.getCanonicalAddress(demoinfo.getWorkAddress()));
            }catch (Exception e){
                System.out.println(e);
            }

        }
        if (demoinfo.getResidenceType() != null) {
            patientModel.setResidenceType(demoinfo.getResidenceType());
        }
        patientModel.setTel(demoinfo.getTelphoneNo());
        patientModel.setEmail(demoinfo.getEmail());

        return patientModel;
    }

    public void save(DemographicInfo demoInfo) {
        DemographicInfo di = (DemographicInfo) demoInfo;

        //地址检查并保存
        MAddress xlocation = null;
        //出生地
        xlocation = addressClient.getAddressById( demoInfo.getBirthPlace());
        if (!isNullAddress(xlocation)) {
            String addressId = savaAddress(xlocation);
            demoInfo.setBirthPlace(addressId);
        }else{
            demoInfo.setBirthPlace(null);
        }
        //工作地址
        xlocation = addressClient.getAddressById(demoInfo.getWorkAddress());
        if (!isNullAddress(xlocation)) {
            String addressid = savaAddress(xlocation);
            demoInfo.setWorkAddress(addressid);
        }else{
            demoInfo.setWorkAddress(null);
        }
        //家庭地址
        xlocation = addressClient.getAddressById(demoInfo.getHomeAddress());
        if (!isNullAddress(xlocation)) {
            String addressId = savaAddress(xlocation);
            demoInfo.setHomeAddress(addressId);
        }else{
            demoInfo.setHomeAddress(null);
        }
        demographicInfoRepository.save(demoInfo);
    }



    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean updatePatient(DemographicInfo demographicInfo) throws Exception{
        boolean isRegistered  = isRegistered(new DemographicId(demographicInfo.getIdCardNo()));
        if (isRegistered) {
            String pwd = "123456";
            demographicInfo.setPassword(HashUtil.hashStr(pwd));
        }
        save(demographicInfo);
        return true;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public DemographicInfo[] search(Map<String, Object> args) {


        return stragety.search(args);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DemographicInfo> searchPatientBrowseModel(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String name = (String) args.get("name");
        String idCardNo = (String) args.get("idCardNo");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");
        List<String> addressIdList = addressClient.search(province,city,district);

        String hql = "from DemographicInfo where (name like :name or id like :idCardNo)";

        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            hql += " and homeAddress in (:addressIdList)";
        }
        Query query = session.createQuery(hql);

        query.setString("name", "%" + name + "%");
        query.setString("idCardNo", "%" + idCardNo + "%");

        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) &&!StringUtils.isEmpty(district)) {
            query.setParameterList("addressIdList", addressIdList);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();


        return demographicInfos;
    }

    public Integer searchPatientInt(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String name = (String) args.get("name");
        String idCardNo = (String) args.get("idCardNo");

        String province = (String) args.get("province");
        String city = (String) args.get("city");
        String district = (String) args.get("district");

        String hql = "from DemographicInfo where (name like :name or id like :idCardNo)";
//        if (!StringUtils.isEmpty(province)) {
//            hql += " and homeAddress.province = :province";
//        }
//        if (!StringUtils.isEmpty(city)) {
//            hql += " and homeAddress.city = :city";
//        }
//        if (!StringUtils.isEmpty(district)) {
//            hql += " and homeAddress.district = :district";
//        }

        Query query = session.createQuery(hql);
//        if (!StringUtils.isEmpty(province)) {
//            query.setString("province", province);
//        }
//        if (!StringUtils.isEmpty(city)) {
//            query.setString("city", city);
//        }
//        if (!StringUtils.isEmpty(district)) {
//            query.setString("district", district);
//        }

        //Query query = session.createQuery("select 1 from DemographicInfo where name like :name or id like :idCardNo");
        query.setString("name", "%" + name + "%");
        query.setString("idCardNo", "%" + idCardNo + "%");

        return query.list().size();
    }

    public Boolean isNullAddress(MAddress address){

        String country = address.getCountry();
        String province = address.getProvince();
        String city = address.getCity();
        String district = address.getDistrict();
        String town = address.getTown();
        String street = address.getStreet();
        return addressClient.isNullAddress(country,province,city,district,town,street);
    }


    public String savaAddress(MAddress address){
        String country = address.getCountry();
        String province = address.getProvince();
        String city = address.getCity();
        String district = address.getDistrict();
        String town = address.getTown();
        String street = address.getStreet();
        String extra = address.getExtra();
        String postalCode = address.getPostalCode();
        return addressClient.saveAddress(country,province,city,district,town,street,extra,postalCode);
    }




    @Transactional(Transactional.TxType.SUPPORTS)
    public void delete(DemographicId id) {
        DemographicInfo di = (DemographicInfo) getDemographicInfo(id);
        demographicInfoRepository.delete(di);
    }

    public void resetPass(DemographicId id) {

        String pwd = "123456";
        DemographicInfo demInfo = getDemographicInfo(id);
        demInfo.setPassword(HashUtil.hashStr(pwd));
        demographicInfoRepository.save(demInfo);
    }
}