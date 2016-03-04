package com.yihu.ehr.patient.service.card;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.patient.dao.XAbstractPhysicalCardRepository;
import com.yihu.ehr.patient.dao.XAbstractVirtualCardRepository;
import com.yihu.ehr.patient.feign.ConventionalDictClient;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public AbstractCard getCard(String id, String cardType) {
        AbstractCard card = null;
        MConventionalDict cardTypeDict = conventionalDictClient.getCardType(cardType);
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
        String type = (String) args.get("type");
        String cardType = (String)args.get("cardType");   //","otherCard
        int rows = (Integer)args.get("rows");
        int page = (Integer)args.get("page");
        String sqlPhysical="from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="from AbstractVirtualCard a where (a.number like :number)";
        if ("bind_card".equals(type) && !StringUtils.isEmpty(idCardNo)){
            sqlPhysical += " and (idCardNo=:idCardNo)";
            sqlVirtual += " and (idCardNo=:idCardNo)";
        }else{
            sqlPhysical += " and (idCardNo=null or trim(idCardNo)='')";
            sqlVirtual += " and (idCardNo=null or trim(idCardNo)='')";
        }
        if (!StringUtils.isEmpty(cardType)){
            sqlPhysical += " and (cardType=:cardType)";
            sqlVirtual += " and (cardType=:cardType)";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("idCardNo", idCardNo);
            queryVirtual.setParameter("idCardNo", idCardNo);
        }
        if (!StringUtils.isEmpty(cardType)){
            queryPhysical.setParameter("cardType", cardType);
            queryVirtual.setParameter("cardType", cardType);
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
        String type = (String) args.get("type");
        String cardType = (String)args.get("cardType");   //","otherCard
        int rows = (Integer)args.get("rows");
        int page = (Integer)args.get("page");
        String sqlPhysical="from AbstractPhysicalCard a where (a.number like :number)";
        String sqlVirtual="from AbstractVirtualCard a where (a.number like :number)";
        if ("bind_card".equals(type) && !StringUtils.isEmpty(idCardNo)){
            sqlPhysical += " and (idCardNo=:idCardNo)";
            sqlVirtual += " and (idCardNo=:idCardNo)";
        }else{
            sqlPhysical += " and (idCardNo=null or trim(idCardNo)='')";
            sqlVirtual += " and (idCardNo=null or trim(idCardNo)='')";
        }
        if (!StringUtils.isEmpty(cardType)){
            sqlPhysical += " and (cardType=:cardType)";
            sqlVirtual += " and (cardType=:cardType)";
        }
        Query queryPhysical = session.createQuery(sqlPhysical);
        Query queryVirtual = session.createQuery(sqlVirtual);
        queryPhysical.setString("number", "%"+number+"%");
        queryVirtual.setString("number", "%"+number+"%");
        if (!StringUtils.isEmpty(idCardNo)) {
            queryPhysical.setParameter("idCardNo", idCardNo);
            queryVirtual.setParameter("idCardNo", idCardNo);
        }
        if (!StringUtils.isEmpty(cardType)){
            queryPhysical.setParameter("cardType", cardType);
            queryVirtual.setParameter("cardType", cardType);
        }
        List<AbstractCard> physicalCards = queryPhysical.list();
        List<AbstractCard> virtualCards = queryVirtual.list();
        return physicalCards.size()+virtualCards.size();

    }


    public boolean detachCard(AbstractCard card) {
        if (StringUtils.isEmpty(card.getIdCardNo())) {
            return true;
        }
        card.setIdCardNo(null);
        return  saveCard(card);
    }

    public boolean isAvailableIdCardNo(String idCardNo){
        return idCardNo != null && idCardNo.length() > 0;
    }

    public boolean attachCardWith(AbstractCard card, String idCardNo) {
        if (!isAvailableIdCardNo(idCardNo)) {
            throw new IllegalArgumentException("无效人口学索引.");
        }
        card.setIdCardNo(idCardNo);
        return save(card);
    }

    public boolean save(AbstractCard card){
        if(conventionalDictClient.getCardType(card.getCardType()).checkIsVirtualCard()){
            AbstractVirtualCard abstractVirtualCard = (AbstractVirtualCard) card;
            abstractVirtualCardRepository.save(abstractVirtualCard);
        }else {
            AbstractPhysicalCard abstractPhysicalCard = (AbstractPhysicalCard)card;
            abstractPhysicalCardRepository.save(abstractPhysicalCard);
        }
        return true;
    }

    public boolean saveCard(AbstractCard card) {
        if (card.getNumber().length() == 0 || card.getCardType() == null) {
            throw new ApiException(ErrorCode.RepeatCode,"卡信息不全, 无法更新");
        } else if (card.getStatus() == conventionalDictClient.getCardStatus("Invalid").getValue()) {
            throw new ApiException(ErrorCode.CardIsToVoid,"卡已作废, 无法更新");
        }
        return save(card);
    }
}