package com.yihu.ehr.patient.dao;

import com.yihu.ehr.entity.patient.MedicalCards;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XMedicalCardsDao extends PagingAndSortingRepository<MedicalCards,Long> {

    @Query("select mc from MedicalCards mc where mc.cardNo in (:cardNoStr)")
    List<MedicalCards> getBycardNoStr(@Param("cardNoStr") String[] cardNoStr);


}
