package com.yihu.ehr.patient.service.card;

import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.patient.dao.XAbstractPhysicalCardRepository;
import com.yihu.ehr.patient.dao.XAbstractVirtualCardRepository;
import com.yihu.ehr.patient.feign.GeographyClient;
import com.yihu.ehr.patient.feign.ConventionalDictClient;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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
    private XAbstractPhysicalCardRepository abstractPhysicalCardRepository;

    @Autowired
    private XAbstractVirtualCardRepository abstractVirtualCardRepository;

    public AbstractCard getCard(String apiVersion,String id, String cardType) {
        AbstractCard card = null;
        MConventionalDict cardTypeDict = conventionalDictClient.getCardType(apiVersion,cardType);
        if (!cardTypeDict.checkIsVirtualCard()) {
            card = abstractPhysicalCardRepository.findOne(id);
        } else {
            card = abstractVirtualCardRepository.findOne(id);
        }
        return card;
    }


    public List<AbstractCard> searchAbstractCard(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String idCardNo = (String) args.get("idCardNo");
        String number = (String)args.get("number");
        String searchType = (String) args.get("type");
        String cardType = (String)args.get("cardType");   //","otherCard
        int rows = (Integer)args.get("rows");
        int page = (Integer)args.get("page");
        String sqlPhysical="from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="from AbstractVirtualCard a where (a.number like :number)";
        if ("bound_card".equals(cardType) && !StringUtils.isEmpty(idCardNo)){
            sqlPhysical += " and (idCardNo=:idCardNo)";
            sqlVirtual += " and (idCardNo=:idCardNo)";
        }else{
            sqlPhysical += " and (idCardNo=null or trim(idCardNo)='')";
            sqlVirtual += " and (idCardNo=null or trim(idCardNo)='')";
        }
        if (!StringUtils.isEmpty(searchType)){
            sqlPhysical += " and (type=:searchType)";
            sqlVirtual += " and (type=:searchType)";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("idCardNo", idCardNo);
            queryVirtual.setParameter("idCardNo", idCardNo);
        }
        if (!StringUtils.isEmpty(searchType)){
            queryPhysical.setParameter("searchType", searchType);
            queryVirtual.setParameter("searchType", searchType);
        }

        List<AbstractCard> cards;
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

        return cards;
    }

    public Integer searchCardInt(Map<String, Object> args ,boolean onlyPhysical) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String idCardNo = (String) args.get("idCardNo");
        String number = (String)args.get("number");
        String searchType = (String) args.get("type");
        String searchIdCardNo = (String)args.get("searchIdCardNo");
        String sqlPhysical="from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="from AbstractVirtualCard a where (a.number like :number)";
        if (!StringUtils.isEmpty(idCardNo)){
            sqlPhysical += " and (idCardNo=:idCardNo)";
            sqlVirtual += " and (idCardNo=:idCardNo)";
        }
        if (!StringUtils.isEmpty(searchType)){
            sqlPhysical += " and (type=:searchType)";
            sqlVirtual += " and (type=:searchType)";
        }
        if (!StringUtils.isEmpty(searchIdCardNo)){
            sqlPhysical += " and (idCardNo=null or trim(idCardNo)='')";
            sqlVirtual += " and (idCardNo=null or trim(idCardNo)='')";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("idCardNo", idCardNo);
            queryVirtual.setParameter("idCardNo", idCardNo);
        }
        if (!StringUtils.isEmpty(searchType)){
            queryPhysical.setParameter("searchType", searchType);
            queryVirtual.setParameter("searchType", searchType);
        }
        List<AbstractCard> cards = queryPhysical.list();
        return cards.size();

    }


    public boolean detachCard(String apiVersion,AbstractCard card) {
        if (StringUtils.isEmpty(card.getIdCardNo())) {
            return true;
        }
        card.setIdCardNo(null);
        return  saveCard(apiVersion,card);
    }

    public boolean isAvailableIdCardNo(String idCardNo){
        return idCardNo != null && idCardNo.length() > 0;
    }

    public boolean attachCardWith(String apiVersion,AbstractCard card, String idCardNo) {
        if (!isAvailableIdCardNo(idCardNo)) {
            throw new IllegalArgumentException("无效人口学索引.");
        }
        card.setIdCardNo(idCardNo);
        return save(apiVersion,card);
    }

    public boolean save(String apiVersion,AbstractCard card){
        if(conventionalDictClient.getCardType(apiVersion,card.getType()).checkIsVirtualCard()){
            AbstractVirtualCard abstractVirtualCard = (AbstractVirtualCard) card;
            abstractVirtualCardRepository.save(abstractVirtualCard);
        }else {
            AbstractPhysicalCard abstractPhysicalCard = (AbstractPhysicalCard)card;
            abstractPhysicalCardRepository.save(abstractPhysicalCard);
        }
        return true;
    }

    public boolean saveCard(String apiVersion,AbstractCard card) {
        if (card.getNumber().length() == 0 || card.getType() == null) {
            throw new CardException("卡信息不全, 无法更新");
        } else if (card.getStatus() == conventionalDictClient.getCardStatus(apiVersion,"Invalid").getValue()) {
            throw new CardException("卡已作废, 无法更新");
        }
        return save(apiVersion,card);
    }
}