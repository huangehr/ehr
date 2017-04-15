package com.yihu.ehr.patient.service.arapply;

import com.yihu.ehr.model.patient.MedicalCards;
import com.yihu.ehr.patient.dao.XMedicalCardsDao;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 卡管理实现类.
 *
 */
@Transactional
@Service
public class MedicalCardsService extends BaseJpaService<MedicalCards, XMedicalCardsDao> {
    @Autowired
    private XMedicalCardsDao medicalCardsDao;

    /**
     * 就诊卡新增&修改
     */
    public MedicalCards MCardCreate(MedicalCards card) throws Exception{
        return medicalCardsDao.save(card);
    }

    /**
     * 就诊卡删除
     */
    public void MCardDel(Long id) throws Exception{
        medicalCardsDao.delete(id);
    }

    /**
     * 就诊卡详细信息
     */
    public MedicalCards getMCard(Long id) throws Exception{
        return medicalCardsDao.findOne(id);
    }

}