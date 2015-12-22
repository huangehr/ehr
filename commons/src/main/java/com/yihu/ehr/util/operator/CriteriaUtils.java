/******************************************************************************/
/* SYSTEM     : Commons                                                       */
/*                                                                            */
/* SUBSYSTEM  : Util                                                          */
/******************************************************************************/
package com.yihu.ehr.util.operator;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Hibernate Criteria utils
 *
 * @since 1.0.6
 */
public class CriteriaUtils {

    /**
     * add criterion
     *
     * @param criteria
     * @param criterion
     * @return
     */
    public static Criteria add(Criteria criteria, Criterion criterion) {

        criteria.add(criterion);

        return criteria;
    }

    /**
     * add criterion
     *
     * @param criteria
     * @param criterion
     * @param parameter
     * @return
     */
    public static Criteria add(Criteria criteria, Criterion criterion, String parameter) {

        if (!StringUtil.isEmpty(parameter)) {
            criteria.add(criterion);
        }

        return criteria;
    }

    /**
     * add criterion
     *
     * @param criteria
     * @param criterion
     * @param parameter
     * @return
     */
    public static Criteria add(Criteria criteria, Criterion criterion, Integer parameter) {

        if (parameter != null) {
            criteria.add(criterion);
        }

        return criteria;
    }

    /**
     * add criterion
     *
     * @param criteria
     * @param criterion
     * @param parameter
     * @return
     */
    public static Criteria add(Criteria criteria, Criterion criterion, Date parameter) {

        if (parameter != null) {
            criteria.add(criterion);
        }

        return criteria;
    }

    /**
     * add criterion
     *
     * @param criteria
     * @param criterion
     * @param parameter
     * @return
     */
    public static Criteria add(Criteria criteria, Criterion criterion, BigDecimal parameter) {

        if (parameter != null) {
            criteria.add(criterion);
        }

        return criteria;
    }

    /**
     * add Order
     *
     * @param criteria
     * @param order
     * @return
     */
    public static Criteria addOrder(Criteria criteria, Order order) {

        criteria.addOrder(order);

        return criteria;
    }
}
