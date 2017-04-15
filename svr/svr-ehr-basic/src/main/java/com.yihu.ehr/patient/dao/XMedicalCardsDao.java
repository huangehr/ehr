package com.yihu.ehr.patient.dao;

import com.yihu.ehr.model.patient.MedicalCards;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XMedicalCardsDao extends PagingAndSortingRepository<MedicalCards,Long> {

}
