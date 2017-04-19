package com.yihu.ehr.patient.dao;

import com.yihu.ehr.model.patient.MedicalCards;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XMedicalCardsDao extends PagingAndSortingRepository<MedicalCards,Long> {

    @Query("from MedicalCards where cardNo in (?1)")
    List<MedicalCards> getBycardNoStr(String cardNoStr);


}
