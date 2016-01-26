package com.yihu.ehr.dict.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 常用字典项接口。因为实际只存在一个系统字典接口，所以使用时，字典对象是一个“伪字典”。
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:49
 */
@Service
@Transactional
public class ConventionalDictEntry {

    public ConventionalDictEntry() {
    }

    @Autowired
    private AppCatalogRepository appCatalogRepository;
    public AppCatalog getAppCatalog(String code) {
        return appCatalogRepository.getAppCatalog(code);
    }

    @Autowired
    private AppStatusRepository appStatusRepository;
    public AppStatus getAppStatus(String code) {
        return  appStatusRepository.getAppStatus(code);
    }

    @Autowired
    private GenderRepository genderRepository;
    public Gender getGender(String code) {
        return  genderRepository.getGender(code);
    }

    @Autowired
    private MartialStatusRepository martialStatusRepository;
    public MartialStatus getMartialStatus(String code) {
        return  martialStatusRepository.getMartialStatus(code);
    }

    @Autowired
    private NationRepository nationRepository;
    public Nation getNation(String code) {
        return  nationRepository.getNation(code);
    }

    @Autowired
    private ResidenceTypeRepository residenceTypeRepository;
    public ResidenceType getResidenceType(String code) {
        return  residenceTypeRepository.getResidenceType(code);
    }

    @Autowired
    private OrgTypeRepository orgTypeRepository;
    public OrgType getOrgType(String code) {
        return  orgTypeRepository.getOrgType(code);
    }

    @Autowired
    private SettledWayRepository settledWayRepository;
    public SettledWay getSettledWay(String code) {
        return  settledWayRepository.getSettledWay(code);
    }

    @Autowired
    private CardStatusRepository cardStatusRepository;
    public CardStatus getCardStatus(String code) {
        return  cardStatusRepository.getCardStatus(code);
    }

    @Autowired
    private CardTypeRepository cardTypeRepository;
    public CardType getCardType(String code) {
        return  cardTypeRepository.getCardType(code);
    }

    @Autowired
    RequestStateRepository requestStateRepository;
    public RequestState getRequestState(String code) {
        return  requestStateRepository.getRequestState(code);
    }

    @Autowired
    private KeyTypeRepository keyTypeRepository;
    public KeyType getKeyType(String code) {
        return  keyTypeRepository.getKeyType(code);
    }

    @Autowired
    private MedicalRoleRepository medicalRoleRepository;
    public MedicalRole getMedicalRole(String code) {
        return  medicalRoleRepository.getMedicalRole(code);
    }

    @Autowired
    private UserRoleRepository userRoleRepository;
    public UserRole getUserRole(String code) {
        return userRoleRepository.getUserRole(code);
    }

    @Autowired
    private UserTypeRepository userTypeRepository;
    public UserType getUserType(String code) {
        return  userTypeRepository.getUserType(code);
    }

    @Autowired
    private LoginAddressRepository loginAddressRepository;
    public LoginAddress getLoginAddress(String code){
        return  loginAddressRepository.getLoginAddress(code);
    }

    //...............................................................
    public List<UserType> getUserTypeList(){
        return  userTypeRepository.getUserTypeList();
    }


    @Autowired
    private TagsRepository tagsRepository;
    public List<Tags> getTagsList(){
        return  tagsRepository.getTagList();
    }
    //...............................................................

    @Autowired
    private YesNoRepository yesNoRepository;
    public YesNo getYesNo(boolean code) {
        String resultCode = "";
        resultCode = code ? "True" : "False";
        return  yesNoRepository.getYesNo(resultCode);
    }


    @Autowired
    private AdapterTypeRepository adapterTypeRepository;
    public AdapterType getAdapterType(String code) {
        return adapterTypeRepository.getAdapterType(code);
    }

    @Autowired
    private StdSourceTypeRepository stdSourceTypeRepository;
    public StdSourceType getStdSourceType(String code) {
        return  stdSourceTypeRepository.getStdSourceType(code);
    }


    public List<StdSourceType> getStdSourceTypeList(String[] codes) {
        return  stdSourceTypeRepository.getStdSourceTypeList(codes);
    }
}