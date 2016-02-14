package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.patient.feignClient.AddressClient;
import com.yihu.ehr.patient.feignClient.ConventionalDictClient;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.PatientBrowseModel;
import com.yihu.ehr.patient.service.demographic.PatientModel;
import com.yihu.ehr.util.encode.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
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
    public DemographicInfo createDemographicInfo(String idCardNo, String name) {
//        if (!id.isAvailable()) {
//            throw new IllegalArgumentException("人口学索引无效.");
//        }

        if (name == null || name.length() <= 1) {
            throw new IllegalArgumentException("姓名不能少于一个字符");
        }

        DemographicInfo demoInfo = new DemographicInfo();
        demoInfo.setIdCardNo(idCardNo);
        demoInfo.setName(name);

        return demoInfo;
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
    public boolean updatePatient(PatientModel patientModel) throws Exception{
        DemographicInfo demographicInfo = null;
        boolean isRegistered = false;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;
        isRegistered = isRegistered(new DemographicId(patientModel.getIdCardNo()));
        if (!isRegistered) {
            demographicInfo = createDemographicInfo(patientModel.getIdCardNo(), patientModel.getName());
            //demographicInfo = createDemographicInfo(new DemographicId(patientModel.getIdCardNo()), patientModel.getName());
        } else {
            demographicInfo = getDemographicInfo(new DemographicId(patientModel.getIdCardNo()));
        }
        if (patientModel.getGender() != null && !patientModel.getGender().isEmpty()) {
            demographicInfo.setGender(patientModel.getGender());
        }
        if (patientModel.getNation() != null && !patientModel.getNation().isEmpty()) {
            demographicInfo.setNation(patientModel.getNation());
        }
        demographicInfo.setNativePlace(patientModel.getNativePlace());
        if (patientModel.getMartialStatus() != null && !patientModel.getMartialStatus().isEmpty()) {
            demographicInfo.setMartialStatus(patientModel.getMartialStatus());
        }
        date = patientModel.getBirthday();
        if (date != null && !date.isEmpty()) {
            demographicInfo.setBirthday(format.parse(date));
        }
        demographicInfo.setBirthPlace(patientModel.getBirthPlace().getId());
        demographicInfo.setHomeAddress(patientModel.getHomeAddress().getId());
        demographicInfo.setWorkAddress(patientModel.getWorkAddress().getId());
        if (patientModel.getResidenceType() != null && !patientModel.getResidenceType().isEmpty()) {
            demographicInfo.setResidenceType(patientModel.getResidenceType());
        }
        if(patientModel.getPassword()!=null&&!patientModel.getPassword().equals("")){
            demographicInfo.setPassword(patientModel.getPassword());
        }
        //TODO：Map类型的电话号码还要处理，如何展示问题
        demographicInfo.setTelphoneNo(patientModel.getTel());
        demographicInfo.setEmail(patientModel.getEmail());

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
    public List<PatientBrowseModel> searchPatientBrowseModel(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String name = (String) args.get("name");
        String idCardNo = (String) args.get("idCardNo");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

//        String province = (String) args.retrieve("province");
//        String city = (String) args.retrieve("city");
//        String district = (String) args.retrieve("district");

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

        query.setString("name", "%" + name + "%");
        query.setString("idCardNo", "%" + idCardNo + "%");
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<DemographicInfo> demographicInfos = query.list();

        List<PatientBrowseModel> patientBrowseModelList = new ArrayList<>();
        Integer order = 1;

        for (DemographicInfo demoinfo : demographicInfos) {
            PatientBrowseModel patientBrowseModel = new PatientBrowseModel();
            patientBrowseModel.setOrder(order++);
            patientBrowseModel.setName(demoinfo.getName());
            patientBrowseModel.setIdCardNo(demoinfo.getIdCardNo().toString());
            if (demoinfo.getGender() != null) {
                patientBrowseModel.setGender(demoinfo.getGender());
                patientBrowseModel.setGenderValue(conventionalDictClient.getGender(demoinfo.getGender()).getValue());
            } else {
                patientBrowseModel.setGenderValue("");
            }
            patientBrowseModel.setTel(demoinfo.getTelphoneNo());
            patientBrowseModel.setHomeAddress(demoinfo.getHomeAddress());

            patientBrowseModelList.add(patientBrowseModel);
        }

        return patientBrowseModelList;
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