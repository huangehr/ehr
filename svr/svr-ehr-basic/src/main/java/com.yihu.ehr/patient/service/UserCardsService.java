package com.yihu.ehr.patient.service;

import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.patient.UserCards;
import com.yihu.ehr.patient.dao.XUserCardsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 卡管理实现类.
 *
 */
@Transactional
@Service
public class UserCardsService {

    @Autowired
    private XUserCardsDao userCardsDao;

    /**
     * 获取个人卡列表
     */
    public ListResult cardList(String userId,String cardType,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<UserCards> cardList = null;
        if(!StringUtils.isEmpty(cardType))
        {
            cardList = userCardsDao.findByUserIdAndCardType(userId,cardType,pageRequest);
        }
        else{
            cardList = userCardsDao.findByUserId(userId,pageRequest);
        }

        if(cardList!=null) {
            re.setDetailModelList(cardList.getContent());
            re.setTotalCount(cardList.getTotalPages());
        }
        return re;
    }

    /**
     * 卡认证申请详情
     */
    public UserCards getCardApply(Long id) throws Exception{
        return userCardsDao.findOne(id);
    }

    /**
     * 卡认证申请新增/修改
     */
    public UserCards cardApply(UserCards card) throws Exception{
        return userCardsDao.save(card);
    }

    /**
     * 卡认证申请删除
     */
    public void cardApplyDelete(Long id) throws Exception{
         userCardsDao.delete(id);
    }


    /**
     * 管理员--卡认证列表
     */
    public ListResult cardApplyListManager(String status,String cardType,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<UserCards> cardList = null;
        if(!StringUtils.isEmpty(status)) {
            if (!StringUtils.isEmpty(cardType)) {
                cardList = userCardsDao.findByStatusAndCardType(status, cardType, pageRequest);
            } else {
                cardList = userCardsDao.findByStatus(status, pageRequest);
            }
        }
        else{
            if (!StringUtils.isEmpty(cardType)) {
                cardList = userCardsDao.findByCardType(cardType, pageRequest);
            } else {
                cardList = userCardsDao.findAll(pageRequest);
            }
        }

        if(cardList!=null) {
            re.setDetailModelList(cardList.getContent());
            re.setTotalCount(cardList.getTotalPages());
        }
        return re;
    }

    /**
     * 管理员--卡认证审核操作
     */
    public Result cardVerifyManager(Long id, String status,String auditor,String auditReason) throws Exception{
        UserCards card = userCardsDao.findOne(id);
        if(card == null)
        {
            return Result.error("该数据不存在！");
        }
        else{
            card.setStatus(status);
            card.setAuditor(auditor);
            card.setAuditDate(new Date());
            card.setAuditReason(auditReason);
            userCardsDao.save(card);

            //hbase操作


            return Result.success("卡认证审核成功！");
        }
    }

    /**
     * 管理员--后台绑卡操作
     */
    public UserCards cardBindManager(UserCards card) throws Exception{
        card = userCardsDao.save(card);

        //hbase操作


        return card;
    }


}