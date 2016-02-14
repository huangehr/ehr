package com.yihu.ehr.patient.service.card;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.patient.dao.XAbstractPhysicalCardRepository;
import com.yihu.ehr.patient.dao.XAbstractVirtualCardRepository;
import com.yihu.ehr.patient.feignClient.AddressClient;
import com.yihu.ehr.patient.feignClient.ConventionalDictClient;
import com.yihu.ehr.patient.service.demographic.DemographicId;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 卡管理实现类.
 *
 * @author Sand
 * @version 1.0
 * @created 25-5月-2015 17:47:57
 */
@Transactional
@Service
public class CardManager {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private AddressClient addressClient;

    @Autowired
    private XAbstractPhysicalCardRepository abstractPhysicalCardRepository;

    @Autowired
    private XAbstractVirtualCardRepository abstractVirtualCardRepository;

    public CardManager() {
    }

    public static Class getCardImplemention(MConventionalDict cardType) {
        switch (cardType.getCode()) {
            case "HealthCard":
                return HealthCard.class;

            case "MediCard":
                return MediCard.class;

            case "AICard":
                return AICard.class;

            case "TempCard":
                // TODO: 临时卡需要特定逻辑
                return null;

            case "Passport":
                return Passport.class;

            case "IdCard":
                return IdCard.class;

            case "OfficersCard":
                return OfficersCard.class;

            case "DriverLicence":
                return DriverLisence.class;

            case "NCMS":
                return NCMS.class;

            case "SinaWeibo":
                return SNSAccount.class;
            case "TecentWeixin":
                return SNSAccount.class;
            case "Alipay":
                return SNSAccount.class;

            default:
                return null;
        }
    }

    public AbstractCard registerCard(MConventionalDict cardType, String no, String ownerName, SNSPlatform platform) {
        Class cls = getCardImplemention(cardType);
        if (cls == null) throw new CardException("未知的卡类型.");

        AbstractCard card = new AbstractCard();
        card.setNumber(no);
        card.setOwnerName(ownerName);

        if (card instanceof SNSAccount) {
            SNSAccount snsAccount = (SNSAccount) card;
            snsAccount.setCardType(cardType.getCode());
            snsAccount.setPlatform(platform);
        }

        entityManager.unwrap(org.hibernate.Session.class).save(card);
        return card;
    }

    public boolean isCardRegistered(String number, MConventionalDict type, MAddress address, SNSPlatform platform) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);

        Criteria criteria = null;
        if (type.checkIsVirtualCard()) {
            criteria = session.createCriteria(AbstractVirtualCard.class);
        } else {
            criteria = session.createCriteria(AbstractPhysicalCard.class);
        }

        criteria.add(Restrictions.eq("number", number));
        criteria.add(Restrictions.eq("type", type));

        List<AbstractCard> cards = criteria.list();
        if (cards.size() == 0) return false;

        // 根据地址/平台来判断
        for (AbstractCard card : cards) {
            if (card.checkIsVirtualCard()) {
                AbstractVirtualCard virtualCard = (AbstractVirtualCard) card;
                if (card.checkIsVirtualCard() && platform != null) {
                    SNSAccount snsAccount = (SNSAccount) virtualCard;
                    if (snsAccount.getPlatform().getHomePage().equals(platform.getHomePage())) {
                        return true;
                    }
                }
            }else{
                AbstractPhysicalCard physicalCard = (AbstractPhysicalCard) card;
//                if (address != null &&
//                        physicalCard.getLocal() != null && physicalCard.getLocal().isAvailable() &&
//                        physicalCard.getLocal().getProvince().equals(address.getProvince()) &&
//                        physicalCard.getLocal().getCity().equals(address.getCity())) {
//                    return true;
//                }
            }
        }
        return false;
    }

    public void updateCard(AbstractCardModel card) {
        if (card.getNumber().length() == 0 || card.getType() == null) {
            throw new CardException("卡信息不全, 无法更新");
        } else if ("Invalid".equals(card.getStatus())) {
            throw new CardException("卡已作废, 无法更新");
        }
        if (!card.checkIsVirtualCard()) {
            MAddress local = card.getLocal();

            String country = local.getCountry();
            String province = local.getProvince();
            String city = local.getCity();
            String district = local.getDistrict();
            String town = local.getTown();
            String street = local.getStreet();


            if (!addressClient.isNullAddress(country,province,city,district,town,street)) {

                String extra = local.getExtra();
                String postalCode = local.getPostalCode();
                addressClient.saveAddress(country,province,city,district,town,street,extra,postalCode);
                //retrieve set
                AbstractPhysicalCard abstractPhysicalCard = new AbstractPhysicalCard();
                abstractPhysicalCard.setId(card.getId());
                abstractPhysicalCard.setLocal(card.getLocal().getId());
                abstractPhysicalCard.setReleaseDate(card.getReleaseDate());

            }
        }

        entityManager.unwrap(org.hibernate.Session.class).save(card);
    }

    public void deleteCard(AbstractCard card) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        session.delete(card);
        //地址不做删除
    }

    public AbstractCard getCard(String id, String cardType) {
        AbstractCard card = null;
        AbstractPhysicalCard abstractPhysicalCard = new AbstractPhysicalCard();
        AbstractCard abstractCard = new AbstractCard();
        abstractCard.setType(cardType);
        if (!abstractCard.checkIsVirtualCard()) {
            card = abstractPhysicalCardRepository.findOne(id);
        } else {
            card = abstractVirtualCardRepository.findOne(id);
        }
        return card;
    }

    public CardModel getCard(AbstractCard card) {
        CardModel cardModel = new CardModel();
        cardModel.setTypeValue(conventionalDictClient.getCardType(card.getType()).getValue());
        cardModel.setNumber(card.getNumber());
        cardModel.setOwnerName(card.getOwnerName());
        cardModel.setCreateDate((new SimpleDateFormat("yyyy-MM-dd")).format(card.getCreateDate()));
        cardModel.setStatusValue(conventionalDictClient.getCardStatus(card.getStatus()).getValue());
        cardModel.setDescription(card.getDescription());
        return cardModel;
    }

    public AbstractCard[] getAttachedCards(DemographicId demographicId) {
        if (!demographicId.idAvailable()) {
            throw new IllegalArgumentException("无效人口学索引.");
        }

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = null;
        List<AbstractCard> cards = null;

        criteria = session.createCriteria(AbstractPhysicalCard.class);
        criteria.add(Restrictions.eq("demographicId", demographicId));
        cards = criteria.list();

        criteria = session.createCriteria(AbstractVirtualCard.class);
        criteria.add(Restrictions.eq("demographicId", demographicId));
        cards.addAll(criteria.list());

        return cards.toArray(new AbstractCard[cards.size()]);
    }

    public List<CardBrowseModel> searchCardBrowseModel(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String idCardNo = (String) args.get("idCardNo");
        String number = (String)args.get("number");
        String searchType = (String) args.get("type");
        String searchIdCardNo = (String)args.get("searchIdCardNo");
        int rows = (Integer)args.get("rows");
        int page = (Integer)args.get("page");
        DemographicId demographicId=null;
        MConventionalDict type=null;

        String sqlPhysical="from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="from AbstractVirtualCard a where (a.number like :number)";
        if (!StringUtils.isEmpty(idCardNo)){
            demographicId = new DemographicId(idCardNo);
            sqlPhysical += " and (demographicId=:demographicId)";
            sqlVirtual += " and (demographicId=:demographicId)";
        }
        if (!StringUtils.isEmpty(searchType)){
            type = conventionalDictClient.getCardType(searchType);
            sqlPhysical += " and (type=:type)";
            sqlVirtual += " and (type=:type)";
        }
        if (!StringUtils.isEmpty(searchIdCardNo)){
            sqlPhysical += " and (demographicId=null or trim(demographicId)='')";
            sqlVirtual += " and (demographicId=null or trim(demographicId)='')";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("demographicId", demographicId);
            queryVirtual.setParameter("demographicId", demographicId);
        }
        if (!StringUtils.isEmpty(searchType)){
            queryPhysical.setParameter("type", type);
            queryVirtual.setParameter("type", type);
        }


        List<AbstractCard> cards = null;
        queryPhysical.setMaxResults(rows);
        queryPhysical.setFirstResult((page - 1) * rows);
        cards = queryPhysical.list();
        int physicalCount = searchCardInt(args, true);
        int first = 0;
        if(cards.size()<rows){
            int left = page * rows - physicalCount;
            if(cards.size()==0){
                page = left/rows;
                int tmp = physicalCount%rows;
                first = tmp == 0 ? (page - 1) * rows : rows - tmp;
            }
            else{
                page = 1;
                rows = left>rows?rows:left;
                first = (page - 1) * rows;
            }
            queryVirtual.setMaxResults(rows);
            queryVirtual.setFirstResult(first);
            cards.addAll(queryVirtual.list());
        }

        List<CardBrowseModel> cardBrowseModelList = new ArrayList<>();
        Integer order = 1;
        for(AbstractCard card:cards){
            AbstractVirtualCard virtualCard=null;
            AbstractPhysicalCard physicalCard=null;
            if (card.checkIsVirtualCard()){
                virtualCard=(AbstractVirtualCard)card;
            }else{
                physicalCard=(AbstractPhysicalCard)card;
//                if (physicalCard.getReleaseOrg()!=null){
//                    physicalCard.getReleaseOrg().getFullName();
//                }
            }
            CardBrowseModel cardBrowseModel = new CardBrowseModel();
            cardBrowseModel.setObjectId(card.getId());
            cardBrowseModel.setOrder(order++);
            String cardType = card.getType();
            if (cardType!=null){
                cardBrowseModel.setType(cardType);
                cardBrowseModel.setTypeValue(conventionalDictClient.getCardType(cardType).getValue());
            }
            cardBrowseModel.setNumber(card.getNumber());
            cardBrowseModel.setCreateDate((new SimpleDateFormat("yyyy-MM-dd")).format(card.getCreateDate()));
            cardBrowseModel.setStatus(card.getStatus());
            cardBrowseModel.setStatusValue(conventionalDictClient.getCardStatus(card.getStatus()).getValue());

            cardBrowseModelList.add(cardBrowseModel);
        }

        return cardBrowseModelList;
    }

    public Integer searchCardInt(Map<String, Object> args ,boolean onlyPhysical) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String idCardNo = (String) args.get("idCardNo");
        String number = (String)args.get("number");
        String searchType = (String) args.get("type");
        String searchIdCardNo = (String)args.get("searchIdCardNo");
        DemographicId demographicId=null;
        MConventionalDict type=null;

        String sqlPhysical="select 1 from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="select 1 from AbstractVirtualCard a where (a.number like :number)";
        if (!StringUtils.isEmpty(idCardNo)){
            demographicId = new DemographicId(idCardNo);
            sqlPhysical += " and (demographicId=:demographicId)";
            sqlVirtual += " and (demographicId=:demographicId)";
        }
        if (!StringUtils.isEmpty(searchType)){
            type = conventionalDictClient.getCardType(searchType);
            sqlPhysical += " and (type=:type)";
            sqlVirtual += " and (type=:type)";
        }
        if (!StringUtils.isEmpty(searchIdCardNo)){
            sqlPhysical += " and (demographicId=null or trim(demographicId)='')";
            sqlVirtual += " and (demographicId=null or trim(demographicId)='')";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("demographicId", demographicId);
            queryVirtual.setParameter("demographicId", demographicId);
        }
        if (!StringUtils.isEmpty(searchType)){
            queryPhysical.setParameter("type", type);
            queryVirtual.setParameter("type", type);
        }
        if(onlyPhysical)return  queryPhysical.list().size();
        return queryPhysical.list().size()+queryVirtual.list().size();

    }

    public boolean attachCardWith(AbstractCard card, DemographicId demographicId) {
        if (!demographicId.idAvailable()) {
            throw new IllegalArgumentException("无效人口学索引.");
        }

        card.setDemographicId(demographicId);

        entityManager.unwrap(org.hibernate.Session.class).save(card);
        return card.getDemographicId()!=null;
    }

    public boolean isCardAttachedWith(AbstractCard card, DemographicId demographicId) {
        if (!demographicId.idAvailable()) {
            throw new IllegalArgumentException("无效人口学索引.");
        }

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = null;

        if (card.checkIsVirtualCard()) {
            criteria = session.createCriteria(AbstractVirtualCard.class);
        } else {
            criteria = session.createCriteria(AbstractPhysicalCard.class);
        }

        criteria.add(Restrictions.eq("id", card.getId()));
        criteria.add(Restrictions.eq("demographicId", demographicId));

        return criteria.list().size() > 0;
    }

    public boolean detachCard(AbstractCard card) {
        if (card.getDemographicId() == null) {
            return true;
        }

        card.setDemographicId(null);
        entityManager.unwrap(org.hibernate.Session.class).save(card);
        return card.getDemographicId()==null;
    }
}