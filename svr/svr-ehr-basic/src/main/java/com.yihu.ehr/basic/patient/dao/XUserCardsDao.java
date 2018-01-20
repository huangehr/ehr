package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.UserCards;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XUserCardsDao extends PagingAndSortingRepository<UserCards,Long> {

    Page<UserCards> findByUserId(String userId, Pageable pageable);

    Page<UserCards> findByUserIdAndCardType(String userId, String cardType, Pageable pageable);

    Page<UserCards> findByStatusAndCardType(String status, String cardType, Pageable pageable);

    Page<UserCards> findByStatus(String status, Pageable pageable);

    Page<UserCards> findByCardType(String cardType, Pageable pageable);
}
